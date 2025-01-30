<?php
    
    if(isset($argv[1])){
        //obtener el numero ingresado como argumento
        $tamano = intval($argv[1]);
        
        imprimirDiamante($tamano);
    }
    else{
        echo "Por favor, ingresa un número como argumento";
    }
function imprimirDiamante($tamano){
    if(!is_numeric($tamano) ||  $tamano<=0){
        echo"Por favor, ingrese un número positivo como argumento.";
        return false;
    }else{
//im<primir el diamante
for($i= 0; $i<=$tamano; $i++){
    //imprimir espacios en blanco para la alineación
    echo str_repeat(" ", $tamano-$i);
    //imprimir asteriscos para la parte superior del diamante
    echo str_repeat("* ", $i);
    echo "\n";
    }
    for ($i = $tamano-1 ; $i>= 1 ; $i--){
        //Imprimir espacios en blanco para la alineación
        echo str_repeat(" ",$tamano-$i);

        //imprimir asteriscos para la parte inferior del diamante
        echo str_repeat("* ", $i);

        echo "\n";
        }

    }

    }
