package com.hikers.sanneomeo.config;

public class Constants {
  //URIS
  public static final String[] SECURITY_WEB_EXCLUDE_URIS = {"/resources/**", "/csrf", "/error", "/swagger*/**", "favicon.ico", "/webjars/**"};
  public static final String[] SECURITY_HTTP_EXCLUDE_URIS = {"/user/after/login", "/user/token", "/user/challenge", "/main/**", "/trail/recommend/**", "/trail/info/**","/spot/**",
                                                              "/mountain/trail/**", "/mountain/photo/**", "/mountain/info/**", "/mountain/position/**", "/mountain/review/**", "/mountain/season", "/mountain/hundred"};
  public static final String[] CORS_HEADER_URIS = {"Authorization", "Refresh"};
  public static final String SECURITY_LOGIN_PROCESSING_URI = "/user/login/callback/*";
  public static final String BASE_URI = "/user/login";
  public static final String SECURITY_AFTER_LOGIN = "http://localhost:9090/user/after/login";



}
