package com.lolup.lolup_project.oauth;

import com.lolup.lolup_project.member.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    // userRequest 데이터에 대한 후처리가 진행되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("getClientRegistration={}", userRequest.getClientRegistration());
        log.info("getAccessToken={}", userRequest.getAccessToken().getTokenValue());

        /*
        로그인 버튼 클릭 -> 로그인 창 -> 로그인 완료 -> code 리턴( OAuth-Client 라이브러리 )
            -> Access Token 요청 -> userRequest 정보 반환 -> loadUser함수 호출 -> 회원 프로필 확인 가능
        */

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        log.info("getAttributes={}", oAuth2User.getAttributes());

        // OAuth2 서비스 id (구글, 카카오, 네이버)
        String registrationId = userRequest.getClientRegistration()
                                           .getRegistrationId();

        // OAuth2 로그인 진행 시 키가 되는 필드 값( PK )
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                                                  .getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);

        Member member = saveOrUpdate(userProfile);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                attributes,
                userNameAttributeName);
        }

        // 유저 생성 및 수정 서비스 로직
        private Member saveOrUpdate(UserProfile userProfile){
            Member member = memberRepository.findByEmail(userProfile.getEmail())
                    .map(m -> m.update(userProfile.getName(), userProfile.getEmail(), userProfile.getPicture()))
                    .orElse(userProfile.toMember());

            String email = memberRepository.save(member).getEmail();

            return memberRepository.findByEmail(email).orElse(null);
        }
}
