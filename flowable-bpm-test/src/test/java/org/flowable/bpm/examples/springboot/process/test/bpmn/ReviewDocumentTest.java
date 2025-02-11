package org.flowable.bpm.examples.springboot.process.test.bpmn;

import static org.flowable.bpm.examples.springboot.WorkflowParameters.*;
import static org.flowable.bpm.examples.springboot.process.test.TestDataProvider.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.flowable.bpm.examples.springboot.process.scenario.ProcessGiven;
import org.flowable.bpm.examples.springboot.process.scenario.ProcessThen;
import org.flowable.bpm.examples.springboot.process.scenario.ProcessWhen;
import org.flowable.bpm.examples.springboot.spring.AbstractSpringTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("squid:S2699") // Tests should include assertions
public class ReviewDocumentTest extends AbstractSpringTest<ProcessGiven<?>, ProcessWhen<?>, ProcessThen<?>> {

    public static Stream<Arguments> model() {
        return Stream.of(
            arguments(ALEX),
            arguments(YAO),
            arguments(ZACK),
            arguments(JAREK)
        );
    }

    @ParameterizedTest
    @MethodSource("model")
    public void ensureDocumentReviewed(String reviewer) {

        Map<String, Object> model = reviewDocumentModel(document(), reviewer);

        given()
                .a_process_engine()
                .a_process_model(model);

        when()
                .create_a_process(PROCESS_REVIEW_DOCUMENT);

        then()
                .active_processes(1)
                .active_processes(1, PROCESS_REVIEW_DOCUMENT)
                .process_history()
                .contains(model)
                .active_tasks(1)
                .active_tasks(1, TASK_REVIEW_DOCUMENT);

        when()
                .complete_tasks(TASK_REVIEW_DOCUMENT, approveDocument(true));

        then()
                .completed_processes(1)
                .completed_processes(1, PROCESS_REVIEW_DOCUMENT)
                .completed_tasks(1)
                .completed_tasks(1, TASK_REVIEW_DOCUMENT)
                .process_history()
                .contains(PARAM_APPROVED, true);

    }

}
