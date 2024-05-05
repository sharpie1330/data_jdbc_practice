package com.example.datajdbcpractice.domain.customer;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "customer")
public class Customer {
    @Id
    @Column(value = "customer_id")
    private Long id;
    private String firstName;
    @Column(value = "date_of_birth")
    private LocalDate dob;

    public void updateFirstName(String firstName) {
        this.firstName = firstName;
    }
}
