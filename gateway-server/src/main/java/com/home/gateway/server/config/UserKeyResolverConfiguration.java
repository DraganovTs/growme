package com.home.gateway.server.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;


@Configuration
public class UserKeyResolverConfiguration {

    private static final String USER_HEADER = "user";
    private static final String ANONYMOUS_USER = "anonymous";


    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest()
                        .getHeaders()
                        .getFirst(USER_HEADER))
                .defaultIfEmpty(ANONYMOUS_USER);
    }
}