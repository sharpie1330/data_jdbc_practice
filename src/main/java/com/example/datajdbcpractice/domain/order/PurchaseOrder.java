package com.example.datajdbcpractice.domain.order;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("purchase_order")
public class PurchaseOrder {
    @Column("purchase_order_id")
    private @Id Long id;
    private String shippingAddress;

    @Builder.Default
    private Set<OrderItem> items = new HashSet<>();

    public void addItem(int quantity, String product) {
        items.add(createOrderItem(quantity, product));
    }

    private OrderItem createOrderItem(int quantity, String product) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .build();
    }
}