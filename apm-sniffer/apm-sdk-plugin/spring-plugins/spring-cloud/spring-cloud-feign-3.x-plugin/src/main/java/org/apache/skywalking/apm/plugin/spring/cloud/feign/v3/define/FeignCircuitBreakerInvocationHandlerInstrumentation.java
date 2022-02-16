package org.apache.skywalking.apm.plugin.spring.cloud.feign.v3.define;

import java.util.Map;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

public class FeignCircuitBreakerInvocationHandlerInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {
    private static final String ENHANCE_CLASS =
        "org.springframework.cloud.openfeign.FeignCircuitBreakerInvocationHandler";
    private static final String INTERCEPTED_METHOD = "asSupplier";
    private static final String CONSTRUCTOR_INTERCEPTOR_CLASS =
        "org.apache.skywalking.apm.plugin.spring.cloud.feign.v3.FeignCircuitBreakerInvocationHandlerConstructorInterceptor";
    private static final String METHOD_INTERCEPTOR_CLASS =
        "org.apache.skywalking.apm.plugin.spring.cloud.feign.v3.FeignCircuitBreakerInvocationHandlerMethodInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[] {new ConstructorInterceptPoint() {
            @Override
            public ElementMatcher<MethodDescription> getConstructorMatcher() {
                return ElementMatchers.takesArguments(6)
                    .and(ElementMatchers.takesArgument(0,
                        ElementMatchers.named("org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory")))
                    .and(ElementMatchers.takesArgument(1, String.class))
                    .and(ElementMatchers.takesArgument(2, ElementMatchers.named("feign.Target")))
                    .and(ElementMatchers.takesArgument(3, Map.class))
                    .and(ElementMatchers.takesArgument(4,
                        ElementMatchers.named("org.springframework.cloud.openfeign.FallbackFactory")))
                    .and(ElementMatchers.takesArgument(5, boolean.class));
            }

            @Override
            public String getConstructorInterceptor() {
                return CONSTRUCTOR_INTERCEPTOR_CLASS;
            }
        }};
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {new InstanceMethodsInterceptPoint() {
            @Override
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return ElementMatchers.named(INTERCEPTED_METHOD);
            }

            @Override
            public String getMethodsInterceptor() {
                return METHOD_INTERCEPTOR_CLASS;
            }

            @Override
            public boolean isOverrideArgs() {
                return false;
            }
        }};
    }
}
