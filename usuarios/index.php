<?php
include 'Usuario.php';
require_once __DIR__ . '/UsuarioDAO.php';

$usuarioDAO = new UsuarioDAO();

$bugs = new Usuario();
$bugs->setNombres('Bugs');
$bugs->setApellidos('Bunny');
$bugs->setCorreo('bugsbunny@wb.com');
$bugs->guardar();

$lola = new Usuario();
$lola->setNombres('Lola');
$lola->setApellidos('Bunny');
$lola->setCorreo('lolabunny@wb.com');
$lola->guardar();

$lucas = new Usuario();
$lucas->setNombres('Lucas');
$lucas->setApellidos('Daffy');
$lucas->setCorreo('patolucas@wb.com');
$lucas->guardar();

$porky = new Usuario();
$porky->setNombres('Porky');
$porky->setApellidos('Pig');
$porky->setCorreo('porkypig@wb.com');
$porky->guardar();

$usuarios = $usuarioDAO->selectAll();


foreach ($usuarios as $usuario) {
    echo $usuario->getNombres() . ' ' . $usuario->getApellidos() . ' - ' . $usuario->getCorreo() . '<br>';
}
echo "<br>";

$porky->setCorreo("porkyphp@web.com");
$porky->actualizar();

$bugs->eliminar();

$usuarios = $usuarioDAO->selectALL();
foreach ($usuarios as $usuario) {
    echo $usuario->getNombres() . " " . $usuario->getApellidos() . " " . $usuario->getCorreo() . "<br>";
}

?>