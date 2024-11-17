package me.thespeace.restapiwithspring.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {


    /**
     * <h2>DTO -> Domain</h2>
     * <p>간편하게 객체로 값 복사를 위해 사용</p>
     * <p>조금 더 성능상 이점을 노린다면 직접 builder 사용</p>
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
