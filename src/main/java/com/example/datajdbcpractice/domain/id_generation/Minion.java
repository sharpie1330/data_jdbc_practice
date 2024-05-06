package com.example.datajdbcpractice.domain.id_generation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
public class Minion {
    @Id
    @Column("minion_id")
    private Long id;
    private String name;

    Minion(String name) {
        this.name = name;
    }
}
