# 🔥 Tenpo Backend Challenge `microservicio backend`

Este repositorio contiene el código del **microservicio backend** que realiza cálculos con un porcentaje dinámico obtenido desde un servicio externo.

📌 **Autor:** Rodrigo Espinoza Aguayo  
📌 **Email:** rodrigo.espinoza.aguayo@gmail.com  

---

## 🚀 **Descripción del proyecto**
Este es un **microservicio** desarrollado en **Spring Boot 3.4.1** y **Java 21**, con soporte para programación reactiva utilizando **WebFlux**.  
Además, se implementa **Redis** para caching distribuido y **Rate Limiting** para controlar la cantidad de solicitudes por minuto.

✅ **Spring Boot 3.4.1** + **Java 21**  
✅ **Spring WebFlux** (API reactiva y no bloqueante)  
✅ **Redis** para almacenamiento en caché  
✅ **Rate Limiting** (Máximo 3 solicitudes por minuto)  
✅ **Flyway para la creación de tablas en PostgreSQL**  
✅ **Documentación con Swagger**  
✅ **Preparado para Docker y Docker Compose**  

📌 **Cálculo con porcentaje dinámico**  
Este servicio obtiene un **porcentaje desde una API externa** para aplicarlo en el cálculo.  
Dicha API externa es un **MOCK creado en Postman**, el cual está disponible públicamente en la siguiente URL:

