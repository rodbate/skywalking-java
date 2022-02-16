package org.apache.skywalking.apm.plugin.spring.cloud.feign.v3;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import feign.Feign;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;

public class FeignCircuitBreakerInvocationHandlerMethodInterceptor implements InstanceMethodsAroundInterceptor {

	@Override
	public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Object ret) throws Throwable {
		if (!ContextManager.isActive() || ret == null) {
			return ret;
		}

		FeignCircuitBreakerContext feignCircuitBreakerContext = (FeignCircuitBreakerContext) objInst.getSkyWalkingDynamicField();
		String feignClientName = feignCircuitBreakerContext.getFeignClientName();
		String feignKey = Feign.configKey(feignCircuitBreakerContext.getTarget().type(), (Method) allArguments[0]);

		final String operationName = String.format("FeignCircuitBreaker/%s/%s", feignClientName, feignKey);
		final ContextSnapshot contextSnapshot = ContextManager.capture();
		final Supplier<Object> retSupplier = (Supplier<Object>) ret;
		return (Supplier<Object>) () -> {
			try {
				AbstractSpan localSpan = ContextManager.createLocalSpan(operationName);
				localSpan.setComponent(ComponentsDefine.FEIGN);
				SpanLayer.asHttp(localSpan);

				ContextManager.continued(contextSnapshot);

				return retSupplier.get();
			}
			finally {
				ContextManager.stopSpan();
			}
		};
	}

	@Override
	public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Throwable t) {

	}
}
