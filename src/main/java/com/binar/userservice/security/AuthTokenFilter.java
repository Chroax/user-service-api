package com.binar.userservice.security;

import com.binar.userservice.services.impl.security.UserDetailsServiceImpl;
import com.binar.userservice.utility.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!hasToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);

        if (!jwtUtils.validateJwtToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthentication(token, request);

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        String email = jwtUtils.getEmailFromJwtToken(token);
        log.info("Email - " + email);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if(userDetails == null) return;
        request.setAttribute("token", token);
        request.setAttribute("authorities", authoritiesToString(userDetails.getAuthorities()));
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(Constants.HEADER);
        return header.split(" ")[1].trim();
    }

    private boolean hasToken(HttpServletRequest request) {
        String header = request.getHeader(Constants.HEADER);
        return !ObjectUtils.isEmpty(header) && header.startsWith(Constants.TOKEN_PREFIX);
    }

    private String authoritiesToString(Collection<? extends GrantedAuthority> collection) {
        StringBuilder strb = new StringBuilder();
        for(GrantedAuthority a : collection) {
            strb.append(a.getAuthority());
            strb.append(" ");
        }
        return strb.toString().trim();
    }
}