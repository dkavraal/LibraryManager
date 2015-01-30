package com.n11.LibraryManager.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

public class Book {
	@Id
	private String uuid = null;
	
	@NotNull
	@Size(min=2, max=50)
	private String author = null;
	@NotNull
	@Size(min=2, max=70)
	private String title = null;

	public Book() { }
	
	public Book(String author, String title) {
		this.author = author;
		this.title = title;
	}
		
	public Book(String id, String author, String title) {
		this.uuid = id;
		this.author = author;
		this.title = title;
	}
		
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getId() {
		return this.uuid;
	}
	
	public void setId(String id) {
		this.uuid = id;
	}
	

	@Override
	public String toString() {
		return String.format("[%s] %s by %s", uuid, title, author);
	}

}
