package org.flowable.bpm.examples.springboot.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { SpELClassValidator.class })
@Repeatable(SpELValidateClasses.class) // only support by jdk8
public @interface SpELValidateClass {

  String message() default "{spel.class.validation.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String value();

}
