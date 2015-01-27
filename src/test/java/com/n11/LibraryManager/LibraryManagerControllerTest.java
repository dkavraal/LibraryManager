package com.n11.LibraryManager;


import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.n11.LibraryManager.data.IBookRepository;
import com.n11.LibraryManager.model.Book;
import com.n11.LibraryManager.model.RequestNewBook;
import com.n11.LibraryManager.service.CaptchaService;
import com.n11.LibraryManager.service.LibraryManagerController;
import com.n11.LibraryManager.service.ServiceResponse;
import com.n11.LibraryManager.service.ServiceResponse.T_RESP_CODE;


/**
 * @author 194091
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-servlet-context.xml")
public class LibraryManagerControllerTest {
	private static final String TEST_DB_DB = "local";
	private static final String TEST_DB_COLLECTION = "book";
	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
	        MediaType.APPLICATION_JSON.getType(),
	        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	private LibraryManagerController libManService;
	private static Mongo mongo;
	private static DB db;
	private static DBCollection dbCollection;
	
	private CaptchaService imgCaptchaService;
	private IBookRepository bookRepository;
	private HttpServletRequest httpReq;
	private HttpServletResponse httpResp;
	
	@BeforeClass
	public static void setupDB() {
		mongo = mock(Mongo.class);
		db = mock(DB.class);
		dbCollection = mock(DBCollection.class);

		when(mongo.getDB(TEST_DB_DB)).thenReturn(db);
		when(db.getCollection(TEST_DB_COLLECTION)).thenReturn(dbCollection);
	}
	
	
	@Before
	public void setup() {
		mockMvc = webAppContextSetup(this.wac).build();
		httpResp = mock(HttpServletResponse.class);
		httpReq = mock(HttpServletRequest.class);
		
		libManService = spy(new LibraryManagerController());
		imgCaptchaService = mock(CaptchaService.class);
		bookRepository = mock(IBookRepository.class);
	}
	
	@Test
	public void web_application_version_service_should_return_ok()
			throws Exception {
		// a primitive service for also test purposes - DK
		this.mockMvc.perform(get("/version").accept(MediaType.TEXT_HTML))
					.andExpect(status().isOk());
	}

	@Test
	public void new_book_item_info_should_be_recorded_into_db() throws Exception {
		
	}
	
	@Test
	public void wrong_captcha_should_disallow_new_book_record() throws Exception {
		doReturn(true).when(imgCaptchaService).validateResponse(eq(httpReq), anyString());
		
		RequestNewBook newBook = new RequestNewBook();
		newBook.setAuthor("new1 author");
		newBook.setTitle("new1 title");
		newBook.verify = "@@@@@@@@@@@ it-mustnt-pass @@@@@@@@@@@@@@";
		
		ServiceResponse resp = libManService.addNewBook(httpReq, newBook);
		verify(libManService).addNewBook(eq(httpReq), eq(newBook));
		verify(libManService, never()).recordNewBook(eq(newBook));

		assertTrue(resp.getResponseType() == T_RESP_CODE.INSECURE);
	}

	@Test
	public void get_books_should_return_list() throws Exception {
		//json response bookListResultShouldContain  "bookList"; 
		//   and a book with a title
		//   !! make sure insert tested before this
		
		this.mockMvc.perform(get("/books").accept(APPLICATION_JSON_UTF8))
					.andExpect(status().isOk())
					.andExpect(content().contentType(APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.R[0].bookList").isArray())
					.andExpect(jsonPath("$.R[0].bookList[0].title").exists());
	}
	
	@Test
	public void add_new_book_should_record_the_book() throws Exception {
		doReturn(true).when(imgCaptchaService).validateResponse(eq(httpReq), anyString());
		libManService.setCaptchaService(imgCaptchaService);
		libManService.setRepository(bookRepository);
		
		RequestNewBook newBook = new RequestNewBook();
		newBook.setAuthor("new1 author");
		newBook.setTitle("new1 title");
		newBook.verify = "@@@@@@@@@@@ it-should-pass @@@@@@@@@@@@@@";
		
		ServiceResponse resp = libManService.addNewBook(httpReq, newBook);
		
		verify(libManService).addNewBook(eq(httpReq), eq(newBook));
		
		ArgumentCaptor<Book> newBookInstance = ArgumentCaptor.forClass(Book.class);
		
		verify(libManService, times(1)).recordNewBook(newBookInstance.capture());
		assertEquals(newBookInstance.getValue().getAuthor(), newBook.getAuthor());
		assertEquals(newBookInstance.getValue().getTitle(), newBook.getTitle());
		
		verify(bookRepository, times(1)).save(newBookInstance.capture());
		assertEquals(newBookInstance.getValue().getAuthor(), newBook.getAuthor());
		assertEquals(newBookInstance.getValue().getTitle(), newBook.getTitle());
		
		resp.getResponseType().equals(ServiceResponse.T_RESP_CODE.OK);
	}
	
}
