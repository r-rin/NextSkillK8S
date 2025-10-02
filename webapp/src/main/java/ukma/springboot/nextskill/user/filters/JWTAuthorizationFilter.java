package ukma.springboot.nextskill.user.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ukma.springboot.nextskill.user.security.JWTUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private JWTUtility jwtUtility;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        List<Cookie> cookies = request.getCookies() == null ? new ArrayList<>() : Arrays.asList(request.getCookies());
        Optional<Cookie> secretTokenCookie = cookies.stream()
                .filter(cookie -> "SECRET_TOKEN".equals(cookie.getName()))
                .findFirst();

        if (secretTokenCookie.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        DecodedJWT jwt = jwtUtility.verifyToken(secretTokenCookie.get().getValue());

        String username = jwt.getSubject();
        List<GrantedAuthority> authorities = jwt.getClaim("roles").asList(String.class).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
