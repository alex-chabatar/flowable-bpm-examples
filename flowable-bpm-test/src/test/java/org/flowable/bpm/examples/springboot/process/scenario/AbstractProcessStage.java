package org.flowable.bpm.examples.springboot.process.scenario;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.SingleQuoted;
import org.flowable.bpm.examples.springboot.utils.WorkflowHelper;
import org.flowable.engine.ManagementService;
import org.flowable.engine.impl.test.JobTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractProcessStage<SELF extends AbstractProcessStage<SELF>> extends Stage<SELF> {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    protected WorkflowHelper workflowHelper;

    @Autowired
    protected ManagementService managementService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @SuppressWarnings("unused")
    public SELF test_case(@SingleQuoted String testCase) {
        return self();
    }

    // -- Jobs

    protected void executeJob(Callable<SELF> job) {
        doJob(job, false);
    }

    protected void scheduleJob(Callable<SELF> job) {
        doJob(job, true);
    }

    private void doJob(Callable<SELF> job, boolean async) {
        try {
            if (async) {
                executorService.submit(job);
            } else {
                job.call();
            }
        } catch (Exception ex) {
            LOG.error("Job execution failed", ex);
        }
    }

    protected void waitForJobExecutorToProcessAllJobs() {
        waitForJobExecutorToProcessAllJobs(60 * 1000L, 25L);
    }

    private void waitForJobExecutorToProcessAllJobs(long maxMillisToWait, long intervalMillis) {
        JobTestHelper.waitForJobExecutorToProcessAllJobs(
                workflowHelper.getProcessEngineConfiguration(),
                managementService,
                maxMillisToWait,
                intervalMillis);
    }

}
