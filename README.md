# OMC Leads API

API REST para gestión de leads construida como prueba técnica backend sobre el scaffold de Clean Architecture de Bancolombia. El proyecto centraliza la creación, consulta, actualización, eliminación lógica, analítica básica y generación de un resumen ejecutivo desacoplado mediante un puerto de IA actualmente implementado con un mock.

## Descripción

La API resuelve el flujo básico de administración de prospectos comerciales:

- crear leads con validación de negocio
- consultar leads por id
- listar leads con paginación y filtros
- actualizar leads parcialmente
- aplicar eliminación lógica
- consultar estadísticas agregadas
- generar un resumen ejecutivo a partir de los leads filtrados

El alcance implementado está orientado al backend y a la separación clara entre dominio, casos de uso e infraestructura.

## Arquitectura

El proyecto sigue el enfoque de Clean Architecture del scaffold de Bancolombia y está dividido en módulos:

- `domain/model`: entidades, value objects simples, excepciones y puertos
- `domain/usecase`: lógica de negocio
- `infrastructure/entry-points/api-rest`: controlador REST, DTOs, mapeo, documentación OpenAPI y manejo de errores
- `infrastructure/driven-adapters/jpa-repository`: persistencia con Spring Data JPA y PostgreSQL
- `infrastructure/driven-adapters/ai-summary-mock`: implementación mock del puerto de IA
- `applications/app-service`: arranque de la aplicación y configuración de beans

Puertos principales del dominio:

- `LeadRepository`: persistencia y consultas de leads
- `AiSummaryGateway`: generación del resumen ejecutivo

Esto permite reemplazar implementaciones de infraestructura sin afectar la lógica del dominio.

## Tecnologías utilizadas

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- PostgreSQL 15
- Gradle 9.4.1
- SpringDoc OpenAPI
- Lombok
- Docker y Docker Compose
- GitHub Actions
- JaCoCo

## Estructura del proyecto

```text
applications/
  app-service/
domain/
  model/
  usecase/
infrastructure/
  entry-points/
    api-rest/
  driven-adapters/
    jpa-repository/
    ai-summary-mock/
deployment/
  Dockerfile
```

## Requisitos previos

- Java 21
- Docker y Docker Compose
- Gradle Wrapper incluido en el repositorio

## Ejecución local

### 1. Levantar PostgreSQL

```bash
docker compose up -d
```

Esto levanta un contenedor PostgreSQL 15 con:

- base de datos: `omc_leads_db`
- usuario: `postgres`
- contraseña: `postgres`
- puerto: `5432`

### 2. Configurar variables de entorno

La aplicación espera estas variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `PORT` opcional, por defecto `8080`

Ejemplo para entorno local usando la base de datos de `docker compose`:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/omc_leads_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

### 3. Ejecutar la aplicación

```bash
./gradlew bootRun
```

La API queda disponible en:

- `http://localhost:8080`

## Configuración

La configuración principal vive en [application.yaml](/home/certhakzu/Documentos/Jobs/One%20Million%20Copy%20SAS/test-api-onemillion/applications/app-service/src/main/resources/application.yaml).

Aspectos relevantes:

- el puerto HTTP usa `PORT` con valor por defecto `8080`
- la conexión a PostgreSQL depende de variables de entorno
- `spring.sql.init.mode=always` ejecuta el script de inicialización al arrancar
- el dialecto configurado es PostgreSQL
- CORS permite por defecto `http://localhost:4200` y `http://localhost:8080`

## Base de datos

El proyecto usa un script SQL de inicialización en [schema.sql](/home/certhakzu/Documentos/Jobs/One%20Million%20Copy%20SAS/test-api-onemillion/applications/app-service/src/main/resources/schema.sql).

Actualmente:

- crea la tabla `leads` si no existe
- crea índices sobre `created_at` y `fuente`
- limpia la tabla en cada arranque cuando la inicialización está activa
- inserta 12 registros semilla, incluyendo 1 lead marcado como eliminado lógicamente

No hay Flyway ni Liquibase configurados en el repositorio.

## Documentación API

Documentación local disponible con SpringDoc:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

El título configurado en OpenAPI es `OMC Leads API`.

## Endpoints principales

Todos los endpoints cuelgan de `/leads`.

- `POST /leads`: crea un lead
- `POST /leads/webhook`: recibe leads externos reutilizando la lógica de creación
- `GET /leads/{id}`: consulta un lead activo por id
- `GET /leads`: lista leads con `page`, `limit`, `source`, `startDate`, `endDate`
- `PATCH /leads/{id}`: actualización parcial de un lead
- `DELETE /leads/{id}`: eliminación lógica
- `GET /leads/stats`: estadísticas agregadas
- `POST /leads/ai/summary`: resumen ejecutivo usando el gateway de IA

