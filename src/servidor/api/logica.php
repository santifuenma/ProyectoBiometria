<?php
/**
 * Archivo: logica.php
 * Descripción: Lógica de negocio del sistema Biometría.
 * Contiene los métodos para consultar y agregar mediciones.
 */

class LogicaNegocio {
    private $conn;

    public function __construct($conexion) {
        $this->conn = $conexion;
    }

    /**
     * consultarMediciones()
     * Devuelve todas las mediciones registradas en la base de datos.
     */
    public function consultarMediciones() {
        $sql = "SELECT * FROM Mediciones ORDER BY id DESC";
        $res = $this->conn->query($sql);
        $mediciones = [];

        while ($fila = $res->fetch_assoc()) {
            $mediciones[] = $fila;
        }

        return $mediciones;
    }

    /**
     * agregarMedicion()
     * Inserta una nueva medición (id_sensor + valor + timestamp).
     */
    public function agregarMedicion($id_sensor, $valor, $timestamp) {
        $stmt = $this->conn->prepare("INSERT INTO Mediciones (id_sensor, valor, timestamp) VALUES (?, ?, ?)");
        $stmt->bind_param("ids", $id_sensor, $valor, $timestamp);
        return $stmt->execute();
    }
}
?>
