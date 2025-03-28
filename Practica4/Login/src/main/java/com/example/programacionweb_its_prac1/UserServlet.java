package com.example.programacionweb_its_prac1;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.example.programacionweb_its_prac1.AutenticacionServlet.generalKey;
import static com.example.programacionweb_its_prac1.AutenticacionServlet.users;
@WebServlet("/user-servlet/*")
public class UserServlet extends HttpServlet {
    private final JsonResponse jResp = new JsonResponse();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String authTokenHeader = req.getHeader("Authorization");
        if (authTokenHeader == null || !authTokenHeader.startsWith("Bearer ")) {
            jResp.failed(req, resp, "Token no proporcionado", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        validateAuthToken(req, resp, authTokenHeader.split(" ")[1]);
    }

    private void validateAuthToken(HttpServletRequest req, HttpServletResponse resp, String token) throws IOException {
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(generalKey())
                .build();
        try {
            var claims = jwtParser.parseSignedClaims(token);
            String username = claims.getPayload().getSubject();

            // Obtener usuario del HashMap y crear objeto sin contraseña
            User user = users.get(username);
            if (user != null) {
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("username", user.getUsername());
                userInfo.put("fullName", user.getFullName());
                userInfo.put("email", user.getEmail());

                jResp.success(req, resp, "Usuario autenticado", userInfo);
            } else {
                jResp.failed(req, resp, "Usuario no encontrado", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            jResp.failed(req, resp, "Unauthorized: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
