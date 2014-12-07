package com.n11.LibraryManager.model;

import java.util.List;

import org.apache.commons.collections.IteratorUtils;

public class BookList {
	Book[] bookList = null;

	public Book[] getBookList() {
		return bookList;
	}

	public BookList(final Book[] bookList) {
		this.bookList = bookList;
	}
	
	public BookList(List<Book> bookList) throws Exception {
		this.bookList = getBookList(bookList);
	}
	
	public BookList(Iterable<Book> bookList) throws Exception {
		@SuppressWarnings("unchecked")
		List<Book> listOfBooks = IteratorUtils.toList(bookList.iterator());
		this.bookList = getBookList(listOfBooks);
	}
	
	public static Book[] getBookList(List<Book> bookList) throws Exception {
		Book[] resultSet = new Book[bookList.size()];
		int count = 0;
		for (Book b : bookList) {
			if (resultSet.length > count) {
				resultSet[count] = b;
				count++;
			} else {
				throw new Exception("Book List size misleads.");
			}
		}
		return resultSet;
	}
}
