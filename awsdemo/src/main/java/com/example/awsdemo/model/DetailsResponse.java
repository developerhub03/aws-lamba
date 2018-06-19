package com.example.awsdemo.model;

import com.google.gson.Gson;

public class DetailsResponse {

	private String message;

	public String toString() {
		final Gson gson = new Gson();
		return gson.toJson(this);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
