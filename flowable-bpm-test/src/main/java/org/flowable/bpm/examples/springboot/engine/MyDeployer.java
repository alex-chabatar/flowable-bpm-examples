package org.flowable.bpm.examples.springboot.engine;

import java.util.Map;

import org.flowable.common.engine.api.repository.EngineDeployment;
import org.flowable.common.engine.impl.EngineDeployer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyDeployer implements EngineDeployer {

  @Override
  public void deploy(EngineDeployment deployment, Map<String, Object> deploymentSettings) {
    log.info("Deploying {}", deployment);
  }

}
