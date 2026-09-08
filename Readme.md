# Bank Legacy BFF - Semana 4

## Desarrollo Backend III - PBY2203

Proyecto correspondiente a la Semana 4 del curso Desarrollo Backend III, cuyo objetivo es implementar el patrón arquitectónico **Backend for Frontend (BFF)** para el sistema del Banco XYZ.

---

## 1. Objetivo del proyecto

El objetivo es implementar una solución basada en el patrón **Backend for Frontend (BFF)** que permita adaptar las respuestas del backend según las necesidades de cada tipo de cliente:

- BFF Web
- BFF Móvil
- BFF Cajeros Automáticos

La solución utiliza los datos del sistema legacy del Banco XYZ y proporciona APIs específicas para cada canal.

De esta manera, cada cliente recibe solamente la información que necesita, mejorando la comunicación entre frontend y backend.

---

## 2. Estrategia de implementación

Para este proyecto se decidió implementar los tres BFF dentro de una misma aplicación Spring Boot, organizados mediante controladores y servicios independientes para cada canal.

La estrategia permite mantener una estructura sencilla y fácil de mantener, diferenciando claramente las responsabilidades de cada cliente.

### Arquitectura

```text
                    Banco XYZ
                       │
                       ▼
                  MySQL - bank_bff
                       │
                       ▼
              ┌──────────────────┐
              │  Aplicación BFF   │
              └──────────────────┘
                 │       │       │
                 ▼       ▼       ▼
              ┌────┐  ┌──────┐  ┌─────┐
              │Web │  │Mobile│  │ ATM │
              └────┘  └──────┘  └─────┘

Cada canal posee su propio controlador, servicio y formato de respuesta.

3. Tecnologías utilizadas
Java 17
Spring Boot 3.5.3
Spring Web
Spring JDBC
Spring Batch
Maven
MySQL
Postman
Git
GitHub
4. Estructura del proyecto
src/
└── main/
    ├── java/
    │   └── com/
    │       └── banco/
    │           └── bank_legacy_batch/
    │               ├── config/
    │               ├── Exception/
    │               ├── model/
    │               ├── processor/
    │               ├── reader/
    │               └── bff/
    │                   ├── controller/
    │                   │   ├── WebBffController.java
    │                   │   ├── MobileBffController.java
    │                   │   └── AtmBffController.java
    │                   │
    │                   ├── service/
    │                   │   ├── WebBffService.java
    │                   │   ├── MobileBffService.java
    │                   │   ├── AtmBffService.java
    │                   │   └── BffSecurityService.java
    │                   │
    │                   ├── repository/
    │                   │   └── CuentaBffRepository.java
    │                   │
    │                   └── dto/
    │                       ├── WebCuentaResponse.java
    │                       ├── MobileCuentaResponse.java
    │                       └── AtmSaldoResponse.java
    │
    └── resources/
        └── application.properties
5. Base de datos

Para esta implementación se creó una nueva base de datos para mantener aislados los datos utilizados en la Semana 3.

Base de datos:

bank_bff

Tabla principal:

cuentas_bff

La tabla contiene información como:

ID de cuenta
Nombre
Saldo
Edad
Tipo de cuenta
Interés
Saldo final
Estado
6. BFF Web

El BFF Web está optimizado para navegadores y aplicaciones que requieren información más completa.

Endpoint
GET /bff/web/cuenta/{cuentaId}

Ejemplo:

GET http://localhost:8080/bff/web/cuenta/101
Header requerido
X-Canal: WEB
Respuesta
{
    "nombre": "John Doe",
    "saldo": 5000.00,
    "tipo": "ahorro",
    "saldoFinal": 5100.00,
    "estado": "PROCESADO"
}

El BFF Web entrega una respuesta con mayor cantidad de información para permitir interfaces más completas.

7. BFF Móvil

El BFF Móvil está diseñado para entregar respuestas más ligeras, reduciendo la cantidad de información transferida y mejorando la velocidad de respuesta.

Endpoint
GET /bff/mobile/cuenta/{cuentaId}

Ejemplo:

GET http://localhost:8080/bff/mobile/cuenta/101
Header requerido
X-Canal: MOBILE
Respuesta
{
    "nombre": "John Doe",
    "saldo": 5000.00
}

A diferencia del BFF Web, el BFF Móvil solamente entrega los datos esenciales para la consulta.

8. BFF Cajeros Automáticos

El BFF para cajeros automáticos está orientado a operaciones críticas y entrega respuestas mínimas y eficientes.

Actualmente se implementó la consulta de saldo.

Endpoint
GET /bff/atm/cuenta/{cuentaId}/saldo

Ejemplo:

GET http://localhost:8080/bff/atm/cuenta/101/saldo
Headers requeridos
X-Canal: ATM
X-Operacion: CONSULTAR_SALDO
Respuesta
{
    "saldo": 5000.00
}
9. Autenticación y autorización por canal

La solución incorpora una validación específica para cada canal mediante headers HTTP.

Web
X-Canal: WEB
Mobile
X-Canal: MOBILE
ATM
X-Canal: ATM
X-Operacion: CONSULTAR_SALDO

El componente BffSecurityService verifica que el canal recibido corresponda al endpoint utilizado y, en el caso del ATM, también valida la operación solicitada.

Esta implementación corresponde a una validación académica de canal y operación. No reemplaza un sistema de autenticación completo basado en JWT, OAuth2 u otro mecanismo de identidad.

10. Ejecución del proyecto
Requisitos

Se requiere tener instalado:

Java
Maven o utilizar el Maven Wrapper incluido
MySQL
Configuración de la base de datos

La aplicación utiliza la base de datos:

bank_bff

y se conecta mediante:

jdbc:mysql://localhost:3306/bank_bff

Se debe verificar que MySQL se encuentre ejecutándose y que las credenciales configuradas en application.properties sean correctas.

Ejecutar el proyecto

Desde la raíz del proyecto:

.\mvnw.cmd spring-boot:run

La aplicación se ejecuta en:

http://localhost:8080
11. Pruebas con Postman
Web
GET http://localhost:8080/bff/web/cuenta/101

Header:

X-Canal: WEB

Resultado esperado:

200 OK
Mobile
GET http://localhost:8080/bff/mobile/cuenta/101

Header:

X-Canal: MOBILE

Resultado esperado:

200 OK
ATM
GET http://localhost:8080/bff/atm/cuenta/101/saldo

Headers:

X-Canal: ATM
X-Operacion: CONSULTAR_SALDO

Resultado esperado:

200 OK
12. Evidencia de ejecución

Para comprobar el funcionamiento del sistema se realizaron pruebas utilizando Postman.

BFF Web

La API respondió correctamente con código:

200 OK

y entregó la información completa de la cuenta.

BFF Mobile

La API respondió correctamente con:

200 OK

y entregó únicamente los datos esenciales de la cuenta.

BFF ATM

La API respondió correctamente con:

200 OK

y entregó el saldo de la cuenta.

También se verificó mediante Maven que el proyecto compila y ejecuta correctamente sus pruebas:

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
13. Repositorio

Código fuente del proyecto:

https://github.com/Kevinlovera/bank-legacy-bff-week4

14. Conclusión

La implementación permite aplicar el patrón Backend for Frontend (BFF) diferenciando las necesidades de los clientes Web, Móvil y Cajeros Automáticos.

Cada canal posee endpoints y respuestas adaptadas a su propósito, evitando entregar información innecesaria y permitiendo una comunicación más adecuada entre los distintos clientes y el backend.

La solución fue desarrollada utilizando Spring Boot, Spring JDBC y MySQL, incorporando además validaciones específicas para los canales y operaciones solicitadas.