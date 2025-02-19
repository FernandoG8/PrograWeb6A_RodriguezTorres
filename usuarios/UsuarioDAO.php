<?php
require_once 'DataSource.php';
require_once 'IDao.php';
require_once 'Usuario.php';

class UsuarioDAO implements IDao
{
    private $dataSource;

    public function __construct()
    {
        $this->dataSource = new DataSource();
    }
    public function selectAll()
    {
        $sql = "SELECT * FROM usuarios";
        $data = $this->dataSource->ejecutarConsulta($sql);

        $usuarios = [];
        foreach ($data as $row) {
            $usuario = new Usuario();
            $usuario->setId($row['id']);
            $usuario->setNombres($row['nombres']);
            $usuario->setApellidos($row['apellidos']);
            $usuario->setCorreo($row['correo']);
            array_push($usuarios, $usuario);
        }

        return $usuarios;
    }

    public function insertar(Usuario $usuario)
    {
        // Verificar si ya existe un usuario con el mismo correo  
        $sqlCheck = "SELECT COUNT(*) as total FROM usuarios WHERE correo = :correo";
        $valuesCheck = [
            ':correo' => $usuario->getCorreo()
        ];
        $resultadoCheck = $this->dataSource->ejecutarConsulta($sqlCheck, $valuesCheck);

        // Si se encontró algun registro (total > 0), mandamos un mensaje de error
        if ($resultadoCheck[0]['total'] > 0) {
            error_log("El correo " . $usuario->getCorreo() . " ya existe en la base de datos.");
            return false;
        }

        $sql = "INSERT INTO usuarios (nombres, apellidos, correo) VALUES (:nombres, :apellidos, :correo)";
        $values = [
            ':nombres'   => $usuario->getNombres(),
            ':apellidos' => $usuario->getApellidos(),
            ':correo'    => $usuario->getCorreo()
        ];
        $resultado = $this->dataSource->ejecutarActualizacion($sql, $values);
        // Si se insertó correctamente, actualizamos el id del objeto, es necesario para poder realizar otras operaciones con el objeto
        if ($resultado > 0) {
            $nuevoId = $this->dataSource->getConexion()->lastInsertId();
            $usuario->setId($nuevoId);
        }
        return $resultado;
    }

    public function actualizar(Usuario $usuario)
    {
        $sql = "UPDATE usuarios SET nombres = :nombres, apellidos = :apellidos, correo = :correo WHERE id = :id";
        $values = [
            ':nombres' => $usuario->getNombres(),
            ':apellidos' => $usuario->getApellidos(),
            ':correo' => $usuario->getCorreo(),
            ':id' => $usuario->getId()
        ];
        return $this->dataSource->ejecutarActualizacion($sql, $values);
    }

    public function eliminar($id)
    {

        $sql = "DELETE FROM usuarios WHERE id = :id";
        $values = [
            ':id' => $id
        ];
        return $this->dataSource->ejecutarActualizacion($sql, $values);
    }

    public function buscar($id)
    {
        $sql = "SELECT * FROM usuarios WHERE id = :id";
        $values = [
            'id' => $id
        ];
        $result = $this->dataSource->ejecutarConsulta($sql, $values);
    }

    public function buscarTodos()
    {
        $sql = "SELECT * FROM usuarios";
        $data = $this->dataSource->ejecutarConsulta($sql);

        $usuarios = [];

        foreach ($data as $usuario) {
            $usuario = new Usuario($usuario['id']);
            array_push($usuarios, $usuario);
        }

        return $usuarios;
    }
}
