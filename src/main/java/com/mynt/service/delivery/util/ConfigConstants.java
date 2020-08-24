package com.mynt.service.delivery.util;

public class ConfigConstants {
	public static int MAX_WEIGHT = 50;
	public static int MIN_WEIGHT = 10;
	public static int MIN_VOLUME = 1500;
	public static int MAX_VOLUME = 2500;
	
	public static enum Status{
		SUCCESS,
		REJECT,
		FAILED
	}
	
	public static enum  Rules{
		REJECT,
		HEAVY_PARCEL,
		SMALL_PARCEL,
		MEDIUM_PARCEL,
		LARGE_PARCEL;
		
		public int getPriority() {
			switch(this) {
				case REJECT:return 1;
				case HEAVY_PARCEL: return 2;
				case SMALL_PARCEL: return 3;
				case MEDIUM_PARCEL: return 4;
				case LARGE_PARCEL: return 5;
				default: return 0;
			}
		}
	}
}
