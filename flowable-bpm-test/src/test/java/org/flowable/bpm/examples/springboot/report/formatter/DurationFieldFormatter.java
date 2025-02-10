package org.flowable.bpm.examples.springboot.report.formatter;

import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.format.ArgumentFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Format(DurationFieldFormatter.Formatter.class)
public @interface DurationFieldFormatter {

    class Formatter implements ArgumentFormatter<Duration> {

        @Override
        public String format(Duration duration, String... args) {
            return duration.toString();
        }

    }

}
