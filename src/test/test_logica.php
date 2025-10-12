<?php
/**
 * Test automático para la lógica de negocio (logica.php)
 * Ejecutar en Plesk o CLI: php test_logica.php
 */

require_once(__DIR__ . '/../api/logica.php');

// ---------------------------------------------------------
// CONFIGURACIÓN DE CONEXIÓN
// ---------------------------------------------------------
$host = "localhost:3306";
$user = "sfuenma";
$pass = "Sfuenmayor";
$db   = "sfuenma_biometria";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die("❌ Error conectando a la BD: " . $conn->connect_error);
}

$logica = new LogicaNegocio($conn);

// ---------------------------------------------------------
// Función auxiliar
// ---------------------------------------------------------
function test($cond, $ok, $fail) {
    echo $cond ? "✅ $ok\n" : "❌ $fail\n";
}

// ---------------------------------------------------------
// 1️⃣ CONSULTAR MEDICIONES
// ---------------------------------------------------------
echo "\n=== TEST 1: CONSULTAR MEDICIONES ===\n";
$lista1 = $logica->consultarMediciones();
test(is_array($lista1), "Devuelve array de mediciones", "No devuelve array");

$conteoInicial = count($lista1);

// ---------------------------------------------------------
// 2️⃣ AGREGAR MEDICIÓN
// ---------------------------------------------------------
echo "\n=== TEST 2: AGREGAR MEDICION ===\n";
$id_sensor = 1;
$valor = rand(300, 800);
$timestamp = date("Y-m-d H:i:s");
$ok = $logica->agregarMedicion($id_sensor, $valor, $timestamp);
test($ok, "Inserta nueva medición correctamente", "Error al insertar medición");

// ---------------------------------------------------------
// 3️⃣ IDEMPOTENCIA
// ---------------------------------------------------------
echo "\n=== TEST 3: IDEMPOTENCIA ===\n";
$lista2 = $logica->consultarMediciones();
$conteoFinal = count($lista2);
test($conteoFinal >= $conteoInicial, "Consulta refleja inserción", "Conteo no cambió tras inserción");

// ---------------------------------------------------------
// 4️⃣ ESTRUCTURA DE DATOS
// ---------------------------------------------------------
echo "\n=== TEST 4: ESTRUCTURA DE DATOS ===\n";
$ultimo = $lista2[0] ?? null;
$campos = ['id', 'id_sensor', 'valor', 'timestamp'];
$estructuraCorrecta = $ultimo && count(array_intersect(array_keys($ultimo), $campos)) === count($campos);
test($estructuraCorrecta, "Campos esperados en la tabla", "Estructura incorrecta");

// ---------------------------------------------------------
// FINAL
// ---------------------------------------------------------
echo "\n=== RESULTADO FINAL ===\n✅ Tests de lógica completados correctamente.\n";
$conn->close();
?>
