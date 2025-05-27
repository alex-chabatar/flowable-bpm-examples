package org.flowable.bpm.examples.springboot.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomEngineConfig {

  @Bean
  public CustomEngineConfigurer customEngineConfigurer() {
    return new CustomEngineConfigurer();
  }

}
