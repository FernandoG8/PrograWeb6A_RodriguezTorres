package com.example.programacionweb_its_prac1;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import persistencia.dao.UserDAO;
import persistencia.modelo.User;

@WebServlet("/user-servlet/*")
public class UserServlet extends HttpServlet {
    private static final int SC_UNPROCESSABLE_ENTITY = 422;
    private final JsonResponse jResp = new JsonResponse();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        // Validación del token
        String authTokenHeader = req.getHeader("Authorization");
        if (authTokenHeader == null || !authTokenHeader.startsWith("Bearer ")) {
            jResp.failed(req, resp, "Token no proporcionado", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authTokenHeader.split(" ")[1];
        if (!validateAuthToken(token)) {
            jResp.failed(req, resp, "Token no válido", SC_UNPROCESSABLE_ENTITY);
            return;
        }

        // Procesamiento de la ruta
        //(GET) /user-servlet/
        //▪ Debe devolver los datos de todos los usuarios registrados, excepto sus
        //contraseñas.
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllUsers(req, resp);
        } else {
            //(GET) /user-servlet/{id}
            //▪ Debe devolver los datos del usuario con el id indicado en la ruta, excepto su contraseña.
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int userId = Integer.parseInt(pathParts[1]);
                    getUserById(req, resp, userId);
                } catch (NumberFormatException e) {
                    jResp.failed(req, resp, "ID de usuario no válido", HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                jResp.failed(req, resp, "Ruta no válida", HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        // Validación del token
        String authTokenHeader = req.getHeader("Authorization");
        if (authTokenHeader == null || !authTokenHeader.startsWith("Bearer ")) {
            jResp.failed(req, resp, "Token no proporcionado", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authTokenHeader.split(" ")[1];
        if (!validateAuthToken(token)) {
            jResp.failed(req, resp, "Token no válido", SC_UNPROCESSABLE_ENTITY);
            return;
        }

        // Validación de datos del usuario
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Validación de campos vacíos
        if (name == null || email == null || password == null ||
                name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            jResp.failed(req, resp, "Todos los campos son obligatorios", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            // Verificar si el usuario ya existe
            if (userDAO.existsUserByNameOrEmail(name.trim(), email.trim())) {
                jResp.failed(req, resp, "El nombre de usuario o email ya está en uso", HttpServletResponse.SC_CONFLICT);
                return;
            }

            // Encriptar la contraseña
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Crear nuevo usuario con la contraseña encriptada
            User newUser = new User(0, name.trim(), email.trim(), hashedPassword, null);

            if (userDAO.agregar(newUser)) {
                // Crear objeto de respuesta
                Map<String, Object> responseData = new HashMap<>();
                Map<String, String> userData = new HashMap<>();
                userData.put("name", newUser.getName());
                userData.put("email", newUser.getEmail());
                responseData.put("message", "Usuario creado exitosamente");
                responseData.put("user", userData);

                // Enviar respuesta exitosa
                jResp.success(req, resp, responseData);
            } else {
                jResp.failed(req, resp, "Error al crear el usuario", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            jResp.failed(req, resp, "Error interno del servidor", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean validateAuthToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parser()
                    .verifyWith(AutenticacionServlet.generalKey())
                    .build();
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.err.println("Error validando token: " + e.getMessage());
            return false;
        }
    }

    private void getAllUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ArrayList<User> users = userDAO.consultar();
            ArrayList<Map<String, String>> usersInfo = new ArrayList<>();

            for (User user : users) {
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("id", String.valueOf(user.getId()));
                userInfo.put("name", user.getName());
                userInfo.put("email", user.getEmail());
                userInfo.put("created_at", user.getCreated_at());
                usersInfo.add(userInfo);
            }

            jResp.success(req, resp, usersInfo);
        } catch (Exception e) {
            jResp.failed(req, resp, "Error al obtener usuarios: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getUserById(HttpServletRequest req, HttpServletResponse resp, int userId) throws IOException {
        User user = userDAO.getUser(userId);

        if (user != null) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("id", String.valueOf(user.getId()));
            userInfo.put("name", user.getName());
            userInfo.put("email", user.getEmail());
            userInfo.put("created_at", user.getCreated_at());
            jResp.success(req, resp, userInfo);
        } else {
            jResp.failed(req, resp, "Usuario no encontrado", HttpServletResponse.SC_NOT_FOUND);
        }
    }
}