package org.flowable.bpm.examples.springboot.engine;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.flowable.batch.api.BatchService;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.jobexecutor.ProcessInstanceMigrationStatusJobHandler;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.job.service.impl.persistence.entity.JobEntity;

@Slf4j
public class ProcessMigrationListener extends AbstractFlowableEngineEventListener {

    private static final Set<String> JOB_HANDLER_TYPES = Set.of(ProcessInstanceMigrationStatusJobHandler.TYPE);

    private static final String BATCH_ID = "batchId";
    private static final String BATCH_DOCUMENT_JSON = "batchDocumentJson";

    private static final String BATCH_STATUS_COMPLETED = "completed";
    private static final String BATCH_PART_STATUS_SUCCESS = "success";
    private static final String BATCH_PART_STATUS_FAIL = "fail";
    private static final String BATCH_PART_RESULT = "batchPartResult";

    @Override
    public Collection<? extends FlowableEventType> getTypes() {
        return List.of(FlowableEngineEventType.JOB_EXECUTION_SUCCESS);
    }

    @Override
    protected void jobExecutionSuccess(FlowableEngineEntityEvent event) {
        if (event.getEntity() instanceof JobEntity jobEntity && JOB_HANDLER_TYPES.contains(jobEntity.getJobHandlerType())) {
            var batchId = getBatchIdFromHandlerCfg(jobEntity.getJobHandlerConfiguration());
            var batch = batchService().getBatch(batchId);
            if (BATCH_STATUS_COMPLETED.equals(batch.getStatus())) {
                log.info("Completed Migration Batch '{}': {}",
                        batchId, batch.getBatchDocumentJson(BATCH_DOCUMENT_JSON));
                Optional.ofNullable(batchService().findBatchPartsByBatchId(batchId)).orElse(emptyList()).forEach(batchPart -> {
                    var batchPartStatus = batchPart.getStatus();
                    if (BATCH_PART_STATUS_SUCCESS.equals(batchPartStatus)) {
                        log.info("Batch part '{}' (batchId '{}') completed successfully for process instance '{}'",
                                batchPart.getId(), batchId, batchPart.getScopeId());
                    } else if (BATCH_PART_STATUS_FAIL.equals(batchPartStatus)) {
                        log.error("Batch part '{}' (batchId '{}') failed for process instance '{}': {}",
                                batchPart.getId(), batchId, batchPart.getScopeId(),
                                batchPart.getResultDocumentJson(BATCH_PART_RESULT));
                    }
                });
            }
        }
    }

    private ProcessEngineConfigurationImpl processEngineConfiguration() {
        return Context.getProcessEngineConfiguration();
    }

    private BatchService batchService() {
        return processEngineConfiguration().getBatchServiceConfiguration().getBatchService();
    }

    private static String getBatchIdFromHandlerCfg(String handlerCfg) {
        try {
            var cfgAsJson = getObjectMapper().readTree(handlerCfg);
            if (cfgAsJson.has(BATCH_ID)) {
                return cfgAsJson.get(BATCH_ID).asText();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private static ObjectMapper getObjectMapper() {
        if (CommandContextUtil.getCommandContext() != null) {
            return CommandContextUtil.getProcessEngineConfiguration().getObjectMapper();
        } else {
            return new ObjectMapper();
        }
    }

}
