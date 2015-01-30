package com.n11.LibraryManager.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonProperty;

public class RequestNewBook extends Book {
	@JsonProperty("verify")
	@NotNull
	@Size(min=2, max=20)
	public String verify = "";
	
	@Override
	public String toString() {
		return String.format("NewBookRequest. Captcha: [%s] Title: [%s] Author: [%s]", verify, getTitle(), getAuthor());
	}
}
