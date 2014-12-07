package com.n11.LibraryManager;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.n11.LibraryManager.model.Book;
import com.n11.LibraryManager.model.BookList;
import com.n11.LibraryManager.utility.ServiceResponse;
import com.n11.LibraryManager.utility.ServiceResponse.T_RESP_CODE;

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
		model.addAttribute("version", version());
		return "hello";
	}
	
	@RequestMapping(value="/version", method=RequestMethod.GET)
	public String getVersion(Model model) {
		logger.debug("requested getVersion service");
		model.addAttribute("key", "version");
		model.addAttribute("value", version());
		model.addAttribute("version", version());
		return "hello";
	}
	
	@RequestMapping(value="/books", method=RequestMethod.GET)
	public @ResponseBody ServiceResponse getBookList(Model model) {
		logger.debug("requested book list");
		
		BookList bookList = new BookList();
		ArrayList<Book> incomingBooks = new ArrayList<Book>();
		Book b1 = new Book("acar baltas", "hep boyle anlatın");
		Book b2 = new Book("canavar necmi", "cok guzel top oynarım");
		incomingBooks.add(b1);
		incomingBooks.add(b2);
		try {
			bookList.setBookList(incomingBooks);
		} catch (Exception e) {
			return new ServiceResponse(T_RESP_CODE.NOK, "COULDNOT READ BOOKLIST");
		}
		
		return new ServiceResponse(T_RESP_CODE.OK, bookList);
	}
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String getLanding(Model model) {
		logger.debug("requested the main page");
		
		model.addAttribute("version", version());
		return "index";
	}
	
	protected String tryThis() {
		return "OK_ME";
	}
	
	public static String version() {
		return "0.0.1a";
	}
	
}
