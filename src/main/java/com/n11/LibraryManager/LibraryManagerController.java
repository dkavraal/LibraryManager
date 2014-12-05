package com.n11.LibraryManager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LibraryManagerController {

	@RequestMapping(value = "/helloMa", method = RequestMethod.GET)
	public String printHello(ModelMap model) {
		model.addAttribute("name", "Hello Spring MVC Framework!");
		return "index";
	}

	@RequestMapping("/hello")
	public String hello(
			@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);
		return "index";
	}
}
