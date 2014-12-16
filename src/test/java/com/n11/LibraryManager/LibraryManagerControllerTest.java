package com.n11.LibraryManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.n11.LibraryManager.model.Book;
import com.n11.LibraryManager.service.LibraryManagerController;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-servlet-context.xml")
public class LibraryManagerControllerTest {
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
	        MediaType.APPLICATION_JSON.getType(),
	        MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	private LibraryManagerController libman;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.libman = new LibraryManagerController();
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
		
	}

	@Test
	public void get_books_should_return_list() throws Exception {
		//json response bookListResultShouldContain  "bookList"; 
		//   and a book with a title
		//   !! be sure there is already a book in db
		this.mockMvc.perform(get("/books").accept(APPLICATION_JSON_UTF8))
					.andExpect(status().isOk())
					.andExpect(content().contentType(APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.R[0].bookList").isArray())
					.andExpect(jsonPath("$.R[0].bookList[0].title").exists());
	}
	
	@Test
	public void add_new_book_should_record_the_book() throws Exception {
		// this is the actual method tested that should record into db
		/*
		Book insertedBook = libman.recordNewBook(new Book("YYYY", "TTTT"));
		
		insertedBook.getId();
		
		Mongo mongo = PowerMockito.mock(Mongo.class);
		DB db = PowerMockito.mock(DB.class);
		DBCollection dbCollection = PowerMockito.mock(DBCollection.class);

		PowerMockito.when(mongo.getDB("foo")).thenReturn(db);
		PowerMockito.when(db.getCollection("bar")).thenReturn(dbCollection);

		MyService svc = new MyService(mongo); // Use some kind of dependency injection
		svc.getObjectById(1);
		
		PowerMockito.verify(dbCollection).findOne(new BasicDBObject("_id", 1));
		*/
	}
	
	
	
	/*
	 * @Test public void testSearchProduct() throws Exception { String keyword =
	 * "Very Nice Shoes"; this.mockMvc
	 * .perform(get("/product/search").param("q",
	 * keyword).accept(MediaType.APPLICATION_JSON)) .andExpect(status().isOk())
	 * .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	 * .andExpect(jsonPath("$.name").value(keyword)); }
	 */
}
