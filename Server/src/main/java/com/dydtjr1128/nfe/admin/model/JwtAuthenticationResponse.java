package com.dydtjr1128.nfe.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
}
