package persistencia.conexion;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.util.ArrayList;

public final class Conexion<T> {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String bd = dotenv.get("DB_NAME");
    private static final String usuario = dotenv.get("DB_USER");
    private static final String password = dotenv.get("DB_PASSWORD");
    private static final String host = dotenv.get("DB_HOST");
    private static final String puerto = dotenv.get("DB_PORT");
    private final String url;

    private Connection conexion;

    public Conexion() {
        url = "jdbc:mysql://" + host + ":" + puerto + "/" + bd;
    }

    public boolean abrir() {
        try {
            System.out.println("Intentando conectar a la base de datos...");
            System.out.println("URL: " + url);
            System.out.println("Usuario: " + usuario);
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, usuario, password);
            System.out.println("Conexión exitosa");
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection obtener() {
        return conexion;
    }

    public boolean cerrar() {
        try {
            conexion.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // Método para consultas sin parámetros
    public ArrayList<ArrayList<String>> ejecutarConsulta(String query, String[] columnas) {
        if (abrir()) {
            try {
                Statement st = this.conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                return procesarResultSet(rs, columnas);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                cerrar();
            }
        }
        return new ArrayList<>();
    }

    // Método para consultas con parámetros
    public ArrayList<ArrayList<String>> ejecutarConsultaPreparada(String query, Object[] params, String[] columnas) {
        if (abrir()) {
            try {
                PreparedStatement pstm = this.conexion.prepareStatement(query);

                // Establecer los parámetros
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        pstm.setObject(i + 1, params[i]);
                    }
                }

                ResultSet rs = pstm.executeQuery();
                return procesarResultSet(rs, columnas);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                cerrar();
            }
        }
        return new ArrayList<>();
    }

    private ArrayList<ArrayList<String>> procesarResultSet(ResultSet rs, String[] columnas) throws SQLException {
        ArrayList<ArrayList<String>> registros = new ArrayList<>();
        ResultSetMetaData metadata = rs.getMetaData();

        // Si no se especificaron columnas, obtener todas
        if (columnas == null || columnas.length == 0) {
            int numColumnas = metadata.getColumnCount();
            columnas = new String[numColumnas];
            for (int i = 1; i <= numColumnas; i++) {
                columnas[i-1] = metadata.getColumnName(i);
            }
        }

        while (rs.next()) {
            ArrayList<String> registro = new ArrayList<>();
            for (String columna : columnas) {
                registro.add(rs.getString(columna));
            }
            registros.add(registro);
        }

        return registros;
    }

    public boolean ejecutarActualizacion(String query, Object[] values) {
        if (this.abrir()) {
            try {
                PreparedStatement pstm = this.conexion.prepareStatement(query);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        pstm.setObject(i + 1, values[i]);
                    }
                }
                return pstm.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                cerrar();
            }
        }
        return false;
    }
}