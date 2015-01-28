package com.n11.LibraryManager.service;

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
import com.n11.LibraryManager.service.ServiceResponse.T_RESP_CODE;

@Controller
public class LibraryManagerController {
	protected static Logger logger = Logger.getLogger(LibraryManagerController.class.getName());
	static {
		logger.info(String.format("Library Manager %s", version()));
	}

	@Autowired
	private IBookRepository repository;
	private AbstractCaptchaService captchaService;
	
	
	public LibraryManagerController() {
		captchaService = new CaptchaService();
	}

	/**
	 * Simple hello (demo) service
	 * 
	 * @param name
	 * @param model
	 * @return
	 */
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
	
	/**
	 * Gives the package version
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/version", method=RequestMethod.GET)
	public String getVersion(Model model) {
		// app version -- uses hello world view
		logger.debug("requested getVersion service");
		model.addAttribute("key", "version");
		model.addAttribute("value", version());
		model.addAttribute("version", version());
		return "hello";
	}
	
	/**
	 * Returns the book list from the library as JSON
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/books", method=RequestMethod.GET)
	public @ResponseBody ServiceResponse getBookList(Model model) {
		logger.debug("requested book list");
		
		try {
			return new ServiceResponse(T_RESP_CODE.OK, getBookList());
		} catch (Exception e) {
			return new ServiceResponse(T_RESP_CODE.NOK, "COULDNOT READ BOOKLIST");
		}
		
		
	}
	
	/**
	 * Record a new book into library.
	 * 
	 * @param request
	 * @param newBook
	 * @return
	 */
	@RequestMapping(value="/addNewBook", method=RequestMethod.POST)
	public @ResponseBody ServiceResponse addNewBook(HttpServletRequest request,
			@RequestBody RequestNewBook newBook) {
		logger.debug(String.format("requested addNewBook => %s", newBook));
		
		try {
			// check captcha test
			String userCaptchaResponse = newBook.verify.trim();
			boolean captchaPassed = captchaService.validateResponse(request, userCaptchaResponse);
			if (!captchaPassed) {
				logger.info(String.format("Failed captcha. IP:[%s]", request.getRemoteAddr()));
				return new ServiceResponse(T_RESP_CODE.INSECURE, "Check http://en.wikipedia.org/wiki/Chinese_room");
			}
			
			// get the posted data
			String title = newBook.getTitle().trim();
			String author = newBook.getAuthor().trim();
			Book theNewBook = new Book(author, title);
			
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
	
	/**
	 * Delete a book off the library.
	 * 
	 * @param request
	 * @param newBook
	 * @return
	 */
	@RequestMapping(value="/deleteBook", method=RequestMethod.POST)
	public @ResponseBody ServiceResponse deleteBook(HttpServletRequest request,
			@RequestBody String id) {
		logger.debug(String.format("requested deleteBook => %s", id));
		
		try {
			// get the posted data
			id = id.trim();
			// send off db, if any
			deleteTheBook(id);
			
			return new ServiceResponse(T_RESP_CODE.OK, id);
			
		} catch (Exception e) {
			logger.error("Failed removing from the db.", e);
			return new ServiceResponse(T_RESP_CODE.NOK, "Sth is very wrong");
		}
	}
	
	/**
	 * Delete a book off the library.
	 * 
	 * @param request
	 * @param newBook
	 * @return
	 */
	@RequestMapping(value="/updateBook", method=RequestMethod.POST)
	public @ResponseBody ServiceResponse updateBook(HttpServletRequest request,
			@RequestBody Book updateReqBook) {
		logger.debug(String.format("requested deleteBook => %s", updateReqBook));
		
		try {
			// send to db
			updateReqBook = updateTheBook(updateReqBook);
			
			// reply back to user
			if (updateReqBook != null) {
				return new ServiceResponse(T_RESP_CODE.OK, updateReqBook);
			} else {
				return new ServiceResponse(T_RESP_CODE.DBERROR, "Couldnot update the book");
			}
		} catch (Exception e) {
			logger.error("Failed updating the book on db.", e);
			return new ServiceResponse(T_RESP_CODE.NOK, "Sth is very wrong");
		}
	}
	
	/**
	 * Search for a book
	 * 
	 * @param id
	 * @return
	 */
	public Book findABook(String id) {
		try {
			return repository.findOne(id);
		} catch (Exception e) {
			logger.error("Failed finding the book DB", e);
			return null;
		}
	}
	
	/**
	 * Delete the book
	 * 
	 * @param id book record ID
	 * @return
	 */
	public void deleteTheBook(String id) {
		try {
			repository.delete(id);
		} catch (Exception e) {
			logger.error("Failed deleting the book", e);
		}
	}
	
	/**
	 * Update the book's information
	 * 
	 * @param book up to date book information, if ID doesn't exist in DB, it will be appended into DB
	 * @return
	 */
	public Book updateTheBook(Book book) {
		try {
			return recordNewBook(book);
		} catch (Exception e) {
			logger.error("Failed deleting the book", e);
			return null;
		}
	}
	
	/**
	 * Do record a new book into DB
	 * 
	 * @param book
	 * @return
	 */
	public Book recordNewBook(Book book) {
		try {
			return repository.save(book);
		} catch (Exception e) {
			logger.error("Couldnot write book into DB", e);
			return null;
		}
	}
	
	/**
	 * Get the book list from the library
	 * 
	 * @return
	 */
	public BookList getBookList() {
		try {
			BookList resultSet = new BookList(repository.findAll());
			return resultSet;
		} catch (Exception e) {
			logger.error("Couldnot write book into DB", e);
			return null;
		}
	}
	
	/**
	 * Change/set the image captcha service (most probably you won't need)
	 * 
	 * @param imgCaptchaService
	 */
	public void setCaptchaService(CaptchaService imgCaptchaService) {
		this.captchaService = imgCaptchaService;
	}
	
	/**
	 * Change repository manager (most probably you won't need)
	 * 
	 * @param repos
	 */
	public void setRepository(IBookRepository repos) {
		this.repository = repos;
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
