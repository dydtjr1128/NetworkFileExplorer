package com.dydtjr1128.nfe.admin.security;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sun.nio.ch.IOUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

/*    @Autowired
    private CustomUserDetailsService customUserDetailsService;*/

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            System.out.println("@@@@" + jwt);
            Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                System.out.println("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
            }
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                /*Long userId = tokenProvider.getUserIdFromJWT(jwt);

                    *//*Note that you could also encode the user's username and roles inside JWT claims
                    and create the UserDetails object by parsing those claims from the JWT.
                    That would avoid the following database hit. It's completely up to you.*//*


                UserDetails userDetails = customUserDetailsService.loadUserById(userId);*/
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@ never?");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("admin","password");
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String splitToken = request.getHeader("Authorization");
        if (StringUtils.hasText(splitToken) && splitToken.startsWith("CYS ")) {
            return splitToken.substring(4, splitToken.length());
        }
        return null;
    }
}
