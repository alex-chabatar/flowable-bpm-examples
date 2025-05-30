package org.flowable.bpm.examples.springboot.engine;

import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.flowable.batch.api.Batch;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.engine.delegate.event.BaseEntityEventListener;

@Slf4j
public class BatchCompletedListener extends BaseEntityEventListener {

    public BatchCompletedListener() {
        super(true, Batch.class);
    }

    @Override
    public Collection<? extends FlowableEventType> getTypes() {
        return List.of(FlowableEngineEventType.ENTITY_UPDATED);
    }

    @Override
    protected void onUpdate(FlowableEvent event) {
        var entityEvent = (FlowableEntityEvent) event;
        var batch = (Batch) entityEvent.getEntity();
        if (batch.getCompleteTime() != null) {
            // Custom implementation
            log.info("Batch '{}' completed at {}", batch.getId(), batch.getCompleteTime());
        }
    }
}
