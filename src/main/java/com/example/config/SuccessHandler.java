package com.example.config;

import com.example.model.UserModel;
import com.example.repository.DatabaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Component
public class SuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(SuccessHandler.class);
    @Autowired
    private DatabaseRepository repository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
       log.info("Oauth2SuccessHandler:::::::::");

        String usr=null;

        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            //identify the provider
            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            String authorizedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
            log.info("Registration id of Oauth2 ::"+authorizedClientRegistrationId);
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            UserModel user = new UserModel();
            user.setPassword("dummy");
            user.setRole("ENDUSER");
            user.setFirstLogin(false);
            user.setDob(Date.valueOf(LocalDate.of(2000,1,1)));
            user.setCommission(0);
            user.setCoinValue(0);
            user.setCreatedOn(LocalDateTime.now());
            user.setBloodGroup("B+");
            if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
                //for google
                user.setCreatedBy("auto");
                user.setName(oAuth2User.getAttribute("name").toString());
                usr=oAuth2User.getAttribute("email").toString();
                user.setUserName(oAuth2User.getAttribute("email").toString());
                user.setAddress("google");

            }
            else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
                String email = oAuth2User.getAttribute("email") != null ? oAuth2User.getAttribute("email").toString()
                        : oAuth2User.getAttribute("login").toString() + "@gmail.com";
//                String picture = oAuth2User.getAttribute("avatar_url").toString();
                String name = oAuth2User.getAttribute("login").toString();
                String providerUserId = oAuth2User.getName();
                user.setUserName(email);
                user.setName(name);
                user.setCreatedBy("auto");
                user.setAddress("github");
                usr=email;

            }else {
                log.info("Unknown Provider");
            }

            UserModel byUserName = repository.findByUserName(user.getUserName());
            if (byUserName == null) {
                repository.save(user);
                log.info("User Saved By Email Id:::" + user.getUserName());
            }
        }
        else {
            User principal = (User) authentication.getPrincipal();
            usr= principal.getUsername();
        }

        HttpSession session = httpServletRequest.getSession();   //session object created
        System.out.println("session created");
        UserModel returnDto = repository.findByUserName(usr);
        session.setAttribute("id", returnDto.getId());
        session.setAttribute("userId", returnDto.getUserName());
        session.setAttribute("name", returnDto.getName());
        session.setAttribute("role", returnDto.getRole());
        session.setAttribute("dob", returnDto.getDob());
        session.setAttribute("createdBy", returnDto.getCreatedBy());
        session.setAttribute("createdOn", returnDto.getCreatedOn());
        session.setAttribute("bloodGroup", returnDto.getBloodGroup());
        session.setAttribute("coinValue", returnDto.getCoinValue());

            new DefaultRedirectStrategy().sendRedirect(httpServletRequest,httpServletResponse,"/dashboard");
        log.info("Now redirect to ->>>/dashboard");

    }
}
