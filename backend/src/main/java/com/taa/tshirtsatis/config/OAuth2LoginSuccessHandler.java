package com.taa.tshirtsatis.config;

import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.service.JwtService;
import com.taa.tshirtsatis.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UsersService userService;

    public OAuth2LoginSuccessHandler(JwtService jwtService, UsersService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Users user = userService.processOAuthPostLogin(email);

        String token = jwtService.generateToken(user);

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }
}
