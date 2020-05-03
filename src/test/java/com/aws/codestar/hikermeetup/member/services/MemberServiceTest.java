package com.aws.codestar.hikermeetup.member.services;

import com.aws.codestar.hikermeetup.member.data.Member;
import com.aws.codestar.hikermeetup.security.model.CurrentUserInfo;
import com.aws.codestar.hikermeetup.security.services.CurrentUserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MemberServiceTest {
    @InjectMocks
    MemberService memberService;

    @Mock
    CurrentUserInfoService currentUserInfoService;

    private final UUID EXTERNAL_ID = UUID.randomUUID();
    private final String NEW_GIVEN_NAME = "newGiven";
    private final String NEW_FAMILY_NAME = "newFamily";
    private final String NEW_EMAIL = "newEmail";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        mockCurrentUserInfoSetup(EXTERNAL_ID, NEW_GIVEN_NAME, NEW_FAMILY_NAME, NEW_EMAIL);
    }

    @Test
    void getCurrentMember() {
        Member result = memberService.getCurrentMember();

        assertEquals(EXTERNAL_ID, result.getExternalIamId());
        assertEquals(String.format("%s, %s", NEW_GIVEN_NAME, NEW_FAMILY_NAME), result.getName());
        assertEquals(NEW_EMAIL, result.getEmail());
    }

    private void mockCurrentUserInfoSetup(UUID sub, String givenName, String familyName, String email) {
        CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        currentUserInfo.setSub(sub);
        currentUserInfo.setFamily_name(familyName);
        currentUserInfo.setGiven_name(givenName);
        currentUserInfo.setEmail(email);

        when(currentUserInfoService.getUserInfo())
                .thenReturn(currentUserInfo);
    }
}