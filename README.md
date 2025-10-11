# Proyecto Biometría – GTI 3A

## Descripción general

Proyecto desarrollado para la asignatura **Desarrollo de Sistemas Interactivos y Ciberfísicos (DSIC)** del Grado en Tecnologías Interactivas (GTI) – 3º curso.  
El sistema permite medir variables ambientales (**CO₂** y **temperatura**) mediante un dispositivo **Arduino con Bluetooth Low Energy (BLE iBeacon)** y almacenar las mediciones en una **base de datos MySQL**, accesibles a través de una **API REST en PHP**.  
Los datos pueden visualizarse en una **interfaz web** o en una **aplicación Android** con lógica de envío al backend.

---

## Estructura del proyecto

```
Sprint0_Biometria/
├── src/
│   ├── arduino/              → Código C++ del sistema de medición BLE
│   │   ├── Publicador.h
│   │   ├── EmisoraBLE.h
│   │   ├── LED.h
│   │   ├── Medidor.h
│   │   ├── PuertoSerie.h
│   │   └── ServicioEnEmisora.h
│   │
│   ├── servidor/             → Backend PHP con API REST
│   │   └── api/
│   │       ├── api.php
│   │       └── logica.php
│   │
│   ├── telefono/              → Aplicación móvil (Android Studio)
│   │   ├── MainActivity.java
│   │   ├── ClienteApi.java
│   │   ├── TramaIBeacon.java
│   │   └── Utilidades.java
│   │
│   └── cliente/                  → Cliente web (HTML + CSS + JS)
│       ├── index.html
│       ├── styles.css
│       └── script.js
│
└── doc/
    └── diseños/
        └── Documento de diseño Sprint_0.pdf
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

Archivo principal: `/src/servidor/api/api.php`

#### Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| GET | `/api/api.php?endpoint=health` | Devuelve el estado del servicio. |
| GET | `/api/api.php?endpoint=mediciones` | Devuelve las últimas mediciones. |
| POST | `/api/api.php?endpoint=mediciones` | Inserta una nueva medición (`id_sensor`, `valor`). |

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

## Firmware Arduino BLE

El firmware Arduino gestiona la **emisión de beacons BLE** que contienen los valores medidos.  
Las clases implementadas permiten estructurar el sistema de manera modular:

| Archivo | Descripción |
|----------|-------------|
| `Publicador.h` | Publica los valores de CO₂ y temperatura como beacons. |
| `EmisoraBLE.h` | Controla la emisora BLE (inicio, configuración y anuncios). |
| `Medidor.h` | Simula la lectura de sensores. |
| `LED.h` | Control del LED integrado. |
| `PuertoSerie.h` | Comunicación serie para depuración. |
| `ServicioEnEmisora.h` | Define servicios BLE personalizados. |

El sistema transmite valores de ejemplo en formato iBeacon con UUID propio (`EPSG-GTI-PROY-3A`).

---

## Cliente web

Archivo principal: `/src/web/index.html`  
La interfaz muestra los datos obtenidos desde la API en una tabla dinámica.

**Características:**
- Conexión automática al endpoint `/api/api.php?endpoint=mediciones`.  
- Visualización de ID, tipo, valor y fecha de cada registro.  
- Estilo simple con `styles.css`.  
- Mensajes de carga y manejo básico de errores.

---

## Aplicación Android

Desarrollada en **Android Studio**.  
La clase `ClienteApi` gestiona la comunicación con la API mediante peticiones HTTP, permitiendo:
- Enviar mediciones simuladas desde la app.  
- Validar respuestas del backend.  
- Integrar la lógica del sistema con otras clases (`MainActivity`, `TramaIBeacon`, `Utilidades`).  

---

## Tests

Las pruebas se han realizado manualmente sobre:

- Funcionamiento del API con **Postman**.  
- Visualización en el cliente web.  
- Emisión de datos BLE en Arduino.  
- Envío de mediciones desde Android.

**Pendiente de implementación:**
- Tests automáticos para la API y la UI.  
- Verificación automática de inserciones y respuestas.

---

## Despliegue

### En Plesk (hosting)

1. Subir la carpeta `/api` completa a `/httpdocs/api/`.  
2. Subir la carpeta `/web` a `/httpdocs/`.  
3. Verificar conexión MySQL con las credenciales:

```php
$host = "localhost:3306";
$user = "sfuenma";
$pass = "Sfuenmayor";
$db   = "sfuenma_biometria";
```

4. Accesos:
   - **API:** `https://sfuenma.upv.edu.es/api/api.php?endpoint=mediciones`  
   - **Web:** `https://sfuenma.upv.edu.es/`

---

## Documentación y créditos

**Autor:** Santiago Fuenmayor Ruiz  
**Asignatura:** Desarrollo de Sistemas Interactivos y Ciberfísicos (GTI 3A)  
**Universidad:** Universitat Politècnica de València – Campus de Gandía  
**Profesor:** Jordi Bataller i Mascarell  

---

## Próximos pasos

- Implementar tests automáticos (PHPUnit / Jest / JUnit).  
- Integrar sensores físicos para lecturas reales.  
- Sincronizar los datos en tiempo real entre Arduino, servidor y Android.  
- Mejorar la interfaz web con gráficos y actualización dinámica.
