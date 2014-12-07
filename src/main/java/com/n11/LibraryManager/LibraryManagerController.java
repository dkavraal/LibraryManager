package com.n11.LibraryManager;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.n11.LibraryManager.data.IBookRepository;
import com.n11.LibraryManager.model.Book;
import com.n11.LibraryManager.model.BookList;
import com.n11.LibraryManager.model.RequestNewBook;
import com.n11.LibraryManager.utility.ServiceResponse;
import com.n11.LibraryManager.utility.ServiceResponse.T_RESP_CODE;
import com.octo.captcha.module.servlet.image.SimpleImageCaptchaServlet;

@Controller
public class LibraryManagerController {
	protected static Logger logger = Logger.getLogger(LibraryManagerController.class.getName());
	static {
		logger.info(String.format("Library Manager %s", version()));
	}

	@Autowired
	private IBookRepository repository;

	@RequestMapping(value="/hello", method=RequestMethod.GET)
	public String sayHello(
			@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		// hello world service
		logger.debug(String.format("requested Hello World service [value=%s]", name));
		model.addAttribute("key", "Hello");
		model.addAttribute("value", name);
		model.addAttribute("version", version());
		return "hello";
	}
	
	@RequestMapping(value="/version", method=RequestMethod.GET)
	public String getVersion(Model model) {
		// app version -- uses hello world view
		logger.debug("requested getVersion service");
		model.addAttribute("key", "version");
		model.addAttribute("value", version());
		model.addAttribute("version", version());
		return "hello";
	}
	
	@RequestMapping(value="/books", method=RequestMethod.GET)
	public @ResponseBody ServiceResponse getBookList(Model model) {
		logger.debug("requested book list");
		
		try {
			return new ServiceResponse(T_RESP_CODE.OK, getBookList());
		} catch (Exception e) {
			return new ServiceResponse(T_RESP_CODE.NOK, "COULDNOT READ BOOKLIST");
		}
		
		
	}
	
	@RequestMapping(value="/addNewBook", method=RequestMethod.POST)
	public @ResponseBody ServiceResponse addNewBook(HttpServletRequest request,
			@RequestBody RequestNewBook newBook,
			Model model) {
		logger.debug(String.format("requested addNewBook => %s", newBook));
		
		try {
			// check captcha test
			String userCaptchaResponse = newBook.verify.trim();
			boolean captchaPassed = SimpleImageCaptchaServlet.validateResponse(request, userCaptchaResponse);
			if (!captchaPassed) {
				logger.info(String.format("Failed captcha. IP:[%s]", request.getRemoteAddr()));
				return new ServiceResponse(T_RESP_CODE.INSECURE, "Check http://en.wikipedia.org/wiki/Chinese_room");
			}
			
			// get the posted data
			String title = newBook.getTitle().trim();
			String author = newBook.getAuthor().trim();
			Book theNewBook = new Book(title, author);
			
			// send to db
			theNewBook = recordNewBook(theNewBook);
			
			// reply back to user
			if (theNewBook != null) {
				return new ServiceResponse(T_RESP_CODE.OK, theNewBook);
			} else {
				return new ServiceResponse(T_RESP_CODE.DBERROR, "Couldnot save the book");
			}
		} catch (Exception e) {
			logger.error("Failed adding a new book into db.", e);
			return new ServiceResponse(T_RESP_CODE.NOK, "Sth is very wrong");
		}
	}
	
	protected Book recordNewBook(Book book) {
		try {
			return repository.save(book);
		} catch (Exception e) {
			logger.error("Couldnot write book into DB", e);
			return null;
		}
	}
	
	protected BookList getBookList() {
		try {
			BookList resultSet = new BookList(repository.findAll());
			return resultSet;
		} catch (Exception e) {
			logger.error("Couldnot write book into DB", e);
			return null;
		}
	}

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String getLanding(Model model) {
		logger.debug("requested the main page");
		
		model.addAttribute("version", version());
		return "index";
	}
	
	public static String version() {
		return "0.0.1a";
	}
	
}
