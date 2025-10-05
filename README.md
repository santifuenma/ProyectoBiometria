Proyecto Biometría – GTI 3A
Descripción general

Proyecto desarrollado para la asignatura Desarrollo de Sistemas Interactivos y Ciberfísicos (DSIC) del Grado en Tecnologías Interactivas (GTI) – 3º curso.
El sistema permite medir variables ambientales (CO₂ y temperatura) mediante un dispositivo Arduino con Bluetooth Low Energy (BLE iBeacon) y almacenar las mediciones en una base de datos MySQL, accesibles a través de una API REST en PHP.
Los datos pueden visualizarse en una interfaz web o en una aplicación Android con lógica fake para simulación.

Estructura del proyecto

El proyecto sigue la estructura recomendada en la rúbrica DSIC GTI 3A:

ProyectoBiometria/
├── src/
│   ├── arduino/         → Código C++ del sistema de medición BLE
│   │   ├── Publicador.h
│   │   ├── EmisoraBLE.h
│   │   ├── LED.h
│   │   ├── Medidor.h
│   │   ├── PuertoSerie.h
│   │   └── ServicioEnEmisora.h
│   ├── api/             → Backend PHP con API REST
│   │   └── index.php
│   └── cliente/         → Cliente web (HTML + CSS + JS)
│       ├── index.html
│       └── styles.css
├── doc/                 → Documentación técnica y esquemas
├── test/                → Pendiente de incluir tests automáticos
└── README.md


El repositorio utiliza Gitflow, con ramas main, develop y features.
Se realizan commits frecuentes documentando los avances del backend, frontend y firmware Arduino.

Base de datos

Servidor: MySQL

Nombre: sfuenma_biometria

Tabla principal: Mediciones

Estructura de la tabla
Campo	Tipo	Descripción
id	INT AUTO_INCREMENT	Identificador único
id_sensor	INT	Tipo de sensor (11 = CO₂, 12 = Temperatura)
valor	FLOAT	Valor medido por el sensor
timestamp	TIMESTAMP	Fecha y hora de la medición

El diseño está sincronizado con la implementación actual de la API.

Backend
API REST (PHP + MySQL)

Archivo principal: /src/api/index.php

Endpoints disponibles
Método	Endpoint	Descripción
GET	/api/index.php?endpoint=health	Devuelve el estado del servicio (heartbeat).
GET	/api/index.php?endpoint=mediciones	Devuelve las últimas 50 mediciones (id, tipo, valor, fecha).
POST	/api/index.php?endpoint=mediciones	Inserta una nueva medición con los campos id_sensor y valor.
Ejemplo de respuesta
[
  {
    "id": 1,
    "tipo": 11,
    "valor": 735.42,
    "fecha": "2025-10-05 18:40:23"
  }
]


La API maneja errores comunes (conexión fallida, datos incompletos, endpoint no encontrado) y responde siempre en formato JSON.

Lógica de negocio (Arduino BLE)

El firmware Arduino gestiona la emisión de beacons BLE que incluyen los valores medidos.
Las clases implementadas permiten estructurar el sistema de manera modular:

Archivo	Descripción
Publicador.h	Publica los valores de CO₂ y temperatura como beacons.
EmisoraBLE.h	Controla la emisora BLE (inicio, configuración y anuncios).
Medidor.h	Simula la lectura de sensores.
LED.h	Control del LED integrado para señalización.
PuertoSerie.h	Comunicación serie para depuración.
ServicioEnEmisora.h	Implementa servicios BLE personalizados.

El sistema transmite valores de ejemplo en formato iBeacon con UUID propio (EPSG-GTI-PROY-3A).

Frontend
Cliente web

Archivo principal: /src/cliente/index.html
La interfaz permite visualizar los datos obtenidos desde la API en una tabla dinámica.

Características:

Conexión automática al endpoint ./api/index.php?endpoint=mediciones.

Visualización de ID, tipo, valor y fecha de cada registro.

Estilo minimalista con styles.css.

Mensajes de carga y manejo básico de errores.

Acceso:
https://sfuenma.upv.edu.es/

Cliente Android

Desarrollado en Android Studio.
Implementa una lógica fake para simular la recepción de datos del backend, lo que permite:

Mostrar valores simulados de CO₂ y temperatura.

Visualizar alertas o indicadores de estado.

Mantener la misma estructura de lógica de negocio que la web.

La lógica real y la fake están separadas, siguiendo el modelo de arquitectura recomendado.

Tests

