<?php
// Se define que la API responderá siempre en formato JSON
header("Content-Type: application/json");

// Se configuran los datos de conexión a la base de datos
$host = "localhost:3306";
$user = "sfuenma";       
$pass = "Sfuenmayor";           
$db   = "sfuenma_biometria";

// Se abre la conexión con la base de datos
$conn = new mysqli($host, $user, $pass, $db);

// Si ocurre un error en la conexión, se devuelve un error 500
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["ok" => false, "error" => $conn->connect_error]);
    exit();
}

// Se obtiene el endpoint desde la URL (?endpoint=xxx)
$endpoint = $_GET['endpoint'] ?? '';

// ------------------------------------------------------------
// Endpoint GET /health → se devuelve un estado de la API
if ($_SERVER["REQUEST_METHOD"] === "GET" && $endpoint === "health") {
    echo json_encode([
        "ok" => true,
        "service" => "api-biometria",
        "ts" => time() // se devuelve la marca de tiempo actual
    ]);
    exit();
}

// ------------------------------------------------------------
// Endpoint GET /mediciones → se devuelven las últimas mediciones
if ($_SERVER["REQUEST_METHOD"] === "GET" && $endpoint === "mediciones") {
    // Se consultan las últimas 50 mediciones ordenadas por fecha descendente
    $result = $conn->query("SELECT id, id_sensor AS tipo, valor, timestamp AS fecha FROM Mediciones ORDER BY timestamp DESC LIMIT 50");

    $rows = [];
    while ($row = $result->fetch_assoc()) {
        $rows[] = $row; // se guarda cada fila en un array
    }

    // Se devuelve el array completo en formato JSON
    echo json_encode($rows);
    exit();
}


// ------------------------------------------------------------
// Endpoint POST /mediciones → se inserta una nueva medición
if ($_SERVER["REQUEST_METHOD"] === "POST" && $endpoint === "mediciones") {
    // Se leen los datos enviados en el cuerpo de la petición (JSON)
    $data = json_decode(file_get_contents("php://input"), true);

    // Se valida que los campos requeridos estén presentes
    if (!isset($data["id_sensor"]) || !isset($data["valor"])) {
        http_response_code(400);
        echo json_encode(["ok" => false, "error" => "id_sensor y valor son requeridos"]);
        exit();
    }

    // Se convierten los valores a enteros/decimales seguros
    $id_sensor = intval($data["id_sensor"]);
    $valor = floatval($data["valor"]);

    // Se construye la consulta SQL de inserción con la fecha actual
    $sql = "INSERT INTO Mediciones (id_sensor, valor, timestamp) VALUES ($id_sensor, $valor, NOW())";

    // Si la inserción se ejecuta correctamente, se devuelve ok
    if ($conn->query($sql) === TRUE) {
        echo json_encode(["ok" => true, "id" => $conn->insert_id]);
    } else {
        // Si ocurre un error en la inserción, se devuelve error 500
        http_response_code(500);
        echo json_encode(["ok" => false, "error" => $conn->error]);
    }
    exit();
}

// ------------------------------------------------------------
// Si no coincide ningún endpoint definido, se devuelve error 404
http_response_code(404);
echo json_encode(["ok" => false, "error" => "Endpoint no encontrado"]);


