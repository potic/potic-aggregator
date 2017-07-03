package me.potic.aggregator.config

import com.auth0.spring.security.api.JwtWebSecurityConfigurer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity(debug = true)
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value(value = '${auth0.apiAudience}')
    String apiAudience

    @Value(value = '${auth0.issuer}')
    String issuer

    @Override
    protected void configure(HttpSecurity http) {
        JwtWebSecurityConfigurer
                .forRS256(apiAudience, issuer)
                .configure(http)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, '/sandbox/**').permitAll()
                .antMatchers(HttpMethod.OPTIONS, '/sandbox/**').permitAll()
                .anyRequest().authenticated()
    }
}
