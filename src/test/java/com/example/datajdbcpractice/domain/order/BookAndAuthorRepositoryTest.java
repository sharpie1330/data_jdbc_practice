package com.example.datajdbcpractice.domain.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BookAndAuthorRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void booksAndAuthors() {

        Author author = Author.builder()
                .name("Greg L. Turnquist")
                .build();

        author = authorRepository.save(author);

        Book book = Book.builder()
                .title("Spring Boot")
                .build();

        book.addAuthor(author);

        bookRepository.save(book);

        bookRepository.deleteAll();

        assertThat(authorRepository.count()).isEqualTo(1);
    }
}
