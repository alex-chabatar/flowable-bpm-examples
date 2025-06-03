package org.flowable.bpm.examples.springboot.engine;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.repository.EngineDeployment;
import org.flowable.common.engine.impl.EngineDeployer;
import org.flowable.engine.ProcessMigrationService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.persistence.entity.DeploymentEntityImpl;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

@Slf4j
public class ProcessMigrationDeployer implements EngineDeployer {

    @Override
    public void deploy(EngineDeployment deployment, Map<String, Object> deploymentSettings) {

        if (deployment == null || !deployment.isNew()) {
            return; // null or already processed...
        }

        Optional.ofNullable(((DeploymentEntityImpl) deployment).getDeployedArtifacts(ProcessDefinition.class))
                .orElse(emptyList())
                .forEach(processDefinition -> {
                    var processDefinitionKey = processDefinition.getKey();
                    var targetProcessDefinitionId = processDefinition.getId();
                    var processDefinitionIdsToMigrate = runtimeService().createProcessInstanceQuery()
                            .processDefinitionKey(processDefinitionKey)
                            .list().stream()
                            .map(ProcessInstance::getProcessDefinitionId)
                            .collect(toSet());
                    var migrationPlan = processMigrationService().createProcessInstanceMigrationBuilder()
                            .migrateToProcessDefinition(targetProcessDefinitionId);
                    processDefinitionIdsToMigrate.forEach(sourceProcessDefinitionId -> {
                        try {
                            var batch = migrationPlan.batchMigrateProcessInstances(sourceProcessDefinitionId);
                            log.info("Scheduled a batch '{}' to migrate process instances of '{}' from '{}' to '{}'",
                                    batch.getId(), processDefinitionKey, sourceProcessDefinitionId, targetProcessDefinitionId);
                        } catch (Exception ex) {
                            log.error("Process Migration failed for process definition '{}': {}",
                                    targetProcessDefinitionId, ex.getMessage(), ex);
                        }
                    });
                });

    }

    private ProcessEngineConfigurationImpl processEngineConfiguration() {
        return Context.getProcessEngineConfiguration();
    }

    private RuntimeService runtimeService() {
        return processEngineConfiguration().getRuntimeService();
    }

    private ProcessMigrationService processMigrationService() {
        return processEngineConfiguration().getProcessMigrationService();
    }

}
