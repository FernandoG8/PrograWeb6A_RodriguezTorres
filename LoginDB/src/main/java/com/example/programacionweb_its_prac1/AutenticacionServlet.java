package com.example.programacionweb_its_prac1;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import io.jsonwebtoken.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import persistencia.dao.UserDAO;
import persistencia.modelo.User;
@WebServlet("/autenticacion-servlet/*")

/**
 * Clase que contiene los siguientes endpoints:
 * - register
 * - login
 * - logout
 */
public class AutenticacionServlet extends HttpServlet {
    private static final String SECRET_KEY = "mWQKjKflpJSqyj0nDdSG9ZHE6x4tNaXGb35J6d7G5mo=";
    private static final long EXPIRATION_TIME = 300000;
    public static final Map<String, User> users = new HashMap<>();
    private final JsonResponse jResp = new JsonResponse();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        jResp.failed(req, resp, "404 - Recurso no encontrado", HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            jResp.failed(req, resp, "Ruta no válida", HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Manejar las rutas POST
        switch (pathInfo) {
            case "/register":
                register(req, resp);
                break;
            case "/login":
                login(req, resp);
                break;
            case "/logout":
                logout(req, resp);
                break;
            default:
                jResp.failed(req, resp, "Ruta no válida", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Metodo que se utiliza para el endpoint /autenticacion-servlet/register de tipo POST
     * Se encarga de registrar un usuario en el sistema, recibe los siguientes parametros:
     * - username
     * - password
     * - fullName
     * - email
     * 
     * Si alguno de los parametros es nulo, se responde con un mensaje de error, en caso contrario
     * se encripta la contraseña y se crea un nuevo usuario con los datos proporcionados.
     * @param req
     * @param resp
     * @throws IOException
     */
    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");

        // Validación de campos obligatorios
        if (username == null || password == null || fullName == null || email == null ||
                username.trim().isEmpty() || password.trim().isEmpty() ||
                fullName.trim().isEmpty() || email.trim().isEmpty()) {
            jResp.failed(req, resp, "Todos los campos son obligatorios", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            // Verificar si el usuario ya existe
            UserDAO userDAO = new UserDAO();
            if (userDAO.existsUserByNameOrEmail(username.trim(), email.trim())) {
                jResp.failed(req, resp, "El usuario o email ya existe", 422);
                return;
            }

            // Encriptar la contraseña
            String encryptedPassword = encryptPassword(password);

            // Crear el nuevo usuario
            User newUser = new User(
                    0,              // El ID será asignado por la base de datos
                    fullName.trim(),
                    email.trim(),
                    encryptedPassword,
                    null           // created_at será manejado por la base de datos
            );

            // Guardar el usuario en la base de datos
            if (userDAO.agregar(newUser)) {
                Map<String, String> responseData = new HashMap<>();
                responseData.put("message", "Usuario registrado exitosamente");
                responseData.put("name", newUser.getName());
                responseData.put("email", newUser.getEmail());
                jResp.success(req, resp, responseData);
            } else {
                jResp.failed(req, resp, "Error al registrar el usuario", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jResp.failed(req, resp, "Error en el servidor: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Metodo que se utiliza para el endpoint /autenticacion-servlet/login de tipo POST
     * Se encarga de autenticar un usuario en el sistema, recibe los siguientes parametros:
     * - username
     * - password
     * 
     * Si el usuario no existe o la contraseña es incorrecta, se responde con un mensaje de error,
     * en caso contrario se genera un token JWT y se responde con un mensaje de éxito.
     * @param req
     * @param resp
     * @throws IOException
     */
    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String identifier = req.getParameter("name");
        String password = req.getParameter("password");

        // Validación de campos obligatorios
        if (identifier == null || password == null ||
                identifier.trim().isEmpty() || password.trim().isEmpty()) {
            jResp.failed(req, resp, "Credenciales incompletas", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            // Buscar usuario por name o email
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByNameOrEmail(identifier.trim());

            if (user != null && verifyPassword(password, user.getPassword())) {
                // Generar token JWT
                Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
                String token = Jwts.builder()
                        .header()
                        .keyId(SECRET_KEY)
                        .and()
                        .subject(user.getEmail())
                        .issuedAt(new Date())
                        .expiration(expirationDate)
                        .signWith(generalKey())
                        .compact();

                // Preparar respuesta
                Map<String, String> responseData = new HashMap<>();
                responseData.put("message", "Inicio de sesión exitoso");
                responseData.put("token", token);
                responseData.put("name", user.getName());
                responseData.put("email", user.getEmail());

                jResp.success(req, resp, responseData);
            } else {
                jResp.failed(req, resp, "Credenciales incorrectas", HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jResp.failed(req, resp, "Error en el servidor: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Metodo que se utiliza para el endpoint /autenticacion-servlet/logout de tipo POST
     * Se encarga de cerrar la sesión de un usuario en el sistema.
     * @param req
     * @param resp
     * @throws IOException
     */
    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Logged out successfully");
    }

    /**
     * Metodo que se encarga de encriptar una contraseña
     * @param password Contraseña a encriptar
     * @return String con la contraseña encriptada
     */
    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Metodo que se encarga de verificar si una contraseña es correcta
     * @param inputPassword Contraseña ingresada por el usuario
     * @param storedPassword Contraseña almacenada en la base de datos (HasMap)
     * @return true si la contraseña es correcta, false en caso contrario
     */
    private boolean verifyPassword(String inputPassword, String storedPassword) {
        return BCrypt.checkpw(inputPassword, storedPassword);
    }

    /**
     * Metodo que se encarga de generar una clave secreta
     * @return SecretKey con la clave secreta generada
     */
    public static SecretKey generalKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
