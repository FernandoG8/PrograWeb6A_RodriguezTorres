package persistencia.dao;

import persistencia.modelo.User;
import persistencia.conexion.Conexion;
import java.util.ArrayList;

public class UserDAO implements DAOGeneral<Integer, User> {
    private final Conexion<User> c;

    public UserDAO() {
        c = new Conexion<>();
    }

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        System.out.println(userDAO.consultar());
    }

    @Override
    public boolean agregar(User user) {
        String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        Object[] values = {
                user.getName(),
                user.getEmail(),
                user.getPassword()
        };

        try {
            return c.ejecutarActualizacion(query, values);
        } catch (Exception e) {
            System.err.println("Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<User> consultar() {
        String query = "SELECT id, name, email, password, created_at FROM users";
        ArrayList<User> users = new ArrayList<>();

        try {
            // Especificar las columnas que queremos obtener
            String[] columnas = {"id", "name", "email", "password", "created_at"};
            ArrayList<ArrayList<String>> resultado = c.ejecutarConsulta(query, columnas);

            if (resultado != null && !resultado.isEmpty()) {
                for (ArrayList<String> fila : resultado) {
                    if (fila != null && fila.size() >= 5) {
                        try {
                            users.add(new User(
                                    Integer.parseInt(fila.get(0)), // id
                                    fila.get(1),                   // name
                                    fila.get(2),                   // email
                                    fila.get(3),                   // password
                                    fila.get(4)                    // created_at
                            ));
                        } catch (Exception e) {
                            System.err.println("Error al procesar usuario: " + e.getMessage());
                        }
                    }
                }
            } else {
                System.out.println("No se encontraron usuarios en la base de datos.");
            }
        } catch (Exception e) {
            System.err.println("Error al consultar usuarios: " + e.getMessage());
        }

        return users;
    }

    @Override
    public boolean actualizar(Integer id, User nuevo) {
        String query = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
        Object[] values = {nuevo.getName(), nuevo.getEmail(), nuevo.getPassword(), id};
        try {
            return c.ejecutarActualizacion(query, values);
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public User getUser(int id) {
        String query = "SELECT id, name, email, created_at FROM users WHERE id = ?";
        Object[] values = {id};
        String[] columnas = {"id", "name", "email", "created_at"};

        try {
            ArrayList<ArrayList<String>> res = c.ejecutarConsultaPreparada(query, values, columnas);
            if (!res.isEmpty()) {
                ArrayList<String> r = res.get(0);
                return new User(
                        Integer.parseInt(r.get(0)), // id
                        r.get(1),                   // name
                        r.get(2),                   // email
                        null,                       // password (no se incluye en la consulta)
                        r.get(3)                    // created_at
                );
            }
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean eliminar(Integer id) {
        String query = "DELETE FROM users WHERE id = ?";
        Object[] values = {id};
        try {
            return c.ejecutarActualizacion(query, values);
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean existsUserByNameOrEmail(String name, String email) {
        String query = "SELECT COUNT(*) as count FROM users WHERE name = ? OR email = ?";
        Object[] values = {name, email};
        String[] columnas = {"count"};

        try {
            ArrayList<ArrayList<String>> result = c.ejecutarConsultaPreparada(query, values, columnas);
            if (!result.isEmpty() && !result.get(0).isEmpty()) {
                return Integer.parseInt(result.get(0).get(0)) > 0;
            }
        } catch (Exception e) {
            System.err.println("Error al verificar existencia de usuario: " + e.getMessage());
        }
        return false;
    }

    public User getUserByNameOrEmail(String identifier) {
        String query = "SELECT id, name, email, password, created_at FROM users WHERE email = ? OR name = ?";
        Object[] values = {identifier, identifier};
        String[] columnas = {"id", "name", "email", "password", "created_at"};

        try {
            ArrayList<ArrayList<String>> result = c.ejecutarConsultaPreparada(query, values, columnas);
            if (!result.isEmpty()) {
                ArrayList<String> userData = result.get(0);
                return new User(
                        Integer.parseInt(userData.get(0)),  // id
                        userData.get(1),                    // name
                        userData.get(2),                    // email
                        userData.get(3),                    // password
                        userData.get(4)                     // created_at
                );
            }
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por nombre o email: " + e.getMessage());
        }
        return null;
    }
}