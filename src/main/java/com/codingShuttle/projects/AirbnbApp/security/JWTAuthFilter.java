package com.codingShuttle.projects.AirbnbApp.security;

import com.codingShuttle.projects.AirbnbApp.entity.User;
import com.codingShuttle.projects.AirbnbApp.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor

public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      try {
          final String requestTokenHeader = request.getHeader("Authorization");
          if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
              filterChain.doFilter(request, response);
              return;
          }

          String token = requestTokenHeader.split("Bearer ")[1];
          Long userId = jwtService.getUserIdFromToken(token);

          if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {  //user already not logged in
              User user = userService.getUserById(userId);
              UsernamePasswordAuthenticationToken authenticationToken =
                      new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
              authenticationToken.setDetails(
                      new WebAuthenticationDetailsSource().buildDetails(request)   //put this data in the request also
              );
              SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          }
          filterChain.doFilter(request, response);  //this  line is missing
      }catch (JwtException e)
      {
          handlerExceptionResolver.resolveException(request , response , null , e);  //we have to put this bacuase it can handled by webmvc exception also
          //beacuse the the security sxception are in the another context so it cant handle by the webmvc
      }
    }
}
