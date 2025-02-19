<?php
class Usuario
{
    private $id;
    private $nombres;
    private $apellidos;
    private $correo;
    private $contrasena;

    public function __construct($id = null) {
        if ($id != null) {
            $usuarioDAO = new UsuarioDAO();
            $usuario = $usuarioDAO->buscar($id);
            $this->id = $usuario[0]['id'];
            $this->nombres = $usuario[0]['nombres'];
            $this->apellidos = $usuario[0]['apellidos'];
            $this->correo = $usuario[0]['correo'];
        }
    }

    // Getters y Setters
    public function getId()
    {
        return $this->id;
    }

    public function setId($id)
    {
        $this->id = $id;
    }

    public function getNombres()
    {
        return $this->nombres;
    }

    public function setNombres($nombres)
    {
        $this->nombres = $nombres;
        return $this;
    }

    public function getApellidos()
    {
        return $this->apellidos;
    }

    public function setApellidos($apellidos)
    {
        $this->apellidos = $apellidos;
        return $this;
    }

    public function getCorreo()
    {
        return $this->correo;
    }

    public function setCorreo($correo)
    {
        $this->correo = $correo;
        return $this;
    }

    public function getContrasena()
    {
        return $this->contrasena;
    }

    public function setContrasena($contrasena)
    {
        $this->contrasena = $contrasena;
    }

    public function guardar()
    {
        $usuarioDAO = new UsuarioDAO();
        return $usuarioDAO->insertar($this);
    }
    public function actualizar()  
    {  
        if ($this->id === null) {  
            throw new Exception("El usuario no se ha guardado en la base de datos; no es posible actualizar.");  
        }  
        $usuarioDAO = new UsuarioDAO();  
        return $usuarioDAO->actualizar($this);  
    }  
  
    public function eliminar()  
    {  
        if ($this->id === null) {  
            throw new Exception("El usuario no se ha guardado en la base de datos; no es posible eliminar.");  
        }  
        $usuarioDAO = new UsuarioDAO();  
        return $usuarioDAO->eliminar($this->id);  
    }

}
?>