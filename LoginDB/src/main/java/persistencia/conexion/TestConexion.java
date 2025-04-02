package persistencia.conexion;

import persistencia.dao.UserDAO;
import persistencia.modelo.User;

import java.util.ArrayList;

public class TestConexion {
    public static void main(String[] args) {
        Conexion<Object> conexion = new Conexion<>();
        ArrayList<User> users = new ArrayList<>();
        UserDAO userDAO = new UserDAO();
        System.out.println( userDAO.consultar());
        //prueba de todos los metodos de userDAO
        System.out.println(userDAO.existsUserByNameOrEmail("juanchuc", "jachucme@gmail.com"));
    }

}