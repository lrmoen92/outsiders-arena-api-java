package org.outsiders.arena.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig
  extends WebSecurityConfigurerAdapter
{
  protected void configure(HttpSecurity httpSecurity)
    throws Exception
  {
    httpSecurity.authorizeRequests().antMatchers("/**").permitAll();
    httpSecurity.headers().frameOptions().disable();
    httpSecurity.csrf().disable();
    httpSecurity.cors().disable();
  }
  
  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter()
  {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(Boolean.valueOf(true));
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean(new CorsFilter(source), new ServletRegistrationBean[0]);
    bean.setOrder(0);
    return bean;
  }
}