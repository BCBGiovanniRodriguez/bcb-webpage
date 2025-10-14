package com.bcb.webpage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.bcb.webpage.model.webpage.repository.OneTimeTokenRepository;
import com.bcb.webpage.service.CustomOneTimeTokenSuccessHandler;
import com.bcb.webpage.service.DatabaseUserDetailsService;
import com.bcb.webpage.service.EmailGeneratedOneTimeTokenHandler;
import com.bcb.webpage.service.JpaOneTimeTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SecurityConfig {
    
    public static final int BCRYPT_DEFAULT_STRENGHT = 12;

    @Autowired
    DatabaseUserDetailsService databaseUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_DEFAULT_STRENGHT);
    }

    @Bean
    public OneTimeTokenService jpaOneTimeTokenService(OneTimeTokenRepository oneTimeTokenRepository, DatabaseUserDetailsService databaseUserDetailsService) {
        return new JpaOneTimeTokenService(oneTimeTokenRepository, databaseUserDetailsService);
    }

    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(databaseUserDetailsService);

        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, 
        JpaOneTimeTokenService jpaOneTimeTokenService,
        EmailGeneratedOneTimeTokenHandler ottSuccessHandler) throws Exception {
        return httpSecurity
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .csrf(Customizer.withDefaults())
            .authorizeHttpRequests( auth -> {
                auth.requestMatchers("/**", "/login", "/login/**", "/ml/**", "/ott/sent", "/logout", "/public/**").permitAll();
                auth.requestMatchers("/portal-clientes", "/portal-clientes/**").authenticated();
            })
            .formLogin(frm -> frm
                .loginPage("/inicio-de-sesion")
                .usernameParameter("contract-number")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/portal-clientes/dashboard", true)
                .permitAll()
            )
            //.logout(Customizer.withDefaults())
            .logout(logout -> {
                logout.logoutUrl("/logout");
                logout.deleteCookies("JSESSIONID");
                logout.invalidateHttpSession(true);
                logout.logoutSuccessUrl("/inicio-de-sesion?logout");
                logout.permitAll();
            })
            //.oneTimeTokenLogin(Customizer.withDefaults())
            .oneTimeTokenLogin(ott -> {
                ott.tokenService(jpaOneTimeTokenService);
                ott.defaultSubmitPageUrl("/login/ott");
                ott.tokenGeneratingUrl("/ml/generate");
                ott.loginProcessingUrl("/ml/submit");
                ott.showDefaultSubmitPage(false);
                ott.tokenGenerationSuccessHandler(ottSuccessHandler);
                ott.authenticationSuccessHandler(successHandler());
                ott.authenticationFailureHandler((request, response, exception) -> {
                    log.error("Error", exception);
                    response.sendRedirect("/login?error=ott");
                });
            }) 
            /*
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

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomOneTimeTokenSuccessHandler("/portal-clientes/dashboard");
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
