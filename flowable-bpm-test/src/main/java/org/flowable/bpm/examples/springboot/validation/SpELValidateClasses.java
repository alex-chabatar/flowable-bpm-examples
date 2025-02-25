package org.flowable.bpm.examples.springboot.validation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SpELValidateClasses {
  SpELValidateClass[] value();
}