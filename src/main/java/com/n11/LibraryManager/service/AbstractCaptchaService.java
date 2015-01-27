package com.n11.LibraryManager.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractCaptchaService extends HttpServlet {
	private static final long serialVersionUID = -1964350119030502903L;

	/**
	 * Verify the image text fits the user input
	 * 
	 * @param req for managing session values and etc
	 * @param captchaResponse user input value
	 * @return
	 */
	public boolean validateResponse(HttpServletRequest req, String captchaResponse) {
		return false;
	}
	
	/**
	 * Responds with the captcha image 
	 *
	 * also @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, java.io.IOException {
		
	}
}
