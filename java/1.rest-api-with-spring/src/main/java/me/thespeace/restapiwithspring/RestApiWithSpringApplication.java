package me.thespeace.restapiwithspring;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestApiWithSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApiWithSpringApplication.class, args);
    }

    /**
     * <h2>DTO -> Domain</h2>
     * <p>간편하게 객체로 값 복사를 위해 사용</p>
     * <p>조금 더 성능상 이점을 노린다면 직접 builder 사용</p>
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
