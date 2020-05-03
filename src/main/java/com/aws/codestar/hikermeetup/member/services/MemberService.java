package com.aws.codestar.hikermeetup.member.services;

import com.aws.codestar.hikermeetup.member.data.Member;
import com.aws.codestar.hikermeetup.member.data.MemberRepository;
import com.aws.codestar.hikermeetup.security.model.CurrentUserInfo;
import com.aws.codestar.hikermeetup.security.services.CurrentUserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final CurrentUserInfoService currentUserInfoService;

    public MemberService(MemberRepository memberRepository, CurrentUserInfoService currentUserInfoService) {
        this.memberRepository = memberRepository;
        this.currentUserInfoService = currentUserInfoService;
    }

    public Member getOrCreateCurrentMember() {
        CurrentUserInfo userInfo = currentUserInfoService.getUserInfo();

        UUID externalIamId = userInfo.getSub();
        String name = String.format("%s, %s", userInfo.getGiven_name(), userInfo.getFamily_name());
        String email = userInfo.getEmail();

        return memberRepository.findByExternalIamId(externalIamId)
                .orElseGet(() -> createMember(externalIamId, name, email));
    }

    private Member createMember(UUID externalIamId, String name, String email) {
        Member member = new Member(externalIamId, name, email);

        return memberRepository.save(member);
    }
}
