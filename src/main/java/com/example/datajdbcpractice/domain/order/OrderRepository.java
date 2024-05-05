package com.example.datajdbcpractice.domain.order;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<PurchaseOrder, Long> {

    @Query("select count(*) from order_item")
    int countItems();
}
