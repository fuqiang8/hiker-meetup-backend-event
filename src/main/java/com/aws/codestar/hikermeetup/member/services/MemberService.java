package com.aws.codestar.hikermeetup.member.services;

import com.aws.codestar.hikermeetup.member.data.Member;
import com.aws.codestar.hikermeetup.security.model.CurrentUserInfo;
import com.aws.codestar.hikermeetup.security.services.CurrentUserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class MemberService {
    private final CurrentUserInfoService currentUserInfoService;

    public MemberService(CurrentUserInfoService currentUserInfoService) {
        this.currentUserInfoService = currentUserInfoService;
    }

    public Member getCurrentMember() {
        CurrentUserInfo userInfo = currentUserInfoService.getUserInfo();

        UUID externalIamId = userInfo.getSub();
        String name = String.format("%s, %s", userInfo.getGiven_name(), userInfo.getFamily_name());
        String email = userInfo.getEmail();

        return new Member(externalIamId, name, email);
    }

}
