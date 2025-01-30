<?php

function generarTablero($filas, $columnas, $minas){
$tablero = array_fill(0,$filas,array_fill(0,$columnas,0));
for($i = 0 ; $i<$minas ;$i++){
    do{
        $fila = rand(0,$filas-1);
        $columna = rand(0,$columnas-1);
    }while($tablero[$fila][$columna] == -1);
    
    $tablero[$fila][$columna] =-1;
    
    for($j = $fila-1; $j<=$fila+1; $j++){
        for($k = $columna-1; $k<=$columna+1; $k++){
            if($j>=0 && $j<$filas && $k>=0 && $k<$columnas && $tablero[$j][$k] !== -1){
                $tablero[$j][$k]++;
            }
        }
    }
}
return $tablero;
}
function imprimirTablero($tablero){
    foreach($tablero as $fila){
        foreach($fila as $casilla){
            if($casilla == -1){
                echo "* ";
            }else{
                echo $casilla." ";
            }
        }
        echo "\n";
    }

}
if(isset($argv[1]) && isset($argv[2]) && isset( $argv[3])){
    $filas = intval($argv[1]);
    $columnas = intval($argv[2]);
    $minas = intval($argv[3]);
    
    $tablero = generarTablero($filas,$columnas,$minas);
    imprimirTablero($tablero);
}else  {
    echo"Por favor, ingrese el número de filas, columnas y minas como argumentos.";
}
