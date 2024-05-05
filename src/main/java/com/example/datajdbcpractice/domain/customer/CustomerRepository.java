package com.example.datajdbcpractice.domain.customer;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @Query("select customer_id, first_name, date_of_birth from customer where first_name like concat('%', :name, '%')")
    List<Customer> findByName(@Param("name") String name);
}
