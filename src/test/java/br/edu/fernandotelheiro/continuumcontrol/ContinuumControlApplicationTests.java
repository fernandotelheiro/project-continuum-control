package br.edu.fernandotelheiro.continuumcontrol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ContinuumControlApplicationTests {

    @Test
    void contextLoads() {
    }
}