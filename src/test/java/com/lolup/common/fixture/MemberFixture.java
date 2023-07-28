package com.lolup.common.fixture;

import com.lolup.member.domain.Member;
import com.lolup.member.domain.Role;

public class MemberFixture {

	public static final String NAME = "name";
	public static final String EMAIL = "aaa@bbb.ccc";
	public static final String PICTURE = "picture";
	public static final String SUMMONER_NAME = "summonerName";

	public static Member 신규_회원() {
		return new Member(NAME, EMAIL, Role.USER, PICTURE);
	}

	public static Member 소환사_등록_회원() {
		return new Member(NAME, EMAIL, Role.USER, PICTURE, SUMMONER_NAME);
	}
}
