package br.edu.fernandotelheiro.continuumcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableSpringDataWebSupport(
        pageSerializationMode =
                EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
public class ContinuumControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                ContinuumControlApplication.class,
                args
        );
    }
}