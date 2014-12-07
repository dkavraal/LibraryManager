package com.n11.LibraryManager.utility;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ServiceResponse {
	public enum T_RESP_CODE {
		OK, NOK, TIMEOUT, NOTAUTH, DBERROR, TEMPUNAVAIL, INSECURE
	}
	
	@JsonProperty("RESP_CODE")	private T_RESP_CODE RESP_CODE;
	@JsonProperty("R")			private Object[] RESP;
	
	public ServiceResponse(T_RESP_CODE responseCode, Object response) {
		this.RESP_CODE = responseCode;
		this.RESP = new Object[] {response};
	}
	
	public ServiceResponse(T_RESP_CODE responseCode, Object[] response) {
		this.RESP_CODE = responseCode;
		this.RESP = response.clone();
	}
	
	public ServiceResponse(T_RESP_CODE responseCode, List<Object> response) {
		Object[] responseObjectsList = new Object[response.size()];
		int index = 0;
		for (Object r : response) {
			responseObjectsList[index] = r; 
			index++;
		}
		this.RESP = responseObjectsList;
	}
	
	@JsonProperty("R")
	public Object[] getResponseObjects() {
		return RESP;
	}
	
	@JsonProperty("RESP_CODE")
	public T_RESP_CODE getResponseType() {
		return RESP_CODE;
	}
}
