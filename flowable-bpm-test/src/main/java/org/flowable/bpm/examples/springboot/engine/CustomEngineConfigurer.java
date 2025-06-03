package org.flowable.bpm.examples.springboot.engine;

import java.util.ArrayList;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomEngineConfigurer implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

  public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {

    // advanced configuration
    log.info("Configuring process engine {}", processEngineConfiguration);

    // event listeners
    if (processEngineConfiguration.getEventListeners() == null) {
      processEngineConfiguration.setEventListeners(new ArrayList<>());
    }
    processEngineConfiguration.getEventListeners().add(new ProcessMigrationListener());

    // deployers
    if (processEngineConfiguration.getCustomPostDeployers() == null) {
      processEngineConfiguration.setCustomPostDeployers(new ArrayList<>());
    }
    processEngineConfiguration.getCustomPostDeployers().add(new ProcessMigrationDeployer());

  }

}