package com.wensheng.zcc.sso.config;

import com.wensheng.zcc.sso.service.UserService;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;

    @Resource(name = "userService")
    UserService userService;





  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider());
  }

  @Bean
  @PostConstruct
  public DaoAuthenticationProvider authenticationProvider() {
    final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

    @Autowired
    public void globalUserDetails(final AuthenticationManagerBuilder auth) throws Exception {
        // @formatter:off
	    auth.userDetailsService(userService);
    }// @formatter:on

  @Autowired
  public void dbUserDetails(final AuthenticationManagerBuilder auth) throws Exception {
    // @formatter:off
    auth.userDetailsService(userService);
//    auth.inMemoryAuthentication()
//        .withUser("john").password(passwordEncoder.encode("123")).roles("USER").and()
//        .withUser("tom").password(passwordEncoder.encode("111")).roles("ADMIN").and()
//        .withUser("user1").password(passwordEncoder.encode("pass")).roles("USER").and()
//        .withUser("admin").password(passwordEncoder.encode("nimda")).roles("ADMIN");
  }// @formatte


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
		http.authorizeRequests().antMatchers("/login").permitAll()
        .antMatchers("/oauth/token/revokeById/**").permitAll()
    .antMatchers("/oauth/token/revokeByUserName/**").permitAll()
		.antMatchers("/tokens/**").permitAll()
    .antMatchers("/user/init").permitAll()
    .antMatchers("/wechat/**").permitAll()
    .anyRequest().authenticated()
		.and().formLogin().permitAll()
		.and().csrf().disable();
		// @formatter:on
    }


  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
