# Proyecto Biometría – GTI 3A

## Descripción general

Proyecto desarrollado para la asignatura **Desarrollo de Sistemas Interactivos y Ciberfísicos (DSIC)** del Grado en Tecnologías Interactivas (GTI) – 3º curso.  
El sistema permite medir variables ambientales (**CO₂** y **temperatura**) mediante un dispositivo **Arduino con Bluetooth Low Energy (BLE iBeacon)** y almacenar las mediciones en una **base de datos MySQL**, accesibles a través de una **API REST en PHP**.  
Los datos pueden visualizarse en una **interfaz web** o en una **aplicación Android** con lógica fake para simulación.

---

## Estructura del proyecto

El proyecto sigue la estructura recomendada en la rúbrica DSIC GTI 3A:

```
ProyectoBiometria/
├── src/
│   ├── arduino/         → Código C++ del sistema de medición BLE
│   │   ├── Publicador.h
│   │   ├── EmisoraBLE.h
│   │   ├── LED.h
│   │   ├── Medidor.h
│   │   ├── PuertoSerie.h
│   │   └── ServicioEnEmisora.h
│   ├── servidor/             → Backend PHP con API REST
│   │   └── api/
│   │       └──index.php
│   └── cliente/         → Cliente web (HTML + CSS + JS)
│   │   ├── index.html
│   │   └── styles.css
│   └── telefono/
│   │   ├──AndroidManifest.xml
│   │   ├──ClienteApi.java
│   │   ├──GeneradorDatosFake.java
│   │   ├──MainAtivity.java
│   │   ├──TramaIbeacon.java
│   │   └──Utilidades.java
├── doc/                 → Documentación técnica y esquemas
├── test/                → Pendiente de incluir tests automáticos
└── README.md
```

El repositorio utiliza **Gitflow**, con ramas `main`, `develop` y `features`.  
Se realizan commits frecuentes documentando los avances del backend, frontend y firmware Arduino.

---

## Base de datos

- **Servidor:** MySQL  
- **Nombre:** `sfuenma_biometria`  
- **Tabla principal:** `Mediciones`

### Estructura de la tabla

| Campo | Tipo | Descripción |
|--------|------|-------------|
| id | INT AUTO_INCREMENT | Identificador único |
| id_sensor | INT | Tipo de sensor (11 = CO₂, 12 = Temperatura) |
| valor | FLOAT | Valor medido por el sensor |
| timestamp | TIMESTAMP | Fecha y hora de la medición |

El diseño está sincronizado con la implementación actual de la API.

---

## Backend

### API REST (PHP + MySQL)

Archivo principal: `/src/api/index.php`

#### Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| GET | `/api/index.php?endpoint=health` | Devuelve el estado del servicio (heartbeat). |
| GET | `/api/index.php?endpoint=mediciones` | Devuelve las últimas 50 mediciones (id, tipo, valor, fecha). |
| POST | `/api/index.php?endpoint=mediciones` | Inserta una nueva medición con los campos `id_sensor` y `valor`. |

#### Ejemplo de respuesta

```json
[
  {
    "id": 1,
    "tipo": 11,
    "valor": 735.42,
    "fecha": "2025-10-05 18:40:23"
  }
]
```

La API maneja errores comunes (conexión fallida, datos incompletos, endpoint no encontrado) y responde siempre en formato JSON.

---

## Lógica de negocio (Arduino BLE)

El firmware Arduino gestiona la **emisión de beacons BLE** que incluyen los valores medidos.  
Las clases implementadas permiten estructurar el sistema de manera modular:

| Archivo | Descripción |
|----------|-------------|
| Publicador.h | Publica los valores de CO₂ y temperatura como beacons. |
| EmisoraBLE.h | Controla la emisora BLE (inicio, configuración y anuncios). |
| Medidor.h | Simula la lectura de sensores. |
| LED.h | Control del LED integrado para señalización. |
| PuertoSerie.h | Comunicación serie para depuración. |
| ServicioEnEmisora.h | Implementa servicios BLE personalizados. |

