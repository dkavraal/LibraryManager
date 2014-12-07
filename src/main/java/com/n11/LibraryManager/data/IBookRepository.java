package com.n11.LibraryManager.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.n11.LibraryManager.model.Book;

public interface IBookRepository extends CrudRepository<Book, String> {
    Iterable<Book> findAll(Sort sort);
    Page<Book> findAll(Pageable pageable);
    
    //original at: http://docs.spring.io/autorepo/docs/spring-data-mongodb/1.6.1.RELEASE/reference/html/#repositories.core-concepts - DK
}