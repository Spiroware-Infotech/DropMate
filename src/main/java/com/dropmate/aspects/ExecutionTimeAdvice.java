package com.dropmate.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@ConditionalOnExpression("${aspect.enabled:true}")
public class ExecutionTimeAdvice {

	@Around("@annotation(com.dropmate.aspects.TrackExecutionTime)")
	public Object executionTime(ProceedingJoinPoint point) throws Throwable {
		//log.info("Class Name: " + point.getSignature().getDeclaringTypeName() + " <===> Method Name: " + point.getSignature().getName());
		long startTime = System.currentTimeMillis();
		Object object = point.proceed();
		long endtime = System.currentTimeMillis();
		log.info("\n Class Name: " + point.getSignature().getDeclaringTypeName() + "\n Method Name: "
				+ point.getSignature().getName() + ".\n Time taken for Execution is : " + (endtime - startTime) + "ms");
		return object;
	}
}