El sistema transmite valores de ejemplo en formato iBeacon con UUID propio (`EPSG-GTI-PROY-3A`).

---

## Frontend

### Cliente web

Archivo principal: `/src/cliente/index.html`  
La interfaz permite visualizar los datos obtenidos desde la API en una tabla dinámica.

**Características:**
- Conexión automática al endpoint `./api/index.php?endpoint=mediciones`.  
- Visualización de ID, tipo, valor y fecha de cada registro.  
- Estilo minimalista con `styles.css`.  
- Mensajes de carga y manejo básico de errores.

**Acceso:**  
[https://sfuenma.upv.edu.es](https://sfuenma.upv.edu.es)

---

### Cliente Android

Desarrollado en **Android Studio**.  
Implementa una lógica fake para simular la recepción de datos del backend, lo que permite:
- Mostrar valores simulados de CO₂ y temperatura.  
- Visualizar alertas o indicadores de estado.  
- Mantener la misma estructura de lógica de negocio que la web.  

La lógica real y la fake están separadas, siguiendo el modelo de arquitectura recomendado.

---

## Tests

Actualmente no existen tests automáticos.  
Las pruebas se han realizado manualmente sobre:

- Funcionamiento del API con **Postman**.  
- Visualización correcta en el cliente web.  
- Emisión de datos BLE en Arduino.  
- Lógica simulada en Android.

**Pendiente para futuras versiones:**
- Tests unitarios automáticos para la API y la UI.  
- Verificación de integridad de datos tras inserciones.

---

## Despliegue

### En Plesk (hosting)

1. Subir la carpeta `/api` con el archivo `index.php` a `/httpdocs/api/`
2. Subir la carpeta `/cliente` (con `index.html` y `styles.css`) a `/httpdocs/`
3. Verificar conexión MySQL con credenciales:

```php
$host = "localhost:3306";
$user = "sfuenma";
$pass = "Sfuenmayor";
$db   = "sfuenma_biometria";
```

4. Acceso final:
   - **API:** [https://sfuenma.upv.edu.es/api/index.php?endpoint=mediciones](https://sfuenma.upv.edu.es/api/index.php?endpoint=mediciones)
   - **Web:** [https://sfuenma.upv.edu.es/](https://sfuenma.upv.edu.es/)

---

## Documentación y créditos

**Autor:** Santiago Fuenmayor Ruiz
**Asignatura:** Desarrollo de Sistemas Interactivos y Ciberfísicos (GTI 3A)  
**Universidad:** Universitat Politècnica de València – Campus de Gandía  
**Profesor:** Jordi Bataller i Mascarell  

---

## Estado del proyecto según la rúbrica DSIC GTI 3A

| Criterio | Estado | Comentario |
|-----------|---------|------------|
| Estructura del proyecto | ✅ | Organizado en `src`, `doc`, `test` y `README.md`. |
| Base de datos | ✅ | Diseño correcto y actualizado (tabla `Mediciones`). |
| Comentarios en código | ✅ | Todos los archivos principales están comentados. |
| Lógica de negocio | ✅ | Separada correctamente (Arduino / lógica fake Android). |
| API REST | ✅ | Implementada con endpoints funcionales y documentados. |
| Tests (Backend/UI) | ⚠️ | Solo pruebas manuales, sin automatización. |
| Arduino | ✅ | Sistema funcional con beacons BLE simulando sensores. |
| Android | ✅ | App funcional con lógica fake integrada. |

---

## Próximos pasos

- Añadir tests automáticos con Postman o PHPUnit.  
- Integrar la lectura real de sensores físicos (CO₂ / temperatura).  
- Sincronizar los datos en tiempo real entre Arduino y Android.  
- Mejorar la interfaz web con gráficos y actualización en vivo.
