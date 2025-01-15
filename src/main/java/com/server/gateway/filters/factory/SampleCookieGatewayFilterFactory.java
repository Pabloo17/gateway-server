package com.server.gateway.filters.factory;

import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SampleCookieGatewayFilterFactory
	extends AbstractGatewayFilterFactory<SampleCookieGatewayFilterFactory.ConfigurationCookie> {

    public SampleCookieGatewayFilterFactory() {
	super(ConfigurationCookie.class);

    }

    @Override
    public GatewayFilter apply(ConfigurationCookie config) {

	return new OrderedGatewayFilter((exchange, chain) -> {
	    log.info(String.format("Executing PRE gateway filter factory: %s", config.message));

	    return chain.filter(exchange).then(Mono.fromRunnable(() -> {

		Optional.ofNullable(config.value).ifPresent(
			cookie -> exchange.getResponse().addCookie(ResponseCookie.from(config.name, cookie).build())

		);
		log.info(String.format("Executing POST gateway filter factory: %s", config.message));

	    }));
	}, 100);

    }

    @Getter
    @Setter
    public static class ConfigurationCookie {
	private String name;

	private String value;

	private String message;
    }

}
