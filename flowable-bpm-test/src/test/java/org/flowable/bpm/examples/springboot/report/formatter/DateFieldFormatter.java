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
@Format(DateFieldFormatter.Formatter.class)
public @interface DateFieldFormatter {

    class Formatter implements ArgumentFormatter<Long> {

        @Override
        public String format(Long milliseconds, String... args) {
            return Duration.ofMillis(milliseconds).toString();
        }

    }

}
