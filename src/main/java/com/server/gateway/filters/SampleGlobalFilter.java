package com.server.gateway.filters;

import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
	log.info("Executing global filter before the request");

	ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().headers(h -> h.add("token", "abcdefg"))
		.build();

	ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

	return chain.filter(modifiedExchange).then(Mono.fromRunnable(() -> {

	    log.info("Executing filter POST response");

	    Optional.ofNullable(modifiedExchange.getRequest().getHeaders().getFirst("token")).ifPresent(token -> {
		log.info(String.format("token: %s", token));
		modifiedExchange.getResponse().getHeaders().add("token", token);

	    });

	    modifiedExchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
	    modifiedExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

	}));
    }

    @Override
    public int getOrder() {
	return 100;
    }

}
