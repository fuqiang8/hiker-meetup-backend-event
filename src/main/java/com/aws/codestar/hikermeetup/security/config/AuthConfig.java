package com.aws.codestar.hikermeetup.security.config;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration for web security
 */
@EnableWebSecurity
public class AuthConfig extends WebSecurityConfigurerAdapter {
    private final ConfigurableJWTProcessor<SecurityContext> processor;

    public AuthConfig(ConfigurableJWTProcessor<SecurityContext> processor) {
        this.processor = processor;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new AuthFilter(processor, authenticationManager()), UsernamePasswordAuthenticationFilter.class);

        // TODO: Look into how to provide CSRF token. csrf.disabled() is discouraged as per offical Spring: https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/appendix-faq.html#appendix-faq-forbidden-csrf
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/events/**").permitAll()
                .and()
            .authorizeRequests()
                .antMatchers("/events/**").authenticated()
                .and()
            .csrf().disable();
    }
}
