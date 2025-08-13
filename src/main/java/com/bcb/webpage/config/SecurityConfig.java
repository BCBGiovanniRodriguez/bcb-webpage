package com.bcb.webpage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.bcb.webpage.service.DatabaseUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
//public class SecurityConfig extends WebSecurityConfiguration {
    // Guía: https://www.youtube.com/watch?v=pmSJTrOWi7w
    public static final int BCRYPT_DEFAULT_STRENGHT = 12;

    @Autowired
    DatabaseUserDetailsService databaseUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_DEFAULT_STRENGHT);
    }

    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(databaseUserDetailsService);

        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
        .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .authorizeHttpRequests( auth -> {
                auth.requestMatchers("/portal-clientes/**")
                    .authenticated();
                auth.requestMatchers("/", "/login", "/public/**", "/css/**", "/pages/**", "/inicio-de-sesion", "/portal-clientes/**")
                    .permitAll();
            })
            .formLogin(frm -> frm
                .loginPage("/inicio-de-sesion")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/portal-clientes/dashboard", true)
                .permitAll()
            )
            //.formLogin(Customizer.withDefaults())
            .logout(logout -> {
                logout.logoutSuccessUrl("/inicio-de-sesion?logout");
                logout.permitAll();
            })/*
            .sessionManagement(mgmt -> {
                mgmt.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                //mgmt.invalidSessionUrl("/login");
                mgmt.maximumSessions(1);
                mgmt.sessionFixation().migrateSession();
            })*/
            .build();
    }

    /*
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    */

    /*
    public AuthenticationSuccessHandler successHandler() {
        return (
            (request, response, authentication) -> {
                response.sendRedirect("/inicio-de-sesion");
                //response.sendRedirect("/management/login");
            }
        );
    }
    */

    /*
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("user")
            .build();

            return new InMemoryUserDetailsManager(user);
    }
    */
}
