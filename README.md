# 🚀 OMC Leads API - Clean Architecture

API REST profesional para la gestión estratégica de leads, diseñada bajo los principios de **Clean Architecture** y el scaffold oficial de **Bancolombia**. Este proyecto permite centralizar la captura de prospectos, generar analítica de conversión y obtener resúmenes ejecutivos potenciados por Inteligencia Artificial (Mock).

## 1. 📌 Descripción del Proyecto
Esta API resuelve la necesidad de gestionar el ciclo de vida de prospectos (leads) de forma eficiente y escalable.

### Características principales:
*   **Gestión Integral (CRUD):** Creación, consulta, actualización parcial (PATCH) y eliminación lógica (Soft Delete) de leads.
*   **Filtros Avanzados:** Búsqueda por fuente (`instagram`, `facebook`, etc.) y rangos de fechas con paginación optimizada.
*   **Dashboard de Estadísticas:** Endpoint especializado para obtener métricas de conversión, promedios de presupuesto y leads recientes.
*   **Executive AI Summary:** Generación de resúmenes estratégicos basados en los datos de los leads, utilizando una arquitectura desacoplada para IA.
*   **Persistencia Robusta:** Integración con PostgreSQL y precarga de datos (Seed) para pruebas inmediatas.

---

## 2. 🧱 Arquitectura
El proyecto sigue rigurosamente los principios de **Arquitectura Limpia**, separando las reglas de negocio de los detalles de infraestructura:

*   **Domain (Núcleo):**
    *   **Model:** Entidades de negocio (`Lead`, `LeadStats`) y contratos/puertos (`LeadRepository`, `AiSummaryGateway`).
    *   **UseCase:** Lógica pura de negocio (Crear, Listar, Estadísticas, Resumen IA).
*   **Infrastructure (Adaptadores):**
    *   **Entry Points:** API REST MVC documentada con Swagger.
    *   **Driven Adapters:** Persistencia con JPA Repository y un **Mock de IA** para la generación de resúmenes sin dependencia de APIs externas.

### Desacople de IA:
La lógica de IA está definida mediante el puerto `AiSummaryGateway`. Esto permite que el sistema sea agnóstico al proveedor (OpenAI, Anthropic, Mock), permitiendo cambiar la implementación en segundos sin tocar la lógica de negocio.

---

## 3. 🚀 Tecnologías utilizadas
*   **Java 21** (LTS)
*   **Spring Boot 3.x** (Web MVC, Data JPA, Validation)
*   **PostgreSQL 15**
*   **Docker & Docker Compose**
*   **SpringDoc OpenAPI** (Swagger UI)
*   **Lombok**
*   **Gradle 9.x**

---

## 4. ⚙️ Cómo ejecutar el proyecto

### 4.1 Requisitos previos
*   Java 21 instalado.
*   Docker y Docker Compose instalados.

### 4.2 Levantar base de datos

Desde la raíz del proyecto, ejecuta:

```bash
docker compose up -d
```

*Esto levantará una instancia de PostgreSQL en el puerto `5432` con la base de datos `omc_leads_db`.*

### 4.3 Ejecutar la aplicación
El script de inicialización (`schema.sql`) creará automáticamente las tablas e insertará **12 registros de prueba** (Seed).

```bash
./gradlew bootRun
```

*La API estará disponible en: `http://localhost:8080`*

---

## 5. 📖 Documentación de la API
La API está totalmente documentada y disponible para pruebas visuales a través de **Swagger UI**:

*   **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **OpenAPI Specs (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 6. 🛠 Endpoints Principales

### Leads (CRUD)
*   `POST /leads`: Crear un lead (Valida email único y formato).
*   `GET /leads`: Listado paginado con filtros (`?source=facebook&startDate=2023-11-01`).
*   `PATCH /leads/{id}`: Actualización parcial inteligente.
*   `DELETE /leads/{id}`: Soft delete (mantiene integridad referencial).

### Inteligencia y Analítica
*   `GET /leads/stats`: Métricas de negocio (Totales por fuente, promedios, activos).
*   `POST /leads/ai/summary`: Genera un resumen ejecutivo dinámico. Acepta filtros en el body para resúmenes segmentados.

---

## 7. ✅ Calidad y Validación
Para asegurar la consistencia de la arquitectura y la integridad del proyecto, puedes ejecutar:

**Validación de estructura del scaffold:**

```bash
./gradlew validateStructure
```

**Ejecución de pruebas unitarias:**

```bash
./gradlew test
```

---
**Desarrollado como prueba técnica para One Million Copy SAS.**
