package com.gauro.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator microServiceRouterConfig(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/customer/**")
						.filters(f ->
								f.rewritePath("/customer/(?<segment>,*)", "/${segment}")
										.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())

						)
						.uri("lb://CUSTOMER"))
				.route(p -> p
						.path("/accounts/**")
						.filters(f ->
								f.rewritePath("/accounts/(?<segment>,*)", "/${segment}")
										.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
						)
						.uri("lb://ACCOUNTS"))
				.route(p -> p
						.path("/loans/**")
						.filters(f ->
								f.rewritePath("/loans/(?<segment>,*)", "/${segment}")
										.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
						)
						.uri("lb://LOANS"))
				.route(p -> p
						.path("/cards/**")
						.filters(f ->
								f.rewritePath("/cards/(?<segment>,*)", "/${segment}")
										.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
						)
						.uri("lb://CARDS"))


				.build();


	}

}
