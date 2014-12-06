package com.n11.LibraryManager;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LibraryManagerController {
	protected static Logger logger = Logger.getLogger(LibraryManagerController.class.getName());
	static {
		logger.info(String.format("Library Manager %s", version()));
	}

	@RequestMapping(value="/hello", method=RequestMethod.GET)
	public String sayHello(
			@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		logger.debug(String.format("requested Hello World service [value=%s]", name));
		model.addAttribute("key", "Hello");
		model.addAttribute("value", name);
		return "index";
	}
	
	@RequestMapping(value="/version", method=RequestMethod.GET)
	public String getVersion(Model model) {
		logger.debug("requested getVersion service");
		model.addAttribute("key", "version");
		model.addAttribute("value", version());
		return "index";
	}
	
	protected String tryThis() {
		return "OK_ME";
	}
	
	public static String version() {
		return "0.0.1a";
	}
	
}
