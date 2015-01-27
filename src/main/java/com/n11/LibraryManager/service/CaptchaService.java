package com.n11.LibraryManager.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.octo.captcha.module.servlet.image.SimpleImageCaptchaServlet;

public class CaptchaService extends AbstractCaptchaService {
	private static final long serialVersionUID = 5968537473140934185L;
	private SimpleImageCaptchaServlet octoCaptcha = null;
	
	public CaptchaService() {
		octoCaptcha = new SimpleImageCaptchaServlet();
	}
	
	/**
	 * Verifies the given captcha input
	 * 
	 * @param req Http Request object
	 * @param captchaResponse response of the user
	 * @return true if user input matches with the text on the image which user has just seen
	 */
	public boolean validateResponse(HttpServletRequest req, String captchaResponse) {
		return SimpleImageCaptchaServlet.validateResponse(req, captchaResponse);
	}
	
	/**
	 * Responds with the captcha image 
	 *
	 * also @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * also @see com.octo.captcha.module.servlet.image.SimpleImageCaptchaServlet#doGet(javax.servlet.http.HttpServletRequest httpServletRequest, javax.servlet.http.HttpServletResponse httpServletResponse)
	 */
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, java.io.IOException {
		octoCaptcha.service(httpServletRequest, httpServletResponse);
	}
}
