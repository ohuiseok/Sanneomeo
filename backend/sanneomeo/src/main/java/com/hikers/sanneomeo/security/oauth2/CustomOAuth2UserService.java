package com.hikers.sanneomeo.security.oauth2;

import com.hikers.sanneomeo.domain.User;
import com.hikers.sanneomeo.exception.BaseResponseStatus;
import com.hikers.sanneomeo.exception.OAuth2LoginException;
import com.hikers.sanneomeo.repository.UserRepository;
import com.hikers.sanneomeo.repository.UserRepositoryImpl;
import com.hikers.sanneomeo.security.CustomAuthenticatedUser;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  //Bean
  private UserRepository userRepositorySupport;

  @Autowired
  private void setUserRepository(UserRepository userRepositorySupport){
    this.userRepositorySupport = userRepositorySupport;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    //토큰을 통해 리소스 서버에서 유저 정보를 가져온다.
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    //각 유저 정보를 registration Id를 기준으로 매핑하여 CustomOAuth2User 객체로 변환한다.
    CustomOAuth2User customOAuth2User = CustomOAuth2User.mapper(
        oAuth2User.getAttributes(), userRequest.getClientRegistration().getRegistrationId(),
        userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
            .getUserNameAttributeName());

    //db에 존재하지 않는 유저에 대해 -> failure handler로 보낸다.
    User user = userRepositorySupport
        .findUserBySocialId(customOAuth2User.getRegistrationId(),
            customOAuth2User.getSocialId())
        .orElseThrow(() -> {
          throw new OAuth2LoginException(BaseResponseStatus.SIGNUP_REQUIRED, customOAuth2User);
        });

    //db에 존재하는 유저라면 CustomAuthenticatedUser 인증 객체를 만든다.
    CustomAuthenticatedUser customAuthenticatedUser = new CustomAuthenticatedUser(
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
        user.getUserSeq(),
        true
    );

    //하지만 Spring Security OAuth2에서 관련 필터를 거치기 위해서는
    //OAuth2User의 구현체가 필요하다.
    //기본적인 DefaultOAuth2User 인스턴스를 생성하기 위해
    //CustomAuthenticatedUser의 각 요소를 map으로 변환해 사용한다.
    Map<String, Object> attributes = customAuthenticatedUser.objToMap();

    //oauth2객체의 검사를 위해 name attribute에 해당하는 값을 넣는다.
    attributes.put(customOAuth2User.getNameAttributeKey(), customOAuth2User.getSocialId());

    //인증 객체 만들고 success handler
    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(attributes.get("role").toString())),
        attributes,
        customOAuth2User.getNameAttributeKey());
  }
}
