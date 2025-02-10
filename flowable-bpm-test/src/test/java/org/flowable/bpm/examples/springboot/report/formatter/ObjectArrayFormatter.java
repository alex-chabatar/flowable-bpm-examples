package org.flowable.bpm.examples.springboot.report.formatter;

import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.format.ArgumentFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashSet;
import java.util.Set;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Format(ObjectArrayFormatter.Formatter.class)
public @interface ObjectArrayFormatter {

    class Formatter implements ArgumentFormatter<Object[]> {

        @Override
        public String format(Object[] objs, String... args) {
            Set<String> builder = new LinkedHashSet<>();
            for (Object obj : objs) {
                builder.add(obj.toString());
            }
            return builder.toString();
        }

    }

}
