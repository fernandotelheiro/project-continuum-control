package br.edu.fernandotelheiro.continuumcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableCaching
@EnableSpringDataWebSupport(
        pageSerializationMode =
                EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
@EnableJpaRepositories(
        basePackages =
                "br.edu.fernandotelheiro.continuumcontrol.repository"
)
@EnableMongoRepositories(
        basePackages =
                "br.edu.fernandotelheiro.continuumcontrol.historico"
)
public class ContinuumControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                ContinuumControlApplication.class,
                args
        );
    }
}