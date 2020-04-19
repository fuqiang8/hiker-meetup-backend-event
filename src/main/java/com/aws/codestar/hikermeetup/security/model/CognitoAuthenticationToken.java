package com.aws.codestar.hikermeetup.security.model;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class CognitoAuthenticationToken extends AbstractAuthenticationToken {

    private String token;

    public CognitoAuthenticationToken(String token, JWTClaimsSet details) {
        super(new ArrayList());
        super.setDetails(details);
        super.setAuthenticated(true);

        this.token = token;
    }

    public CognitoAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return super.getDetails();
    }
}
