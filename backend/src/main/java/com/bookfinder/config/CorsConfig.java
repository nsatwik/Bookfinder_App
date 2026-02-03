package com.bookfinder.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Value("${app.cors.allowed-origin}")
  private String allowedOrigin;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] origins = Arrays.stream(allowedOrigin.split(","))
        .map(String::trim)
        .filter(value -> !value.isEmpty())
        .toArray(String[]::new);

    var config = registry.addMapping("/api/**")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*");

    boolean hasWildcard = Arrays.stream(origins).anyMatch(value -> value.contains("*"));
    if (origins.length == 0 || hasWildcard) {
      config.allowedOriginPatterns(origins.length == 0 ? new String[] {"*"} : origins);
    } else {
      config.allowedOrigins(origins);
    }
  }
}
