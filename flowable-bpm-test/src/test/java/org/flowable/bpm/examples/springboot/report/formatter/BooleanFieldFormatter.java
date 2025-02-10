package org.flowable.bpm.examples.springboot.report.formatter;

import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.format.ArgumentFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Format(BooleanFieldFormatter.Formatter.class)
public @interface BooleanFieldFormatter {

    class Formatter implements ArgumentFormatter<Boolean> {

        @Override
        public String format(Boolean value, String... args) {
            return value ? "Yes" : "No";
        }

    }

}
