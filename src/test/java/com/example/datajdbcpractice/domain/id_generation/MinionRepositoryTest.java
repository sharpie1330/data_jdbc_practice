package com.example.datajdbcpractice.domain.id_generation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MinionRepositoryTest {

    @Autowired
    private MinionRepository minionRepository;

    @Test
    void minionTest() {
        Minion before = new Minion("Bob");
        assertThat(before.getId()).isNull();

        Minion after = minionRepository.save(before);

        assertThat(after.getId()).isNotNull();
    }
}