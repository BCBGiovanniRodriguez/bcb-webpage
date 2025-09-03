package com.bcb.webpage.config;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            //.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests( auth -> {
                
                auth.requestMatchers("/**").permitAll();
                auth.requestMatchers("/portal-clientes", "/portal-clientes/**").authenticated();
                //auth.requestMatchers("/", "/login", "/public/**", "/static/**","/css/**", "/pages/**", "/inicio-de-sesion", "/portal-clientes/**").permitAll();
                //auth.requestMatchers("/", "/login", "/public/**", "/static/**").permitAll();
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
            //.cors(c->c.configurationSource(corsConfigurationSource()))
            .httpBasic(Customizer.withDefaults())
            .build();
    }
    /*
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:20000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }*/

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
