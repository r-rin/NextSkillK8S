package ukma.springboot.nextskill.user.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import ukma.springboot.nextskill.user.filters.AuthExceptionHandler;
import ukma.springboot.nextskill.user.filters.AuthenticationFilter;
import ukma.springboot.nextskill.user.filters.JWTAuthorizationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private AuthExceptionHandler authExceptionHandler;
    private JWTAuthorizationFilter jwtAuthorizationFilter;
    private JWTUtility jwtUtility;
    private AuthenticationManager authenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("register").permitAll()
                        .requestMatchers("error").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/home")
                )
                .logout(logout -> logout.deleteCookies("SECRET_TOKEN"))
                .addFilterBefore(authExceptionHandler, AuthenticationFilter.class)
                .addFilter(new AuthenticationFilter(authenticationManager, jwtUtility))
                .addFilterAfter(jwtAuthorizationFilter, AuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
