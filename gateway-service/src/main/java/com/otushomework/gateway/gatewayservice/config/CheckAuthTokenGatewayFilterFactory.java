package com.otushomework.gateway.gatewayservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CheckAuthTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<CheckAuthTokenGatewayFilterFactory.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public static class Config {}

    public CheckAuthTokenGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Пропускаем аутентификацию для путей, которые не требуют ее
            if (exchange.getRequest().getMethod() == HttpMethod.POST && path.equals("/user")) {
                return chain.filter(exchange);
            }

            String authToken = exchange.getRequest().getHeaders().getFirst("X-Auth-Token");

            if (authToken == null || !isValidToken(authToken)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Claims claims = getClaimsFromToken(authToken);
            String userId = String.valueOf(claims.get("user_id"));

            // Модифицируем запрос: добавляем X-User-Id и удаляем X-Auth-Token
            exchange = exchange.mutate().request(
                    exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .headers(httpHeaders -> httpHeaders.remove("X-Auth-Token"))
                            .build()
            ).build();

            return chain.filter(exchange);
        };
    }

    private boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
