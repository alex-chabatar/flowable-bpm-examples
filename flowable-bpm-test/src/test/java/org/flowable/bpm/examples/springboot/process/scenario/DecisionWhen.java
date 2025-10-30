package org.flowable.bpm.examples.springboot.process.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.SingleQuoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import lombok.extern.slf4j.Slf4j;

@JGivenStage
@Slf4j
public class DecisionWhen<SELF extends DecisionWhen<SELF>> extends AbstractProcessStage<SELF> {

  @ExpectedScenarioState
  private Map<String, Object> decisionInput;

  @ProvidedScenarioState
  public List<Map<String, Object>> decisionResult;

  @Override
  @IntroWord
  public SELF when() {
    return super.when();
  }

  public SELF evaluate_decision(@SingleQuoted String decisionDefinitionKey) {
    decisionResult = workflowHelper.evaluateDecision(decisionDefinitionKey, decisionInput);
    assertThat(decisionResult).isNotNull();

    log.info("Evaluated decision {} with result {}", decisionDefinitionKey, decisionResult);
    waitForJobExecutorToProcessAllJobs();

    return self();
  }

}
