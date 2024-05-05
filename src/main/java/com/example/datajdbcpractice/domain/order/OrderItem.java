package com.example.datajdbcpractice.domain.order;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("order_item")
public class OrderItem {
    private int quantity;
    private String product;
}
