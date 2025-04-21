package com.example.tests;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationTestConfiguration {
  @Bean
  public ApiTest testApi() {
    return new ApiTest() {};
  }
}