Actualmente no existen tests automáticos.
Las pruebas se han realizado manualmente sobre:

Funcionamiento del API con Postman.

Visualización correcta en el cliente web.

Emisión de datos BLE en Arduino.

Lógica simulada en Android.

Pendiente para futuras versiones:

Tests unitarios automáticos para la API y la UI.

Verificación de integridad de datos tras inserciones.

Despliegue
En Plesk (hosting)

Subir la carpeta /api con el archivo index.php a /httpdocs/api/

Subir la carpeta /cliente (con index.html y styles.css) a /httpdocs/

Verificar conexión MySQL con credenciales:

$host = "localhost:3306";
$user = "sfuenma";
$pass = "Sfuenmayor";
$db   = "sfuenma_biometria";


Acceso final:

API → https://sfuenma.plesk.space/api/index.php?endpoint=mediciones

Web → https://sfuenma.plesk.space/

Documentación y créditos

Autor: Santi Fuenmayor
Asignatura: Desarrollo de Sistemas Interactivos y Ciberfísicos (GTI 3A)
Universidad: Universitat Politècnica de València – Campus de Gandía
Profesor: Jordi Bataller i Mascarell

Estado del proyecto según la rúbrica DSIC GTI 3A
Criterio	Estado	Comentario
Estructura del proyecto	✅	Organizado en src, doc, test y README.md.
Base de datos	✅	Diseño correcto y actualizado (tabla Mediciones).
Comentarios en código	✅	Todos los archivos principales están comentados.
Lógica de negocio	✅	Separada correctamente (Arduino / lógica fake Android).
API REST	✅	Implementada con endpoints funcionales y documentados.
Tests (Backend/UI)	⚠️	Solo pruebas manuales, sin automatización.
Arduino	✅	Sistema funcional con beacons BLE simulando sensores.
Android	✅	App funcional con lógica fake integrada.
Próximos pasos

Añadir tests automáticos con Postman o PHPUnit.

Integrar la lectura real de sensores físicos (CO₂ / temperatura).

Sincronizar los datos en tiempo real entre Arduino y Android.

Mejorar la interfaz web con gráficos y actualización en vivo.

¿Quieres que el siguiente paso sea que te genere un diagrama Mermaid para incluir justo después de la descripción (mostrando el flujo Arduino → API → BD → Web/Android)?
Queda muy profesional en GitHub y refuerza la rúbrica de documentación.

Tú dijiste:
que se vea asi
ChatGPT Plus

