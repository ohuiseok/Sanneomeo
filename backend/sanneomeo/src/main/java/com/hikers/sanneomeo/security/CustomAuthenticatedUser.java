package com.hikers.sanneomeo.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Setter
public class CustomAuthenticatedUser extends AbstractAuthenticationToken {

  private Long userSeq;

  public CustomAuthenticatedUser(Collection<? extends GrantedAuthority> authorities, Long userSeq, boolean isAuthenticated) {
    super(authorities);
    this.setAuthenticated(isAuthenticated);
    this.userSeq = userSeq;
  }


  public Map<String, Object> objToMap(){
    Map<String, Object> attributes = new HashMap<>();

    attributes.put("userSeq", userSeq);
    attributes.put("role", this.getAuthorities().stream()
        .findFirst()
        .orElseGet(()->new SimpleGrantedAuthority("ROLE_USER"))
        .getAuthority());

    return attributes;
  }

  public static CustomAuthenticatedUser mapToObj(Map<String, Object> attributes){
    return new CustomAuthenticatedUser(Collections.singleton(new SimpleGrantedAuthority(String.valueOf(attributes.get("role")))),
        Long.valueOf(attributes.get("userSeq").toString()),
        true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return userSeq;
  }

  public String getRole(){
    return this.getAuthorities().stream()
        .findFirst()
        .orElseGet(()->new SimpleGrantedAuthority("ROLE_USER"))
        .getAuthority();
  }
}
