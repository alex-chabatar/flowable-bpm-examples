package org.flowable.bpm.examples.springboot.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfig {

  @Bean
  public MyConfigurer myConfigurer() {
    return new MyConfigurer();
  }

}