Perfecto, Santi 👌
Quieres que tu README.md del Proyecto Biometría – GTI 3A tenga el mismo formato estructurado y profesional que el de VIMYP, con títulos bien definidos (##, ###, ####), secciones “Frontend / Backend”, y viñetas claras.

Aquí te dejo el texto ya formateado con el mismo estilo que se ve en la captura.
Cópialo tal cual en tu archivo README.md — se verá igual que en el ejemplo que mostraste.

Proyecto Biometría – GTI 3A

Este proyecto contiene tanto el frontend como el backend del sistema de Biometría desarrollado para la asignatura Desarrollo de Sistemas Interactivos y Ciberfísicos (DSIC).
A continuación, se describe la estructura del proyecto y las tecnologías utilizadas.

Estructura del Proyecto

El proyecto se divide en tres partes principales: Arduino, Backend (API REST en PHP) y Frontend (Web y Android).
Cada módulo está diseñado de forma independiente para facilitar el mantenimiento y la escalabilidad del sistema.

Frontend

El directorio src/cliente/ contiene el código fuente de la interfaz web del proyecto Biometría.

Estructura de carpetas

index.html: Página principal de la interfaz web.

styles.css: Archivo de estilos globales y específicos de la interfaz.

img/: Contendrá imágenes o íconos si se añaden en versiones posteriores.

Archivos principales

index.html: Página principal del sistema.

Se conecta con el endpoint ./api/index.php?endpoint=mediciones para mostrar los datos en una tabla.

Incluye un script que obtiene las mediciones desde la base de datos y las muestra dinámicamente.

styles.css: Define el estilo visual de la página, con un diseño claro, minimalista y adaptable.

Tecnologías utilizadas

HTML5, CSS3 y JavaScript para la estructura, el estilo y la lógica de la aplicación web.

Fetch API para la comunicación con el backend (API REST).

Plesk como entorno de despliegue y hosting.

MySQL para la gestión de la base de datos.

Backend

El directorio src/api/ contiene el backend del sistema, implementado en PHP y conectado a una base de datos MySQL.
El archivo principal es index.php, que define los distintos endpoints de la API.

Estructura de carpetas

index.php: Archivo principal de la API REST.

(Otros archivos auxiliares no necesarios para el funcionamiento básico.)

Endpoints principales

GET /api/index.php?endpoint=health
Devuelve el estado de la API y la marca de tiempo actual.

GET /api/index.php?endpoint=mediciones
Devuelve las últimas 50 mediciones almacenadas en la base de datos.

POST /api/index.php?endpoint=mediciones
Inserta una nueva medición recibiendo los parámetros id_sensor y valor.

Ejemplo de respuesta
[
  {
    "id": 1,
    "tipo": 11,
    "valor": 735.42,
    "fecha": "2025-10-05 18:40:23"
  }
]

Tecnologías utilizadas

PHP 8+ para el desarrollo de la API REST.

MySQL como sistema de gestión de base de datos.

JSON como formato de intercambio de datos.

Postman para pruebas y validación manual de endpoints.

Lógica de negocio (Arduino)

El directorio src/arduino/ contiene el firmware que se ejecuta en el dispositivo con Bluetooth Low Energy (BLE).
El sistema mide valores de CO₂ y temperatura, y los publica como beacons iBeacon.

Archivos principales

Publicador.h: Publica los valores de CO₂ y temperatura.

EmisoraBLE.h: Controla la configuración y envío de paquetes BLE.

Medidor.h: Simula los sensores devolviendo valores de prueba.

LED.h: Controla el LED de estado del dispositivo.

PuertoSerie.h: Maneja la depuración por puerto serie.

ServicioEnEmisora.h: Define servicios BLE personalizados.

Tecnologías utilizadas

C++ / Arduino IDE para la programación del microcontrolador.

BLE iBeacon como protocolo de emisión inalámbrica.

ESP32 / nRF52840 como plataforma base.

Base de datos

El sistema utiliza una base de datos MySQL alojada en el servidor Plesk.

Estructura de la tabla
Campo	Tipo	Descripción
id	INT AUTO_INCREMENT	Identificador único
id_sensor	INT	Tipo de sensor (11 = CO₂, 12 = Temperatura)
valor	FLOAT	Valor medido por el sensor
timestamp	TIMESTAMP	Fecha y hora de la medición
Conexión a la base de datos (PHP)
$host = "localhost:3306";
$user = "sfuenma";
$pass = "Sfuenmayor";
$db   = "sfuenma_biometria";

Despliegue
En Plesk (hosting)

Subir la carpeta /api con el archivo index.php a /httpdocs/api/

Subir la carpeta /cliente (con index.html y styles.css) a /httpdocs/

Configurar la conexión MySQL con las credenciales indicadas.

Acceder desde el navegador a:

API: https://sfuenma.plesk.space/api/index.php?endpoint=mediciones

Web: https://sfuenma.plesk.space/

Aplicación Android

El proyecto incluye una aplicación Android con lógica fake que simula la recepción de datos del backend.

Características principales

Simulación de lectura de CO₂ y temperatura.

Interfaz visual con indicadores de estado.

Arquitectura modular con separación entre la lógica real y la fake.

Desarrollada en Android Studio.

Tests

Actualmente no existen tests automáticos.
Las pruebas se han realizado manualmente sobre:

Funcionamiento de la API con Postman.

Visualización correcta en el cliente web.

Emisión de datos BLE en Arduino.

Lógica simulada en Android.

Pendiente para futuras versiones:

Implementar tests unitarios automáticos para la API y la UI.

Verificación de integridad de datos tras inserciones.

Estado del Proyecto según la Rúbrica DSIC GTI 3A
Criterio	Estado	Comentario
Estructura del proyecto	Organizado en src, doc, test y README.md.
Base de datos	Diseño correcto y actualizado (tabla Mediciones).
Comentarios en código	Todos los archivos principales están comentados.
Lógica de negocio	Separada correctamente (Arduino / lógica fake Android).
API REST	Implementada con endpoints funcionales y documentados.
Tests (Backend/UI)	Solo pruebas manuales, sin automatización.
Arduino		Sistema funcional con beacons BLE simulando sensores.
Android		App funcional con lógica fake integrada.
Créditos

Autor: Santi Fuenmayor
Asignatura: Desarrollo de Sistemas Interactivos y Ciberfísicos (GTI 3A)
Universidad: Universitat Politècnica de València – Campus de Gandía
Profesor: Jordi Bataller i Mascarell  7