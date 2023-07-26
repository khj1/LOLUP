package com.lolup.common.fixture;

import com.lolup.member.domain.Member;
import com.lolup.member.domain.Role;

public class MemberFixture {

	public static Member 테스트_회원() {
		return new Member(null, "name", "email", Role.USER, "picture", "summonerName");
	}
}
