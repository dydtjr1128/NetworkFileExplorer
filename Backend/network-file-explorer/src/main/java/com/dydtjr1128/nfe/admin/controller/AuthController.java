
package com.dydtjr1128.nfe.admin.controller;

import com.dydtjr1128.nfe.admin.model.EmptyJsonResponse;
import com.dydtjr1128.nfe.admin.model.JwtAuthenticationResponse;
import com.dydtjr1128.nfe.admin.model.LoginRequest;
import com.dydtjr1128.nfe.admin.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getId(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken();

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @GetMapping("/token")
    public ResponseEntity<?> validationToken(@RequestHeader(value = "Authorization") String token) {

        if (tokenProvider.validateToken(tokenProvider.getJwtFromString(token)))
            return ResponseEntity.ok(new EmptyJsonResponse());
        else
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

}