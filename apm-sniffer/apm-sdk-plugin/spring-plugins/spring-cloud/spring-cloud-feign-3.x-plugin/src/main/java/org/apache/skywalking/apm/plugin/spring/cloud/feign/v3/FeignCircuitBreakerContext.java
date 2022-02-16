package org.apache.skywalking.apm.plugin.spring.cloud.feign.v3;

import feign.Target;

public class FeignCircuitBreakerContext {
    private final String feignClientName;
    private final Target<?> target;

    public FeignCircuitBreakerContext(String feignClientName, Target<?> target) {
        this.feignClientName = feignClientName;
        this.target = target;
    }

    public String getFeignClientName() {
        return feignClientName;
    }

    public Target<?> getTarget() {
        return target;
    }
}
