package com.example.datajdbcpractice.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@AllArgsConstructor
@Table("book_author")
public class AuthorRef {
    private Long author;
}
