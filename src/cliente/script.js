const URL = "./api/api.php?endpoint=mediciones";

async function cargarDatos() {
  try {
    const respuesta = await fetch(URL);
    const datos = await respuesta.json();

    const tabla = document.getElementById("tabla");
    const contenido = document.getElementById("contenido-tabla");
    const loading = document.getElementById("loading");

    loading.style.display = "none";
    tabla.style.display = "table";

    if (!datos.ok || datos.data.length === 0) {
      contenido.innerHTML = `<tr><td colspan="4">No hay mediciones registradas</td></tr>`;
      return;
    }

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
    document.getElementById("loading").innerText = "Error al cargar los datos";
    console.error("Error al obtener datos:", error);
  }
}

cargarDatos();
