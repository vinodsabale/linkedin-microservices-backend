package com.linkdin.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter; // ✅ Correct import
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key; 
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/*

 * 
 * Applied to all routes except /api/v1/auth/
 * 
 * Flow:
 * 1. Extract JWT from Authorization header
 * 2. Validate JWT signature and expiry
 * 3. Extract userId from token claims
 * 4. Add userId to request header for downstream service
 * 5. Forward request to correct service
 * 
 * If JWT invalid or missing -> 401 Unauthorized
 */
@Component
@Slf4j
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
    @Value("${jwt.secret-key}") 
    private String secretKey;   

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) { //  Fixed typo (GateWayFilter → GatewayFilter)
       
        return (exchange, chain) -> {
           String authHeader=exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
           //no token  -Reject
           if(authHeader == null || !authHeader.startsWith("Bearer")) {
               log.warn("Missing or invalid Authorization Header ");
               return unauthorized(exchange);
           }
           String token=authHeader.substring(7);
           try {
               Claims claims=extractClaims(token); // ✅ आता implement केलेलं आहे
               String userId=claims.get("userId",String.class);
               String email=claims.getSubject();
               log.info("JWT validated for user {}",userId);
               //Add userId to request header for downstream services
               ServerWebExchange modifiedExchange=exchange.mutate() // ✅ Typo आधी होता, आता बरोबर
                       .request(r->r.header("X-User-Id", userId).header("X-User-Email", email))
                       .build();
               return chain.filter(modifiedExchange);
           }catch(Exception e) {
               log.error("JWT Validation failed : {}",e.getMessage());
               return unauthorized(exchange);
           }
        };
    }
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder() 
                .setSigningKey(getSignInKey()) 
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() { 
        byte[] keyByte = Decoders.BASE64.decode(secretKey); 
        return Keys.hmacShaKeyFor(keyByte);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Configuration class for custom properties if needed
    }
}
