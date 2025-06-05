package org.flowable.bpm.examples.springboot.engine;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.configurator.SingleResourceAutoDeploymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomEngineConfig {

  /**
   * By default, Flowable will use a single deployment for all resources.
   * @see <a href="https://forum.flowable.org/t/updating-single-bpmn-creates-new-process-definition-versions-for-all/8215">Flowable Forum</a>
   *
   * @return
   */
  @Bean
  public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> defaultEngineConfigurer() {
    return engineConfiguration -> engineConfiguration.setDeploymentMode(SingleResourceAutoDeploymentStrategy.DEPLOYMENT_MODE);
  }

  @Bean
  public CustomEngineConfigurer customEngineConfigurer() {
    return new CustomEngineConfigurer();
  }

}
