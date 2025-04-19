package com.example.tests;

import org.codice.itest.api.IntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationTestConfiguration {
  @Bean
  public IntegrationTest testApi() {
    return new BookmarkApiIntegrationTest() {};
  }
}
