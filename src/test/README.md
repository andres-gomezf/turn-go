# Tests - TurnGo

Este directorio contiene todos los tests para la aplicación TurnGo.

## Requisitos

### 1. Docker Desktop
Los tests requieren que Docker Desktop esté ejecutándose porque usan Testcontainers.

### 2. Dependencias Maven
Las siguientes dependencias ya están añadidas al `pom.xml`:
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

## Archivos creados

### 1. `BaseIntegrationTest.java`
Clase abstracta que configura Testcontainers una sola vez para todos los tests:
- Configura PostgreSQL container
- Establece propiedades dinámicas de conexión
- Usa perfil `test` para configuración específica

### 2. `application-test.properties`
Configuración específica para tests:
```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.security.enabled=false

# Configuración simplificada para tests
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

### 3. `cleanup.sql`
Script SQL que se ejecuta después de cada test para limpiar la base de datos:
```sql
-- Eliminar datos en orden inverso para respetar foreign keys
DELETE FROM turnos;
DELETE FROM horarios;
DELETE FROM canchas;
DELETE FROM clientes;

-- Resetear secuencias de auto-increment
ALTER SEQUENCE IF EXISTS turnos_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS horarios_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS canchas_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS clientes_id_seq RESTART WITH 1;
```

### 4. Tests de Controladores

#### `CanchaControllerIntegrationTest.java` (7 tests)
- `shouldGetAllCanchasWhenEmpty()` - GET con BD vacía
- `shouldGetAllCanchasWithData()` - GET con datos
- `shouldGetCanchaById()` - GET por ID
- `shouldReturnNotFoundWhenCanchaDoesNotExist()` - GET con ID inexistente
- `shouldCreateNewCancha()` - POST y verificar persistencia
- `shouldDeleteCancha()` - DELETE y verificar eliminación
- `shouldNotAllowDuplicateNumeroCancha()` - Validar constraint unique (HTTP 500)

#### `HorarioControllerIntegrationTest.java` (8 tests)
- `shouldGetAllHorariosWhenEmpty()` - GET con BD vacía
- `shouldGetAllHorariosWithData()` - GET con datos
- `shouldGetHorarioById()` - GET por ID
- `shouldReturnNotFoundWhenHorarioDoesNotExist()` - GET con ID inexistente
- `shouldCreateNewHorarioWithValidCancha()` - POST con HorarioPostDto
- `shouldReturnBadRequestWhenCanchaDoesNotExist()` - POST con canchaId inválido (HTTP 500)
- `shouldReturnBadRequestForInvalidHorarioData()` - Validaciones de datos (HTTP 400)
- `shouldDeleteHorario()` - DELETE y verificar eliminación

#### `TurnoControllerIntegrationTest.java` (10 tests)
- `shouldGetAllTurnosWhenEmpty()` - GET con BD vacía
- `shouldGetAllTurnosWithData()` - GET con datos
- `shouldGetTurnoById()` - GET por ID
- `shouldReturnNotFoundWhenTurnoDoesNotExist()` - GET con ID inexistente
- `shouldCreateNewTurnoWithValidData()` - POST con TurnoDto
- `shouldReturnBadRequestForPastDate()` - Validación @FutureOrPresent (HTTP 400)
- `shouldReturnBadRequestWhenClienteDoesNotExist()` - POST con clienteId inválido (HTTP 500)
- `shouldReturnBadRequestWhenHorarioDoesNotExist()` - POST con horarioId inválido (HTTP 500)
- `shouldDeleteTurno()` - DELETE y verificar eliminación
- `shouldGetAvailableTurnosByDate()` - GET /disponibles?fecha=2025-10-25
- `shouldNotAllowDuplicateTurnoForSameHorarioAndDate()` - Validar lógica de negocio (HTTP 409 Conflict)

## Características de los tests

### 1. Aislamiento
- Cada test usa `@Sql` para limpiar la base de datos después de cada test
- Se ejecuta el script `cleanup.sql` que elimina todos los datos y resetea las secuencias
- Los tests son independientes entre sí

### 2. Datos de prueba
- Usan métodos helper para crear entidades (`createCliente`, `createCancha`, etc.)
- Crean datos necesarios usando repositorios reales
- Verifican tanto respuestas HTTP como persistencia en BD

### 3. Validaciones completas
- Prueban casos exitosos (200, 201, 204)
- Prueban casos de error (400, 404, 500)
- Validan constraints de base de datos
- Verifican lógica de negocio

## Troubleshooting

### Error: "Docker not running"
```
Asegúrate de que Docker Desktop esté ejecutándose antes de ejecutar los tests.
```

### Error: "Port already in use"
```
Los tests usan puertos aleatorios, pero si hay conflictos, reinicia Docker Desktop.
```

### Tests muy lentos
```
Los tests de integración son más lentos que los unit tests porque:
- Levantan el contexto completo de Spring
- Crean contenedores Docker
- Usan base de datos real
```

## Tests Unitarios (Unit Tests)

### `CanchaControllerTest.java` (7 tests)
Tests unitarios usando `@WebMvcTest` con servicios mockeados:
- `shouldGetAllCanchas()` - GET /api/v1/canchas con mocks
- `shouldGetCanchaById()` - GET /api/v1/canchas/1 con mocks
- `shouldReturnNotFoundWhenCanchaDoesNotExist()` - GET con ID inexistente
- `shouldCreateNewCancha()` - POST /api/v1/canchas con mocks
- `shouldDeleteCancha()` - DELETE /api/v1/canchas/1 con mocks
- `shouldReturnNotFoundWhenDeletingNonExistentCancha()` - DELETE con ID inexistente
- `shouldReturnInternalServerErrorWhenExceptionOccurs()` - Simular RuntimeException

### `HorarioControllerTest.java` (8 tests)
Tests unitarios usando `@WebMvcTest` con servicios mockeados:
- `shouldGetAllHorarios()` - GET /api/v1/horarios con mocks
- `shouldGetHorarioById()` - GET /api/v1/horarios/1 con mocks
- `shouldReturnNotFoundWhenHorarioDoesNotExist()` - GET con ID inexistente
- `shouldCreateNewHorario()` - POST con HorarioPostDto válido
- `shouldReturnBadRequestWhenCanchaDoesNotExist()` - POST con canchaId inválido
- `shouldReturnBadRequestForInvalidHorarioData()` - POST con datos inválidos
- `shouldDeleteHorario()` - DELETE /api/v1/horarios/1 con mocks
- `shouldReturnInternalServerErrorWhenExceptionOccurs()` - Simular RuntimeException

### `TurnoControllerTest.java` (10 tests)
Tests unitarios usando `@WebMvcTest` con servicios mockeados:
- `shouldGetAllTurnos()` - GET /api/v1/turnos con mocks
- `shouldGetTurnoById()` - GET /api/v1/turnos/1 con mocks
- `shouldReturnNotFoundWhenTurnoDoesNotExist()` - GET con ID inexistente
- `shouldCreateNewTurno()` - POST con TurnoDto válido
- `shouldReturnBadRequestForPastDate()` - POST con fecha pasada
- `shouldReturnBadRequestWhenClienteDoesNotExist()` - POST con clienteId inválido
- `shouldReturnBadRequestWhenHorarioDoesNotExist()` - POST con horarioId inválido
- `shouldDeleteTurno()` - DELETE /api/v1/turnos/1 con mocks
- `shouldGetAvailableTurnosByDate()` - GET /api/v1/turnos/disponibles con mocks
- `shouldReturnInternalServerErrorWhenExceptionOccurs()` - Simular RuntimeException

## Tests de Repositorio (Repository Tests)

### `BaseRepositoryTest.java` (clase base)
Configuración de `@DataJpaTest` con Testcontainers PostgreSQL:
- `@DataJpaTest` - Solo carga la capa de persistencia (JPA)
- `@AutoConfigureTestDatabase(replace = NONE)` - Usa PostgreSQL real en lugar de H2
- Testcontainers con PostgreSQL para queries reales

### `TurnoRepositoryTest.java` (5 tests)
Tests de repositorio para la query personalizada `findAvailableByDate()`:
- `shouldSaveAndFindTurno()` - Guardar turno y recuperarlo por ID
- `shouldFindAvailableByDate_WhenNoTurnosExist()` - Query personalizada con BD vacía
- `shouldFindAvailableByDate_WhenSomeTurnosExist()` - Query con turnos ocupados
- `shouldNotFindAvailableByDate_WhenAllTurnosOccupied()` - Todos los horarios ocupados
- `shouldDeleteTurno()` - Eliminar turno y verificar

## Resumen de Tests

**Total:** 54 tests implementados
- **25 Integration Tests** (Full Integration Tests con Testcontainers)
- **25 Unit Tests** (Controller Tests con mocks)
- **5 Repository Tests** (DataJpaTest con Testcontainers)

### Diferencias entre tipos de tests:

#### Integration Tests
- **Contexto:** `@SpringBootTest` - contexto completo de Spring
- **Base de datos:** PostgreSQL real con Testcontainers
- **HTTP:** `RestTemplate` - peticiones HTTP reales
- **Objetivo:** Probar flujo completo end-to-end
- **Velocidad:** Lentos (requieren Docker + PostgreSQL)

#### Unit Tests
- **Contexto:** `@WebMvcTest` - solo capa web
- **Base de datos:** Sin BD (servicios mockeados)
- **HTTP:** `MockMvc` - simulación de peticiones HTTP
- **Objetivo:** Probar lógica del controlador aisladamente
- **Velocidad:** Muy rápidos (sin BD, sin Spring completo)

#### Repository Tests
- **Contexto:** `@DataJpaTest` - solo capa de persistencia
- **Base de datos:** PostgreSQL real con Testcontainers
- **HTTP:** Sin HTTP (solo repositorios)
- **Objetivo:** Probar queries personalizadas complejas
- **Velocidad:** Lentos (requieren Docker + PostgreSQL)
