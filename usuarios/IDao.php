<?php
interface IDao {
    public function insertar(Usuario $usuario);
    public function actualizar(Usuario $usuario);
    public function eliminar($id);
    public function buscar($id);
    public function buscarTodos();
}
?>