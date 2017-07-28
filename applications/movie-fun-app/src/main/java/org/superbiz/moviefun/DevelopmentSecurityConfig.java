package org.superbiz.moviefun;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Created by pivotal on 7/28/17.
 */
@Configuration
@Profile("development")
public class DevelopmentSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("SEC override config - disable basic auth");
        http.httpBasic().disable();
    }

    @Bean
    public RestOperations restOperations() {
        System.out.println("SEC default template");
        return new RestTemplate();
    }
}
