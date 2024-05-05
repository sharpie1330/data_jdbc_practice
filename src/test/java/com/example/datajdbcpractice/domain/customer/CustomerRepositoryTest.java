package com.example.datajdbcpractice.domain.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void createSimpleCustomer() {
        Customer customer = Customer.builder()
                .dob(LocalDate.of(1904, 5, 14))
                .firstName("Albert")
                .build();

        Customer saved = customerRepository.save(customer);
        assertThat(saved.getId()).isNotNull();

        saved.updateFirstName("Hans Albert");
        customerRepository.save(saved);

        Optional<Customer> reloaded = customerRepository.findById(saved.getId());

        assertThat(reloaded).isNotEmpty();
        assertThat(reloaded.get().getFirstName()).isEqualTo("Hans Albert");
    }

    @Test
    public void findByName() {
        Customer customer = Customer.builder()
                .dob(LocalDate.of(1904, 5, 14))
                .firstName("Albert")
                .build();

        Customer saved = customerRepository.save(customer);
        assertThat(saved.getId()).isNotNull();

        Customer customerA = Customer.builder()
                .dob(LocalDate.of(1904, 5, 14))
                .firstName("Bertram")
                .build();
        customerRepository.save(customerA);

        Customer customerB = Customer.builder()
                .dob(LocalDate.of(1904, 5, 14))
                .firstName("Beth")
                .build();
        customerRepository.save(customerB);

        assertThat(customerRepository.findByName("bert")).hasSize(2);
    }
}
