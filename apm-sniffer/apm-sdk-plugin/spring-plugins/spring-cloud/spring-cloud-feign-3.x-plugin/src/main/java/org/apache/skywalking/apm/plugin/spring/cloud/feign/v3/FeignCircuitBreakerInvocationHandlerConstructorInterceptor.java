package org.apache.skywalking.apm.plugin.spring.cloud.feign.v3;

import feign.Target;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;

public class FeignCircuitBreakerInvocationHandlerConstructorInterceptor implements InstanceConstructorInterceptor {

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {
        String feignClientName = (String) allArguments[1];
        Target<?> target = (Target<?>) allArguments[2];
        objInst.setSkyWalkingDynamicField(new FeignCircuitBreakerContext(feignClientName, target));
    }
}
