// URL del endpoint que devuelve la lista de mediciones (JSON)
const URL = "./api/api.php?endpoint=mediciones";

/**
 * cargarMediciones()
 * Hace un GET a la API, transforma la respuesta a JSON
 * y coloca las filas en la tabla. Muestra estados de carga y errores.
 */
async function cargarMediciones() {
  try {
    // Petición a la API
    const respuesta = await fetch(URL);
    const datos = await respuesta.json();

    // Referencias a elementos de la UI
    const tabla = document.getElementById("tabla");
    const contenido = document.getElementById("contenido-tabla");
    const loading = document.getElementById("loading");

    // Se oculta el loader y muestra la tabla al tener respuesta
    loading.style.display = "none";
    tabla.style.display = "table";

    // Si no hay datos (o la API indica ok=false), se muestra un placeholder
    if (!datos.ok || datos.data.length === 0) {
      contenido.innerHTML = `<tr><td colspan="4">No hay mediciones registradas</td></tr>`;
      return;
    }

    // Se coloca cada medición como una fila <tr>
    datos.data.forEach(fila => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${fila.id}</td>
        <td>${fila.id_sensor}</td>
        <td>${fila.valor}</td>
        <td>${fila.timestamp}</td>
      `;
      contenido.appendChild(tr);
    });

  } catch (error) {
    // Mensaje visible en pantalla y traza en consola si algo falla
    document.getElementById("loading").innerText = "Error al cargar los datos";
    console.error("Error al obtener datos:", error);
  }
}

// Inicio: se cargan las mediciones al abrir la página
cargarMediciones();
