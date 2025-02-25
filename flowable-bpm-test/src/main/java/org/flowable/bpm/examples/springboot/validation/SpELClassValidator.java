package org.flowable.bpm.examples.springboot.validation;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpELClassValidator implements ConstraintValidator<SpELValidateClass, Object> {

  private final ExpressionParser parser = new SpelExpressionParser();

  private Expression expression;

  @Override
  public void initialize(SpELValidateClass classToValidate) {
    expression = parser.parseExpression(classToValidate.value());
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
    return (Boolean) expression.getValue(new StandardEvaluationContext(value));
  }

}
