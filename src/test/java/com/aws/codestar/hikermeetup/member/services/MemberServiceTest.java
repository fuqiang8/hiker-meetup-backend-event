package com.aws.codestar.hikermeetup.member.services;

import com.aws.codestar.hikermeetup.member.data.Member;
import com.aws.codestar.hikermeetup.member.data.MemberRepository;
import com.aws.codestar.hikermeetup.security.model.CurrentUserInfo;
import com.aws.codestar.hikermeetup.security.services.CurrentUserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MemberServiceTest {
    @InjectMocks
    MemberService memberService;

    @Mock
    CurrentUserInfoService currentUserInfoService;

    @Mock
    MemberRepository memberRepository;

    private Member existingUser;

    private final UUID EXTERNAL_ID = UUID.randomUUID();
    private final String EXISTING_NAME = "existingGiven, existingFamily";
    private final String EXISTING_EMAIL = "existingEmail";
    private final String NEW_GIVEN_NAME = "newGiven";
    private final String NEW_FAMILY_NAME = "newFamily";
    private final String NEW_EMAIL = "newEmail";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        existingUser = new Member(EXTERNAL_ID, EXISTING_NAME, EXISTING_EMAIL);

        when(memberRepository.findByExternalIamId(any(UUID.class)))
                .thenAnswer(i -> {
                    if (existingUser.getExternalIamId().equals(i.getArgument(0))) {
                        return Optional.of(existingUser);
                    }
                    else {
                        Member newMember = new Member(i.getArgument(0), String.format("%s, %s", NEW_GIVEN_NAME, NEW_FAMILY_NAME), NEW_EMAIL);
                        ReflectionTestUtils.setField(newMember, "id", UUID.randomUUID());

                        return Optional.of(newMember);
                    }
                });
    }

    @Test
    void getOrCreateCurrentMember_Existing1() {
        mockCurrentUserInfoSetup(EXTERNAL_ID, "", "");

        Member result = memberService.getOrCreateCurrentMember();

        assertEquals(EXTERNAL_ID, result.getExternalIamId());
        assertEquals(EXISTING_NAME, result.getName());
    }

    @Test
    void getOrCreateCurrentMember_New() {
        UUID newUUID = UUID.randomUUID();
        mockCurrentUserInfoSetup(newUUID, NEW_GIVEN_NAME, NEW_FAMILY_NAME);

        Member result = memberService.getOrCreateCurrentMember();

        assertEquals(newUUID, result.getExternalIamId());
        assertEquals(String.format("%s, %s", NEW_GIVEN_NAME, NEW_FAMILY_NAME), result.getName());
    }

    private void mockCurrentUserInfoSetup(UUID sub, String givenName, String familyName) {
        CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        currentUserInfo.setSub(sub);
        currentUserInfo.setFamily_name(familyName);
        currentUserInfo.setGiven_name(givenName);

        when(currentUserInfoService.getUserInfo())
                .thenReturn(currentUserInfo);
    }
}