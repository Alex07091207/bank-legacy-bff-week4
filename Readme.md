# Banco XYZ - Migración de Procesos Batch

## Desarrollo Backend III - Semana 3

**Actividad:** Optimizando procesos batch para mejorar la resiliencia de procesos

**Estudiante:** Kevin Lovera  
**Tecnología principal:** Spring Boot 3.5.3 / Spring Batch  
**Lenguaje:** Java  
**Base de datos:** MySQL  
**Build:** Maven  

---

## 1. Descripción del proyecto

El proyecto consiste en la migración y modernización de procesos batch pertenecientes a un sistema legacy del Banco XYZ.

La solución utiliza Spring Batch para procesar información proveniente de archivos CSV, aplicar validaciones y transformaciones mediante `ItemProcessor` y almacenar los resultados procesados en una base de datos relacional MySQL.

Se implementaron tres procesos principales:

1. Reporte de transacciones diarias.
2. Cálculo de intereses mensuales.
3. Generación de estados de cuenta anuales.

---

## 2. Objetivo

El objetivo es modernizar los procesos batch del sistema legacy utilizando Spring Batch, incorporando mecanismos de procesamiento por lotes, tolerancia a fallos, validación de datos y estrategias de escalamiento.

La solución busca mejorar la organización, resiliencia y capacidad de procesamiento de los procesos bancarios.

---

## 3. Procesos implementados

### 3.1 Reporte de transacciones diarias

Job:

```text
transaccionesJob

Este proceso lee el archivo:

transacciones.csv

Los datos son procesados mediante un ItemProcessor, donde se realizan validaciones y normalizaciones de los registros.

Posteriormente, los datos procesados son almacenados en:

transacciones_procesadas

Además, el proceso utiliza particionamiento para distribuir el procesamiento en diferentes particiones.

3.2 Cálculo de intereses mensuales

Job:

interesesJob

Archivo de entrada:

intereses.csv

El InteresProcessor valida los datos de las cuentas y calcula el interés correspondiente según el tipo de cuenta.

Las tasas utilizadas son:

Ahorro: 2%
Préstamo: 5%
Hipoteca: 4%

Los resultados son almacenados en:

intereses_procesados
3.3 Generación de estados de cuenta anuales

Job:

cuentasAnualesJob

Archivo de entrada:

cuentas_anuales.csv

El proceso valida y transforma los registros anuales antes de almacenarlos en:

cuentas_anuales_procesadas
4. Arquitectura

El proyecto utiliza la arquitectura tradicional de procesamiento de Spring Batch:

CSV
 │
 ▼
ItemReader
 │
 ▼
ItemProcessor
 │
 ▼
ItemWriter
 │
 ▼
MySQL

En el proceso de transacciones se utiliza además una arquitectura de particionamiento:

                 transaccionesJob
                       │
                       ▼
             transaccionesMasterStep
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
     Partition-0  Partition-1  Partition-2
          │            │            │
          ▼            ▼            ▼
       Worker         Worker       Worker
       Step           Step         Step
          │            │            │
          └────────────┼────────────┘
                       ▼
                      MySQL
5. Componentes principales
ItemReader

Los lectores se encargan de obtener los registros desde los archivos CSV.

Se utilizan:

FlatFileItemReader

y un lector especializado para el procesamiento particionado de transacciones.

ItemProcessor

Los processors realizan validaciones, normalización y transformación de los datos.

Clases principales:

TransaccionProcessor
InteresProcessor
CuentaAnualProcessor
ItemWriter

Los datos procesados son almacenados utilizando:

JdbcBatchItemWriter

Los resultados son enviados a las tablas correspondientes de MySQL.

6. Manejo de errores

Los procesos utilizan mecanismos de tolerancia a fallos de Spring Batch.

Se utilizan configuraciones como:

faultTolerant()
skip()
skipLimit()
retry()
retryLimit()

Estas configuraciones permiten manejar errores durante el procesamiento sin detener necesariamente todo el Job ante un registro problemático.

7. Escalamiento

Para el procesamiento de transacciones se implementó particionamiento.

La configuración utiliza:

gridSize = 3

Por lo tanto, los registros pueden ser distribuidos en tres particiones:

partition-0
partition-1
partition-2

El procesamiento utiliza un TaskExecutor para ejecutar las particiones de manera concurrente.

8. Base de datos

La solución utiliza MySQL.

Tablas principales:

transacciones_procesadas
intereses_procesados
cuentas_anuales_procesadas

La estructura de las tablas permite almacenar los resultados generados por cada proceso batch.

9. Ejecución del proyecto
Requisitos
Java 17 o superior
Maven
MySQL
Spring Boot
Git
Compilar y ejecutar pruebas

Desde la carpeta del proyecto:

./mvnw clean test

En Windows:

.\mvnw.cmd clean test
10. Ejecutar los Jobs
Transacciones
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.batch.job.name=transaccionesJob"
Intereses
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.batch.job.name=interesesJob"
Cuentas anuales
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.batch.job.name=cuentasAnualesJob"
11. Evidencias

Las evidencias de ejecución se encuentran en la carpeta:

evidencias/

Se incluyen capturas de:

Ejecución de las pruebas Maven.
Ejecución del Job de transacciones.
Ejecución del Job de intereses.
Ejecución del Job de cuentas anuales.
Resultados generados en MySQL.
12. Resultado de ejecución

Los Jobs fueron ejecutados mediante Spring Batch y finalizaron correctamente con estado:

COMPLETED

Las ejecuciones pueden ser verificadas mediante los logs de Spring Batch incluidos en las evidencias.

13. Repositorio

El código fuente del proyecto se encuentra versionado en GitHub.


14. Conclusión

La implementación permitió modernizar los procesos batch del sistema legacy del Banco XYZ mediante Spring Batch.

La solución incorpora lectura de archivos CSV, procesamiento y validación mediante ItemProcessor, escritura mediante JdbcBatchItemWriter, tolerancia a fallos y una estrategia de escalamiento mediante particionamiento.

Con esto se obtiene una arquitectura más organizada y preparada para procesar información bancaria mediante procesos batch.