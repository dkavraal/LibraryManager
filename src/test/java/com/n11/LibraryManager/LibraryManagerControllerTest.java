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

import net.minidev.json.JSONObject;

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
	public void service_web_application_version_service_should_return_ok()
			throws Exception {
		// a primitive service for also test purposes - DK
		mockMvc.perform(get("/version").accept(MediaType.TEXT_HTML))
					.andExpect(status().isOk());
	}
	
	@Test
	public void method_wrong_captcha_should_disallow_new_book_record() throws Exception {
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
	public void service_get_books_should_return_list() throws Exception {
		//json response bookListResultShouldContain  "bookList"; 
		//   and a book with a title
		mockMvc.perform(get("/books").accept(APPLICATION_JSON_UTF8))
					.andExpect(status().isOk())
					.andExpect(content().contentType(APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.R[0].bookList").isArray())
					.andExpect(jsonPath("$.R[0].bookList[0].title").exists());
	}
	
	@Test
	public void method_get_book_list_test() {
		libManService.setRepository(bookRepository);
		libManService.getBookList();
		verify(libManService, times(1)).getBookList();
		verify(bookRepository, times(1)).findAll();
	}
	
	@Test
	public void method_add_new_book_should_call_db_record() throws Exception {
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
	
	@Test
	public void service_addNewBook_test() throws Exception {
		JSONObject jsonNewBook = new JSONObject();
		jsonNewBook.put("id", "@id");
		jsonNewBook.put("author", "a new writer");
		jsonNewBook.put("title", "a new subject");
		jsonNewBook.put("verify", "captcha-text");
		
		mockMvc.perform(post("/addNewBook")
							.content(jsonNewBook.toJSONString())
							.contentType(APPLICATION_JSON_UTF8)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.RESP_CODE").value("INSECURE"));
	}
	
	@Test
	public void method_findABook_test() {
		libManService.setRepository(bookRepository);
		Book b = libManService.findABook("1");
		verify(bookRepository).findOne(eq("1"));
	}
	
	@Test
	public void service_updating_a_book_test() throws Exception {
		JSONObject jsonNewBook = new JSONObject();
		jsonNewBook.put("id", "@id");
		jsonNewBook.put("author", "updated writer");
		jsonNewBook.put("title", "updated subject");
		
		mockMvc.perform(post("/updateBook")
							.content(jsonNewBook.toJSONString())
							.contentType(APPLICATION_JSON_UTF8)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentType(APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.RESP_CODE").value("OK"))
					.andExpect(jsonPath("$.R").isArray())
					.andExpect(jsonPath("$.R[0].id").exists());
	}
	
	@Test
	public void method_update_book_test() {
		Book updateReqBook = new Book("@+id", "edited author", "edited title");
		libManService.setRepository(bookRepository);
		
		libManService.updateBook(httpReq, updateReqBook);
		verify(libManService, times(1)).updateTheBook(eq(updateReqBook));
	}
	
}
