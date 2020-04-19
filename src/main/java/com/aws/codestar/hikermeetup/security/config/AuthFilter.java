package com.aws.codestar.hikermeetup.security.config;

import com.aws.codestar.hikermeetup.security.model.CognitoAuthenticationToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public class AuthFilter extends BasicAuthenticationFilter {
    private final ConfigurableJWTProcessor<SecurityContext> processor;
    private final AuthenticationManager authenticationManager;

    public AuthFilter(ConfigurableJWTProcessor<SecurityContext> processor, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.processor = processor;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = extractToken(request.getHeader("Authorization"));
        CognitoAuthenticationToken authentication = extractAuthentication(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    /**
     * Extract token from header
     */
    private String extractToken(String header) {
        if (header == null)
            return null;

        String[] headers = header.split("Bearer ");

        if (headers.length < 2) {
            return null;
        } else {
            return headers[1];
        }
    }

    /**
     * Extract authentication details from token
     */
    private CognitoAuthenticationToken extractAuthentication(String token) {
        if (token == null)
            return null;

        try {
            JWTClaimsSet claims = processor.process(token, null);

            return new CognitoAuthenticationToken(token, claims);
        } catch (ParseException | JOSEException | BadJOSEException e) {
            return null;
        }
    }
}
