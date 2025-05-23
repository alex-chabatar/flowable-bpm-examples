package org.flowable.bpm.examples.springboot.engine;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyConfigurer implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

  public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {

    // advanced configuration
    log.info("Configuring process engine {}", processEngineConfiguration);

    // deployers
    if (processEngineConfiguration.getCustomPostDeployers() == null) {
      processEngineConfiguration.setCustomPostDeployers(new java.util.ArrayList<>());
    }
    processEngineConfiguration.getCustomPostDeployers().add(new MyDeployer());

  }

}