🔗 **[https://42c939fb-7574-4341-91ca-b59c0ed06ddb.mock.pstmn.io/percentage](https://42c939fb-7574-4341-91ca-b59c0ed06ddb.mock.pstmn.io/percentage)**

---

## 📌 **Justificación de tecnologías utilizadas**

### **🔹 ¿Por qué Redis?**
Redis es una base de datos en memoria altamente eficiente, ideal para tareas de caching y control de tasas. Se eligió **Redis** por las siguientes razones:

✅ **Alto rendimiento**: Permite consultas rápidas en memoria para mejorar la respuesta del sistema.  
✅ **Soporte para almacenamiento en caché**: Reduce la latencia al evitar llamadas repetitivas a la API externa.  
✅ **Manejo eficiente del Rate Limiting**: Se usa Redis para controlar la cantidad de solicitudes por usuario en un período de tiempo determinado.  
✅ **Escalabilidad**: Redis funciona bien en entornos distribuidos y con múltiples instancias del microservicio.

### **🔹 ¿Por qué Spring WebFlux?**
Spring WebFlux es un framework reactivo que permite manejar un gran número de solicitudes concurrentes sin bloquear los hilos del servidor. Se utilizó **WebFlux** porque:

✅ **Alta concurrencia**: Maneja múltiples solicitudes sin necesidad de bloquear hilos, mejorando el rendimiento.  
✅ **Eficiencia en operaciones I/O**: Perfecto para servicios que consumen APIs externas y bases de datos.  
✅ **Integración con R2DBC**: Permite el acceso a bases de datos de manera no bloqueante, ideal para PostgreSQL en este caso.  
✅ **Escalabilidad y optimización de recursos**: Uso eficiente de los recursos del sistema, lo que permite manejar más solicitudes con menos hilos.  

### **🔹 ¿Por qué Flyway?**
Se eligió **Flyway** para la gestión de migraciones de base de datos porque:

✅ **Automatización de la creación de tablas**: Se usa para la creación automática de la tabla `api_call_log`.  
✅ **Soporte para WebFlux y R2DBC**: Como **Spring Data JPA no es compatible con WebFlux**, se necesita definir manualmente la estructura de la base de datos.  
✅ **Mantenimiento fácil de la base de datos**: Se pueden agregar versiones de migraciones sin afectar los datos existentes.

---

## 📦 **Requisitos previos**
Antes de ejecutar el servicio, asegúrate de tener instalado:

- **Java 21** ([Descargar JDK](https://adoptium.net/))
- **Maven** o **Gradle**
- **Docker y Docker Compose** (para entorno de contenedores)

---

## 🔧 **Configuración del entorno**
Este servicio puede ejecutarse de forma independiente o junto con una infraestructura completa usando **Docker Compose**.

📌 **Si usas Docker Compose**, no necesitas configurar nada manualmente.  
📌 **Si lo ejecutas de forma aislada o en un IDE**, considera las variables de entorno utilizadas en el `application.yml`.

### 🔹 Ejemplo de `application.yml`
```yaml
spring:
  application:
    name: tenpo-backend-challenge

  # Configuración de base de datos con R2DBC para WebFlux
  r2dbc:
    url: ${DATASOURCE_R2DBC_URL}
    username: ${DATASOURCE_USERNAME}
    password: ${DATASOURCE_PASSWORD}

  # Flyway permite la creación automática de tablas en PostgreSQL
  flyway:
    url: ${DATASOURCE_JDBC_URL}
    enabled: true
    locations: classpath:db/migration
    user: ${DATASOURCE_USERNAME}
    password: ${DATASOURCE_PASSWORD}

  # Configuración de REDIS
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}

# Configuración de Swagger con WebFlux
springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    operationsSorter: method
    configUrl: /v3/api-docs/swagger-config
    urls:
      - url: /v3/api-docs
        name: Default API

# Configuración de la API que entrega el porcentaje para el cálculo
percentage-api:
  url: ${PERCENTAGE_API_URL}
  uri: ${PERCENTAGE_API_URI}
```

---

## 🔹 **Variables de entorno para ejecución local**
Si deseas ejecutar el servicio en tu entorno local, puedes configurar las siguientes variables de entorno en **IntelliJ IDEA** o cualquier otro entorno compatible:

```env
DATASOURCE_PASSWORD=password;
DATASOURCE_R2DBC_URL=r2dbc:postgresql://localhost:5432/calculation_db;
DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/calculation_db;
DATASOURCE_USERNAME=user;
PERCENTAGE_API_URI=/percentage;
PERCENTAGE_API_URL=https://42c939fb-7574-4341-91ca-b59c0ed06ddb.mock.pstmn.io;
REDIS_HOST=127.0.0.1;
REDIS_PORT=6379
```

---

## ▶️ **Cómo ejecutar el servicio**
### **Ejecutar con Docker Compose**
Si deseas ejecutar el microservicio junto con **PostgreSQL y Redis**, sigue estos pasos:

1️⃣ Clona el repositorio de infraestructura:
```sh
  git clone https://github.com/codelious/tenpo-backend-challenge-infra.git
  cd tenpo-backend-challenge-infra
```

2️⃣ Levanta toda la infraestructura con **Docker Compose**:
```sh
  docker-compose up -d
```

📌 Esto iniciará:
- **PostgreSQL** (`postgres:15`)
- **Redis** (`redis:7.0`)
- **Tenpo Backend Challenge** (`codelious/tenpo-backend-challenge:latest`)

---

## 📡 **Endpoints de la API**

Una vez iniciados correctamente la infraestructura y sus servicios puedes acceder a los endpoints en:

```http
http://localhost:8080
```

La API expone los siguientes endpoints:

### 🔹 **Cálculo con porcentaje dinámico**
```http
GET /calculation?num1=10&num2=20
```
📌 **Descripción:**  
Realiza la suma de `num1` y `num2`, y aplica un porcentaje adicional obtenido desde un servicio externo.

📌 **Ejemplo de respuesta:**
```json
{
  "sum": 33.0
}
```

---

### 🔹 **Historial de llamadas a la API**
```http
GET /api-call-log?page=0&size=10
```
📌 **Descripción:**  
Obtiene un historial paginado de todas las llamadas realizadas a la API.

📌 **Ejemplo de respuesta:**
```json
[
  {
    "timestamp": "2025-01-29T12:34:56",
    "endpoint": "/calculation?num1=10&num2=20",
    "parameters": "num1=10&num2=20",
    "response": "{ \"sum\": 33.0 }",
    "httpStatus": 200
  }
]
```

---

## 🔐 **Control de tasas (Rate Limiting)**
El microservicio impone un límite de **3 solicitudes por minuto**.  
Si se excede este umbral, responde con un **error HTTP 429 (Too Many Requests)**.

📌 **Ejemplo de respuesta cuando se excede el límite:**
```json
{
  "error": "Too Many Requests: Límite de 3 solicitudes por minuto alcanzado."
}
```

---

## 📖 **Documentación con Swagger**
La documentación interactiva **Swagger** está disponible en:

📌 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 📖 **Colecciones de Postman**
Para facilitar las pruebas, el proyecto incluye **colecciones de Postman** listas para importar.  
📌 **Descarga las colecciones aquí:**  
🔗 [Colecciones de Postman](https://github.com/codelious/tenpo-backend-challenge/tree/main/docs/postman)

Para importarlas en Postman:
1. Abre Postman.
2. Ve a **File** > **Import**.
3. Selecciona los archivos JSON de la carpeta `docs/postman`.
4. Prueba los endpoints con las variables preconfiguradas.

---

## 🛑 **Cómo detener el servicio**
Si ejecutaste el servicio con **Docker Compose**, puedes detenerlo con:

```sh
  docker-compose down
```

---

## 📌 **Enlaces útiles**
- 👉 **[Tenpo Backend Challenge Infra en GitHub](https://github.com/codelious/tenpo-backend-challenge-infra)**
- 👉 **[Repositorio de este microservicio](https://github.com/codelious/tenpo-backend-challenge)**
- 👉 **[Colecciones de Postman](https://github.com/codelious/tenpo-backend-challenge/tree/main/docs/postman)**
