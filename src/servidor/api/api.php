<?php
/**
 * Archivo: api.php
 * Descripción: API REST del Proyecto Biometría.
 * Endpoints: health, mediciones, medicion.
 */

header("Content-Type: application/json");
require_once(__DIR__ . '/logica.php');

// ---------------------------------------------------------
// Conexión a la base de datos
// ---------------------------------------------------------
$host = "localhost:3306";
$user = "sfuenma";
$pass = "Sfuenmayor";
$db   = "sfuenma_biometria";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["ok" => false, "error" => $conn->connect_error]);
    exit;
}

// ---------------------------------------------------------
// Inicializar la lógica de negocio
// ---------------------------------------------------------
$logica = new LogicaNegocio($conn);
$endpoint = $_GET['endpoint'] ?? '';
$method = $_SERVER["REQUEST_METHOD"];

// ---------------------------------------------------------
// GET /health → Estado del servicio
// ---------------------------------------------------------
if ($method === "GET" && $endpoint === "health") {
    echo json_encode([
        "ok" => true,
        "service" => "api-biometria",
        "ts" => time()
    ]);
    exit;
}

// ---------------------------------------------------------
// GET /mediciones → Consultar todas las mediciones
// ---------------------------------------------------------
if ($method === "GET" && $endpoint === "mediciones") {
    $data = $logica->consultarMediciones();
    echo json_encode(["ok" => true, "data" => $data]);
    exit;
}

// ---------------------------------------------------------
// POST /medicion → Agregar nueva medición
// ---------------------------------------------------------
if ($method === "POST" && $endpoint === "medicion") {
    $data = json_decode(file_get_contents("php://input"), true);

    // Validar parámetros
    if (!isset($data["id_sensor"], $data["valor"])) {
        http_response_code(400);
        echo json_encode(["ok" => false, "error" => "Faltan parámetros"]);
        exit;
    }

    $id_sensor = (int)$data["id_sensor"];
    $valor = (float)$data["valor"];
    $timestamp = date("Y-m-d H:i:s"); // Fecha/hora actual desde PHP

    $ok = $logica->agregarMedicion($id_sensor, $valor, $timestamp);
    echo json_encode(["ok" => $ok]);
    exit;
}

// ---------------------------------------------------------
// Si no coincide con ningún endpoint
// ---------------------------------------------------------
http_response_code(404);
echo json_encode(["ok" => false, "error" => "Endpoint no encontrado"]);
?>
