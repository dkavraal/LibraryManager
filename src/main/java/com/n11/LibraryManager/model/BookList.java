package com.n11.LibraryManager.model;

import java.util.List;

public class BookList {
	Book[] bookList = null;

	public Book[] getBookList() {
		return bookList;
	}

	public void setBookList(Book[] bookList) {
		this.bookList = bookList;
	}
	
	public void setBookList(List<Book> bookList) throws Exception {
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
		this.bookList = resultSet;
	}
}
