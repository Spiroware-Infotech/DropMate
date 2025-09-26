package com.dropmate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

	@Autowired
	@Qualifier("customUserDetailsService")
	UserDetailsService userDetailsService;

	@Autowired
	CustomSuccessHandler customSuccessHandler;

	 
	private final String[] WHITE_LIST_URL = { "/**","/login","/static/**"};

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	};

	
	@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build();
    }

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers(WHITE_LIST_URL).permitAll()
            	 // Role-based protected pages
                .requestMatchers("/user/**").hasRole("PASSENGER")
                //.requestMatchers("/institute/**").hasRole("INSTITUTE")
                // Everything else requires authentication
                .anyRequest().authenticated())
            .formLogin(login -> login
                    .loginPage("/login")
                    .loginProcessingUrl("/perform_login")
                    .successHandler(customSuccessHandler)
                    .failureUrl("/login?error=true") // redirects back with error
                    .permitAll())
            .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll())
                .sessionManagement(session -> session
                    .maximumSessions(1)                       // Allow only 1 session per user
                    .maxSessionsPreventsLogin(false)          // Prevent new login if already logged in
                    .expiredUrl("/login?session=expired")     // Redirect if session is expired
                    .sessionRegistry(sessionRegistry()))      // (Optional) Track sessions
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}