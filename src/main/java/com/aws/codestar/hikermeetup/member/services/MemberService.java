package com.aws.codestar.hikermeetup.member.services;

import com.aws.codestar.hikermeetup.member.data.Member;
import com.aws.codestar.hikermeetup.member.data.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Page<Member> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Member getMember(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find member " + memberId));
    }

    public Member createMember(UUID userId, String name) {
        Member member = new Member(userId, name);

        return memberRepository.save(member);
    }

    public Member getOrCreateMember(UUID userId, String name) {
        return memberRepository.findById(userId)
                .orElse(createMember(userId,name));
    }
}
