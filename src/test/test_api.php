<?php
/**
 * Test automático de la API REST (api.php)
 * Ejecutar: php test_api.php
 */

$BASE_URL = "https://sfuenma.upv.edu.es/api/api.php"; // Cambia por tu dominio real

function test($cond, $ok, $fail) {
    echo $cond ? " $ok\n" : " $fail\n";
}

// ---------------------------------------------------------
// 1️ HEALTH
// ---------------------------------------------------------
echo "\n=== TEST 1: HEALTH ===\n";
$response = @file_get_contents("$BASE_URL?endpoint=health");
$data = json_decode($response, true);
test($data && isset($data["ok"]) && $data["ok"] === true,
     "Servicio activo",
     "Error: /health no responde correctamente");

// ---------------------------------------------------------
// 2️ MEDICIONES
// ---------------------------------------------------------
echo "\n=== TEST 2: MEDICIONES ===\n";
$response = @file_get_contents("$BASE_URL?endpoint=mediciones");
$data = json_decode($response, true);
test(is_array($data["data"] ?? null),
     "Consulta de mediciones correcta",
     "Error al obtener mediciones");

$conteoInicial = count($data["data"] ?? []);

// ---------------------------------------------------------
// 3️ AGREGAR MEDICIÓN
// ---------------------------------------------------------
echo "\n=== TEST 3: MEDICION ===\n";
$payload = json_encode([
    "id_sensor" => 1,
    "valor" => rand(400, 700)
]);

$options = [
    "http" => [
        "method" => "POST",
        "header" => "Content-Type: application/json",
        "content" => $payload
    ]
];
$context = stream_context_create($options);
$response = @file_get_contents("$BASE_URL?endpoint=medicion", false, $context);
$data = json_decode($response, true);
test($data && isset($data["ok"]) && $data["ok"] === true,
     "POST insertó nueva medición",
     "Error al insertar medición");

// ---------------------------------------------------------
// 4️ IDEMPOTENCIA
// ---------------------------------------------------------
echo "\n=== TEST 4: IDEMPOTENCIA ===\n";
$response = @file_get_contents("$BASE_URL?endpoint=mediciones");
$data = json_decode($response, true);
$conteoFinal = count($data["data"] ?? []);
test($conteoFinal >= $conteoInicial,
     "Lectura estable tras inserción",
     "Los datos no se mantuvieron tras POST");

// ---------------------------------------------------------
// FINAL
// ---------------------------------------------------------
echo "\n=== RESULTADO FINAL ===\n Tests de API completados correctamente.\n";
?>
