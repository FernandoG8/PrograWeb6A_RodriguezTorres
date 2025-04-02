package persistencia.conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConexionIndependiente {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/demo";
        String usuario = "root";
        String password = "Fernan";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection(url, usuario, password);
            System.out.println("✅ Conexión establecida exitosamente");
            conexion.close();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Error de SQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
        }
    }
}