## Integración con IA

La integración de IA fue diseñada mediante el puerto `AiSummaryGateway`.

Estado actual:

- la lógica de negocio consume el puerto, no una implementación concreta
- la implementación activa es [AiSummaryMockAdapter.java](/home/certhakzu/Documentos/Jobs/One%20Million%20Copy%20SAS/test-api-onemillion/infrastructure/driven-adapters/ai-summary-mock/src/main/java/co/com/onemillion/aisummarymock/AiSummaryMockAdapter.java)
- no hay dependencia a un proveedor real externo en este repositorio

Esto deja preparado el sistema para reemplazar el mock por un proveedor real sin modificar los casos de uso.

## Manejo de errores y validaciones

Las validaciones se aplican tanto en DTOs como en casos de uso.

Ejemplos de reglas implementadas:

- nombre obligatorio y con longitud mínima al crear
- email obligatorio y con formato válido
- email único
- `limit` entre `1` y `100`
- `page` mayor o igual a `0`
- rango de fechas válido
- en `PATCH` debe enviarse al menos un campo

El manejo global de errores está en [GlobalExceptionHandler.java](/home/certhakzu/Documentos/Jobs/One%20Million%20Copy%20SAS/test-api-onemillion/infrastructure/entry-points/api-rest/src/main/java/co/com/onemillion/api/error/GlobalExceptionHandler.java).

Formato general de error:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Detalle del error"
}
```

Estados HTTP usados por la API:

- `400 Bad Request`
- `404 Not Found`
- `409 Conflict`
- `500 Internal Server Error`

## Tests

El repositorio contiene pruebas unitarias y de configuración sobre los módulos principales:

- casos de uso
- controlador REST y mappers
- manejo de errores
- adaptadores JPA
- adaptador mock de IA
- configuración de aplicación
- reglas de arquitectura del scaffold

Comandos útiles:

```bash
./gradlew test
```

```bash
./gradlew check
```

```bash
./gradlew jacocoMergedReport
```

La cobertura está configurada con JaCoCo y el build verifica un mínimo por módulo en `80%`.

## Docker

### Docker Compose

El archivo [docker-compose.yml](/home/certhakzu/Documentos/Jobs/One%20Million%20Copy%20SAS/test-api-onemillion/docker-compose.yml) está orientado al entorno local y levanta PostgreSQL.

### Imagen de aplicación

El proyecto incluye un Dockerfile en [deployment/Dockerfile](/home/certhakzu/Documentos/Jobs/One%20Million%20Copy%20SAS/test-api-onemillion/deployment/Dockerfile) con build multi-stage:

- etapa 1: compila el `bootJar`
- etapa 2: ejecuta la aplicación sobre JRE 21

## CI/CD

El workflow de GitHub Actions está en [.github/workflows/ci.yml](/home/certhakzu/Documentos/Jobs/One%20Million%20Copy%20SAS/test-api-onemillion/.github/workflows/ci.yml).

Actualmente ejecuta:

- checkout del repositorio
- configuración de Java 21
- configuración de Gradle
- `./gradlew test`
- `./gradlew clean build -x test`

No hay un flujo de despliegue automatizado a Render versionado en el repositorio.

## Despliegue

El repositorio contiene un Dockerfile compatible con despliegue en contenedores y el contexto de la prueba contempla Render como plataforma de despliegue.

Como no hay `render.yaml` ni configuración de Render versionada en este repositorio, el detalle exacto del servicio desplegado depende de la configuración realizada fuera del repo.

## Decisiones técnicas relevantes

- **Clean Architecture**: separa dominio, casos de uso y adaptadores para reducir acoplamiento
- **Soft delete**: los leads no se eliminan físicamente; se marcan con `deleted=true`
- **PostgreSQL**: persistencia relacional con índices sobre campos de consulta relevantes
- **Gateway de IA**: la lógica de resumen usa un puerto desacoplado
- **Mock de IA**: permite demostrar el diseño sin depender de servicios externos
- **Inicialización por SQL**: facilita levantar el proyecto con datos de prueba inmediatos

## Mejoras futuras

- reemplazar el mock de IA por un proveedor real
- versionar migraciones con Flyway o Liquibase
- separar datos semilla del esquema para distintos entornos
- añadir pipeline de despliegue versionado
- endurecer configuración por ambiente para desarrollo, pruebas y producción

## Comandos útiles

```bash
./gradlew validateStructure
```

```bash
./gradlew bootRun
```

```bash
docker compose down -v
```

---

Desarrollado como prueba técnica backend para gestión de leads sobre Clean Architecture.
