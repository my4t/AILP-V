package com.ace.ailpv.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@SuppressWarnings({"deprecated"})
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    @Autowired
    RemembermeSuccessHandler remembermeSuccessHandler;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(customUserDetailsService);
        
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
        
            .authorizeRequests()
                .antMatchers("/assets/**", "/auth/**", "/login", "/logout", "/admin/register").permitAll()
                .antMatchers("/admin/**", "/api/**").hasRole("ADMIN")
                .antMatchers("/teacher/**", "/api/**").hasAnyRole("ADMIN", "TEACHER")
                .antMatchers("/student/**", "/api/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .anyRequest()
                    .authenticated()
        .and()
            .formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler)
        .and()
            .rememberMe()
                .authenticationSuccessHandler(remembermeSuccessHandler)
            
                .tokenValiditySeconds(3600);
                
             ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
}