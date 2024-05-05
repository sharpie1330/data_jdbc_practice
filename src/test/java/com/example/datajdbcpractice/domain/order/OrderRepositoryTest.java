package com.example.datajdbcpractice.domain.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void createUpdateDeleteOrder() {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.addItem(4, "Captain Future Comet Lego set");
        purchaseOrder.addItem(2, "Cute blue angler fish plush toy");

        PurchaseOrder saved = orderRepository.save(purchaseOrder);

        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(orderRepository.countItems()).isEqualTo(2);

        orderRepository.delete(saved);

        assertThat(orderRepository.count()).isEqualTo(0);
        assertThat(orderRepository.countItems()).isEqualTo(0);
    }
}