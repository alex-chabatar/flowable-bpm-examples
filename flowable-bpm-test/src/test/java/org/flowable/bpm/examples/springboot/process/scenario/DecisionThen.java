package org.flowable.bpm.examples.springboot.process.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import lombok.extern.slf4j.Slf4j;

@JGivenStage
@Slf4j
public class DecisionThen<SELF extends DecisionThen<SELF>> extends AbstractProcessStage<SELF> {

  @ExpectedScenarioState
  private List<Map<String, Object>> decisionResult;

  @Override
  @IntroWord
  public SELF then() {
    return super.then();
  }

  public SELF decision_result() {
    return self();
  }

  @As("has $number decision(s)")
  public SELF has_decisions(int number) {
    assertThat(decisionResult).hasSize(number);
    return self();
  }

  @As("contains $key = $value")
  public SELF contains(@SingleQuoted String key, @SingleQuoted Object value) {
    var result = decisionResult.stream()
        .anyMatch(map -> map.containsKey(key) && map.get(key).equals(value)) || value == null;
    log.debug("found match {} = {} with result: {}", key, value, result);
    assertThat(result).isTrue();
    return self();
  }

  public SELF contains(Map<String, Object> model) {
    for (Map.Entry<String, Object> entry : model.entrySet()) {
      contains(entry.getKey(), entry.getValue());
    }
    return self();
  }

}
