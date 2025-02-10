package org.flowable.bpm.examples.springboot.process.test;

import static org.flowable.bpm.examples.springboot.WorkflowParameters.*;

import org.flowable.bpm.examples.springboot.process.scenario.ProcessGiven;
import org.flowable.bpm.examples.springboot.process.scenario.ProcessThen;
import org.flowable.bpm.examples.springboot.process.scenario.ProcessWhen;
import org.flowable.bpm.examples.springboot.spring.AbstractSpringTest;
import org.junit.Test;

@SuppressWarnings("squid:S2699") // Tests should include assertions
public class DeploymentTest extends AbstractSpringTest<ProcessGiven<?>, ProcessWhen<?>, ProcessThen<?>> {

    @Test
    public void ensureDeployment() {

        given()
                .a_process_engine();

        then()
                .deployed_process_definitions(1)
                .deployed_process_definition(PROCESS_REVIEW_DOCUMENT);

    }

}
