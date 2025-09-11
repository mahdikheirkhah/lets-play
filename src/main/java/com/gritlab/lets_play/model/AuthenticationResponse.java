// In a new file: dto/AuthenticationResponse.java
package com.gritlab.lets_play.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private UserUpdateDto user;
    private String token;
}