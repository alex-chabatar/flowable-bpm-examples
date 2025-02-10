package org.flowable.bpm.examples.springboot.delegates;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogProcessVariables implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(LogProcessVariables.class);

    @Override
    public void execute(DelegateExecution execution) {
        LOG.info("Process variables: {}", execution.getVariables());
    }

}
