<?php
function traducirMano($mano) {
    switch($mano) {
        case 0: return "Piedra";
        case 1: return "Papel";
        case 2: return "Tijera";
        case 3: return "Lagarto";
        case 4: return "Spock";
        default: return "Desconocido";
    }
}

function determinarGanador($mano1, $mano2) {
    // empate
    if ($mano1 === $mano2) {
        return 0; 
    }

    switch ($mano1) {
        case 0: // Piedra
            if ($mano2 === 2 || $mano2 === 3) return 1; 
            else return 2;
        case 1: // Papel
            if ($mano2 === 0 || $mano2 === 4) return 1;
            else return 2;
        case 2: // Tijera
            if ($mano2 === 1 || $mano2 === 3) return 1;
            else return 2;
        case 3: // Lagarto
            if ($mano2 === 1 || $mano2 === 4) return 1;
            else return 2;
        case 4: // Spock
            if ($mano2 === 0 || $mano2 === 2) return 1;
            else return 2;
    }

    return 0; // por cualquier cosa
}

if (isset($argv[1]) && isset($argv[2])) {
    $jugador1 = (int)$argv[1];
    $jugador2 = (int)$argv[2];

    echo "Jugador 1 eligió: " . traducirMano($jugador1) . "\n";
    echo "Jugador 2 eligió: " . traducirMano($jugador2) . "\n";

    $winner = determinarGanador($jugador1, $jugador2);
    if ($winner === 0) {
        echo "Resultado: Empate!" . "\n";
    } elseif ($winner === 1) {
        echo "Resultado: Gana el Jugador 1" . "\n";
    } else {
        echo "Resultado: Gana el Jugador 2" . "\n";
    }
  
} else {
    echo "Escribe: php piedra_papel_tijera_lagarto_spock.php [mano_jugador1] [mano_jugador2]" . "\n";
}
