package com.example.config;

//import com.example.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
//@EnableOAuth2Sso
public class SpringSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private SuccessHandler successHandler;

    //    @Autowired
//    private CustomUserDetailsService customUserDetailsService;
    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
//                .cors()
//                .disable()
                .authorizeRequests()
                .antMatchers("/forgotPassword","/processForgotPassword", "/login", "/register", "/", "/privacy", "/contactSubmit", "/processUpdatePassword", "/signup", "/contact")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .loginPage("/login")
                .successHandler(successHandler)
                .and()
                .csrf()
                .disable();
//        http
//                .formLogin()
//                .loginPage("/login")
//               .successHandler(successHandler)
//                .usernameParameter("userName")
//                .passwordParameter("password");
//        http
//                .logout()
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/");
//        http
//                .oauth2Login()
//                .loginPage("/login")
//                .successHandler(successHandler);


    }

    //    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**/*.jpg", "/**/*.jpeg", "/**/*.png", "/favicon.ico");
    }

}
