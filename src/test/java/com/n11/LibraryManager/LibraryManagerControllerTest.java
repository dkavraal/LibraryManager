package com.n11.LibraryManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.*;

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
	public void new_book_item_info_should_be_recorded_into_db()
			throws Exception {

	}

	@Test
	public void get_books_should_return_list() throws Exception {
		//json response bookListResultShouldContain  "bookList"; 
		this.mockMvc.perform(get("/books").accept(APPLICATION_JSON_UTF8))
					.andExpect(status().isOk())
					.andExpect(content().contentType(APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.R[0].bookList").isArray())
					.andExpect(jsonPath("$.R[0].bookList[0].title").exists());
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
