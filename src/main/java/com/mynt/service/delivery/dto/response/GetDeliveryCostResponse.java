package com.mynt.service.delivery.dto.response;

import com.mynt.service.delivery.dom.Parcel;

public class GetDeliveryCostResponse {

	private Parcel parcel;
	private String status;
	private String errorMessage;
	public Parcel getParcel() {
		return parcel;
	}
	public void setParcel(Parcel parcel) {
		this.parcel = parcel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
