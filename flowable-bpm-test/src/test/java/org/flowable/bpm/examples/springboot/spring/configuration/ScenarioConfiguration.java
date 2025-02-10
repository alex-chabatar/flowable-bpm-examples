package org.flowable.bpm.examples.springboot.spring.configuration;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableJGiven
public class ScenarioConfiguration {

    private static final String JGIVEN_REPORT_DIR = "jgiven.report.dir";

    @PostConstruct
    public void setProperty() {
        System.setProperty(JGIVEN_REPORT_DIR, "target/jgiven-reports/json");
    }

}
