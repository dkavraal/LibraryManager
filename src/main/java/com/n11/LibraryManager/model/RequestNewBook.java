package com.n11.LibraryManager.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class RequestNewBook extends Book {
	@JsonProperty("verify")
	public String verify = "";
	
	@Override
	public String toString() {
		return String.format("NewBookRequest. Captcha: [%s] Title: [%s] Author: [%s]", verify, getTitle(), getAuthor());
	}
}
