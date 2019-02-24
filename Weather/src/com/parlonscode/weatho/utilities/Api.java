package com.parlonscode.weatho.utilities;

public class Api {
	private static final String FORECAST_API_BASE_URL = "https://api.darksky.net/forecast/";
	private static final String FORECAST_API_KEY = "335ee5743ce3ff9188e08ab56e27969a";

	public static String getforecastUrl(double latitude, double longitude) {
		return FORECAST_API_BASE_URL + FORECAST_API_KEY + "/" + latitude + "," + longitude+"?units=si&lang=fr";
	}
}
