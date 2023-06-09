package com.hikers.sanneomeo.config;

//import static com.hikers.sanneomeo.config.Constants.HTTP_SECURITY_EXCLUDE_URI;

import com.hikers.sanneomeo.domain.Credentials;
import com.hikers.sanneomeo.repository.UserRepository;
import com.hikers.sanneomeo.security.CustomAuthenticationEntryPoint;
import com.hikers.sanneomeo.security.jwt.JwtTokenFilter;
import com.hikers.sanneomeo.security.oauth2.CustomOAuth2CookieAuthorizationRequestRepository;
import com.hikers.sanneomeo.security.oauth2.CustomOAuth2Provider;
import com.hikers.sanneomeo.security.oauth2.CustomOAuth2UserFailureHandler;
import com.hikers.sanneomeo.security.oauth2.CustomOAuth2UserService;
import com.hikers.sanneomeo.security.oauth2.CustomOAuth2UserSuccessHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig {

  @Autowired
  private YmlConfig ymlConfig;

  @Autowired
  UserRepository userRepository;

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        .antMatchers(Constants.SECURITY_WEB_EXCLUDE_URIS);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    //cors config
    http.cors().configurationSource(corsConfigurationSource());

    //기본 설정 해제와 경로 설정
    http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(FormLoginConfigurer::disable)
        .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .requestCache(RequestCacheConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
                .antMatchers(Constants.SECURITY_HTTP_EXCLUDE_URIS).permitAll()
                .anyRequest().authenticated()
                //임시로 모든 요청 허용
//                .anyRequest().permitAll()
        )
    ;
    http.getConfigurer(DefaultLoginPageConfigurer.class).disable();

    //oauth2 설정
    http
        .oauth2Login(oauth2 -> oauth2
            .authorizationEndpoint(authorization-> authorization
                .baseUri(Constants.BASE_URI)
                .authorizationRequestRepository(oAuth2CookieAuthorizationRequestRepository())
            )
            .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService()))
            .loginProcessingUrl(Constants.SECURITY_LOGIN_PROCESSING_URI)
            .clientRegistrationRepository(clientRegistrationRepository())
            .successHandler(customOAuth2UserSuccessHandler())
            .failureHandler(customOAuth2UserFailureHandler())
        );

    http.addFilterBefore(jwtTokenFilter(), OAuth2AuthorizationRequestRedirectFilter.class)
        .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint());

    return http.build();
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository(){

    List<ClientRegistration> registrationList = new ArrayList<>();

    //kakao client registration
    Map<String, Credentials> credentialsMap = ymlConfig.getCredentials();
    registrationList.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
        .clientId(credentialsMap.get("kakao").getId()).build());

    //google client registration
    registrationList.add(CustomOAuth2Provider.GOOGLE.getBuilder("google")
        .clientId(credentialsMap.get("google").getId())
        .clientSecret(credentialsMap.get("google").getSecret()).build());

    //naver client registration
    registrationList.add(CustomOAuth2Provider.NAVER.getBuilder("naver")
        .clientId(credentialsMap.get("naver").getId())
        .clientSecret(credentialsMap.get("naver").getSecret()).build());

    return new InMemoryClientRegistrationRepository(registrationList);
  }

  @Bean
  public CustomOAuth2CookieAuthorizationRequestRepository<OAuth2AuthorizationRequest> oAuth2CookieAuthorizationRequestRepository(){
    return new CustomOAuth2CookieAuthorizationRequestRepository<>(clientRegistrationRepository());
  }

  @Bean
  public CustomOAuth2UserService customOAuth2UserService(){
    return new CustomOAuth2UserService();
  }

  @Bean
  public CustomOAuth2UserFailureHandler customOAuth2UserFailureHandler() { return new CustomOAuth2UserFailureHandler(userRepository); }
  @Bean
  public CustomOAuth2UserSuccessHandler customOAuth2UserSuccessHandler() {return new CustomOAuth2UserSuccessHandler(ymlConfig);}

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedMethod("*");
    configuration.setAllowCredentials(true);

    //custom header 설정
    for(String key : Constants.CORS_HEADER_URIS){
      configuration.addAllowedHeader(key);
      configuration.addExposedHeader(key);
    }

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public JwtTokenFilter jwtTokenFilter(){
    return new JwtTokenFilter();
  }

  @Bean
  public CustomAuthenticationEntryPoint customAuthenticationEntryPoint(){
    return new CustomAuthenticationEntryPoint();
  }
}
