package com.mynt.service.delivery.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mynt.service.delivery.bean.PricingConfigBean;
import com.mynt.service.delivery.dom.Parcel;
import com.mynt.service.delivery.dom.Voucher;
import com.mynt.service.delivery.dto.request.GetDeliveryCostRequest;
import com.mynt.service.delivery.dto.response.GetDeliveryCostResponse;
import com.mynt.service.delivery.exception.InternalServiceException;
import com.mynt.service.delivery.resource.pricing.Pricing;
import com.mynt.service.delivery.service.VoucherService;
import com.mynt.service.delivery.util.ConfigConstants;
import com.mynt.service.delivery.util.ConfigConstants.Rules;
import com.mynt.service.delivery.util.ConfigConstants.Status;

@RestController
public class DeliveryServiceController {

	@Autowired
	private PricingConfigBean pricingConfig;
	
	@Autowired
	private VoucherService voucherService;
	
	@PostMapping("/delivery/cost")
	public ResponseEntity<GetDeliveryCostResponse> getDeliveryCost(@RequestBody GetDeliveryCostRequest request,
			@RequestParam(name = "voucher", required = false) String voucher) {

		try {
			BigDecimal cost = getCost(request);
			if(null != voucher) {
				System.out.println("VOUCHER = " + voucher);
				Voucher voucherObj = voucherService.getVoucher(voucher);
				cost = new BigDecimal(cost.doubleValue() - Double.valueOf(voucherObj.getDiscount()));
			}
			GetDeliveryCostResponse response = new GetDeliveryCostResponse();

			Parcel parcel = new Parcel();

			parcel.setCost(cost.toString());
			parcel.setWeight(request.getWeight());

			Double volume = Double.valueOf(request.getHeight()) * Double.valueOf(request.getWidth())
					* Double.valueOf(request.getLength());
			parcel.setVolume(volume.toString());

			response.setParcel(parcel);
			response.setErrorMessage("");
			response.setStatus(Status.SUCCESS.name());
			
			return ResponseEntity.status(HttpStatus.OK).body(response);
			
		}catch (InternalServiceException e) {
			
			GetDeliveryCostResponse response = new GetDeliveryCostResponse();
			Parcel parcel = new Parcel();

			parcel.setCost("0");
			parcel.setWeight(request.getWeight());

			Double volume = Double.valueOf(request.getHeight()) * Double.valueOf(request.getWidth())
					* Double.valueOf(request.getLength());
			parcel.setVolume(volume.toString());

			response.setParcel(parcel);

			response.setErrorMessage(e.getMessage());
			response.setStatus(e.getStatus());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			
		}catch (Exception e) {
			e.printStackTrace();
			GetDeliveryCostResponse response = new GetDeliveryCostResponse();
			response.setErrorMessage(e.getMessage());
			response.setStatus(Status.FAILED.name());
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/delivery/config")
	public ResponseEntity<List<Pricing>> getPricingConfig() {
		System.out.println("getConfig called");
		return ResponseEntity.status(HttpStatus.OK).body(pricingConfig.getPricingConfig());
	}

	private BigDecimal getCost(GetDeliveryCostRequest request) throws InternalServiceException , Exception{
		Integer height = Integer.valueOf(request.getHeight());
		Integer length = Integer.valueOf(request.getLength());
		Integer width = Integer.valueOf(request.getWidth());
		Integer weight = Integer.valueOf(request.getWeight());

		Double volume = length.doubleValue() * width.doubleValue() * height.doubleValue();

		int priority = 0;
		BigDecimal unitCost = new BigDecimal(0);
		BigDecimal cost = new BigDecimal(0);

		if (weight > ConfigConstants.MAX_WEIGHT) {
			priority = ConfigConstants.Rules.REJECT.getPriority();
			//cost = new BigDecimal(0);
			throw new InternalServiceException(ConfigConstants.Rules.REJECT.name(), getMessage(priority));
		} else if (weight < ConfigConstants.MAX_WEIGHT && weight > ConfigConstants.MIN_WEIGHT) {
			priority = Rules.HEAVY_PARCEL.getPriority();
			unitCost = getUnitCost(priority);
			cost = BigDecimal.valueOf(Double.valueOf(unitCost.toString()) * Double.valueOf(weight.toString()));
		} else if (weight <= ConfigConstants.MIN_WEIGHT) {
			if (volume < ConfigConstants.MIN_VOLUME) {
				priority = Rules.SMALL_PARCEL.getPriority();
				unitCost = getUnitCost(priority);
				cost = BigDecimal.valueOf(Double.valueOf(unitCost.toString()) * Double.valueOf(volume.toString()));
			} else if (volume < ConfigConstants.MAX_VOLUME && volume > ConfigConstants.MIN_VOLUME) {
				priority = Rules.MEDIUM_PARCEL.getPriority();
				unitCost = getUnitCost(priority);
				cost = BigDecimal.valueOf(Double.valueOf(unitCost.toString()) * Double.valueOf(volume.toString()));
			} else {
				priority = Rules.LARGE_PARCEL.getPriority();
				unitCost = getUnitCost(priority);
				cost = BigDecimal.valueOf(Double.valueOf(unitCost.toString()) * Double.valueOf(volume.toString()));
			}
		}

		return cost;

	}

	private BigDecimal getUnitCost(int priority) throws Exception {
		List<Pricing> pricingList = pricingConfig.getPricingConfig();
		for (Pricing pricing : pricingList) {
			if (pricing.getPriority() == priority) {
				return pricing.getCost();
			}
		}
		return null;

	}
	
	private String getMessage(int priority) {
		List<Pricing> pricingList = pricingConfig.getPricingConfig();
		for (Pricing pricing : pricingList) {
			if (pricing.getPriority() == priority) {
				return pricing.getDescription();
			}
		}
		return null;

		
	}
}
