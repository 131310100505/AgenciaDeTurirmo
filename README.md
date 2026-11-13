# Agencia de Turismo — Rumbo Sur

## Trabajo Práctico Obligatorio — Programación Orientada a Objetos

### 1. Descripción del proyecto

Rumbo Sur es un sistema de gestión para una agencia de turismo receptivo, desarrollado como Trabajo Práctico Obligatorio de la materia Programación Orientada a Objetos.

El objetivo del sistema es administrar tours, salidas programadas, clientes, pasajeros, reservas, pagos, servicios adicionales y cancelaciones.

También permite organizar los recursos de cada salida, como guías y vehículos, realizar liquidaciones y generar reportes administrativos.

El proyecto está desarrollado en Java, aplicando conceptos de Programación Orientada a Objetos, modelado UML, principios SOLID y patrones GRASP.

### 2. Integrantes del equipo

- Germán Rocha
- Francisco Miel
- Valentín Umbides

### 3. Propuesta seleccionada

**Propuesta B — Agencia de Turismo**

La agencia Rumbo Sur ofrece tours de aventura, culturales y de naturaleza, con itinerarios, tarifas por temporada y salidas programadas.

El sistema gestiona las operaciones comerciales, operativas y administrativas de la agencia, respetando las reglas de negocio establecidas en el enunciado.

### 4. Funcionalidades principales

- Administración del catálogo de tours, actividades y tarifas.
- Programación y control de salidas.
- Registro de clientes y pasajeros.
- Creación, modificación y cancelación de reservas.
- Validación de cupos y condiciones de los pasajeros.
- Cálculo de precios, descuentos y promociones.
- Contratación de servicios adicionales y seguros.
- Registro de señas, pagos y ajustes.
- Asignación de guías y vehículos.
- Generación y exportación de listas de pasajeros.
- Inicio y finalización de salidas.
- Control de cupo mínimo.
- Liquidación de honorarios de guías.
- Generación de reportes administrativos.
- Persistencia de información mediante archivos.

### 5. Tecnologías y herramientas

- **Lenguaje:** Java 17 o superior.
- **Entorno de desarrollo:** IntelliJ IDEA o Visual Studio Code.
- **Modelado:** UML.
- **Diagramas:** Draw.io.
- **Control de versiones:** Git y GitHub.
- **Principios de diseño:** SOLID y GRASP.
- **Pruebas unitarias:** JUnit 5, configurado con Maven en `pom.xml`.
- **Interfaz de usuario:** consola y ventana gráfica con Java Swing.
- **Persistencia:** serialización de objetos en archivos.

### 6. Cómo abrir y ejecutar el proyecto

#### Requisitos

- Tener instalado Java JDK 17 o superior.
- Contar con IntelliJ IDEA o Visual Studio Code.
- Tener acceso al repositorio del proyecto.

#### Ejecutar el programa en IntelliJ IDEA

1. Abrir IntelliJ IDEA.
2. Seleccionar **File > Open**.
3. Elegir la carpeta del proyecto que contiene `pom.xml`.
4. Esperar a que IntelliJ cargue las dependencias.
5. Abrir la carpeta `src/ar/edu/uade/rumbosur/`.
6. Buscar el archivo `Principal.java`.
7. Ejecutar el método `main` utilizando el triángulo verde.

Al iniciar el programa se muestra el menú principal:

```text
========== RUMBO SUR - Agencia de Turismo ==========

1. Vendedor
2. Coordinador de operaciones
3. Administrador
4. Crear reserva con ventana (interfaz gráfica)
0. Guardar y salir
```

Cada opción permite acceder a las funcionalidades correspondientes al rol seleccionado.

- **Vendedor (7 opciones):** crear reserva (registra al cliente y a los pasajeros si hace falta), consultar reserva, contratar adicionales, registrar pago (la reserva se confirma sola al alcanzar la seña), reemplazar pasajero, cancelar reserva y registrar ajustes (CU04 a CU09, CU11, CU12 y CU19).
- **Coordinador de operaciones (6 opciones):** listar salidas con su ocupación, programar salida, asignar guía y vehículo, emitir la lista de pasajeros (genera el archivo), iniciar salida, y finalizar salida y liquidar al guía (CU03 y CU14 a CU17).
- **Administrador (5 opciones):** los cuatro reportes (CU18) y el catálogo de tours: ver, registrar y modificar tours, agregar actividades y tarifas por temporada (CU01 y CU02).

Al iniciar, el sistema anula las reservas con el saldo vencido (CU13) y cancela las salidas que, faltando 7 días o menos, no alcanzaron el cupo mínimo, con devolución del 100 % de lo pagado.

Para finalizar correctamente, seleccionar la opción `0`, que permite guardar los datos antes de cerrar la aplicación.

#### Persistencia de datos

El sistema utiliza un archivo denominado `rumbosur.dat` para conservar la información.

Al iniciar, intenta recuperar los datos previamente guardados. Si no existen, se utilizan los datos iniciales preparados para facilitar las pruebas.

Al cerrar correctamente la aplicación, se guardan los cambios realizados.

### 7. Datos iniciales de prueba

El proyecto incluye la clase `DatosIniciales.java`, que prepara información de ejemplo para probar las funcionalidades.

#### Tours y salidas programadas

| Código de salida | Tour | Cupo mínimo | Cupo máximo |
|---|---|---|---|
| 101 | Lagos de Bariloche | 2 | 20 |
| 102 | Trekking en la Patagonia | 2 | 15 |
| 103 | Historia de Bariloche | 2 | 25 |

Las fechas se establecen al generar inicialmente los datos y se conservan mediante la persistencia.

Cada tour tiene una actividad en su itinerario y una tarifa vigente por un año. La actividad del trekking la presta el proveedor externo *Patagonia Aventura*.

Si el archivo `rumbosur.dat` está dañado, el programa guarda una copia (`rumbosur.dat.danado`) antes de seguir, así esa información no se pierde.

#### Vendedores

| Legajo | Nombre |
|---|---|
| 401 | Sofía Martínez |
| 402 | Diego Ramírez |

#### Guías y vehículos

| Salida | Guía asignado | Vehículo |
|---|---|---|
| 101 | Martín López | AB123CD |
| 102 | Lucía Fernández | AC456EF |
| 103 | Carlos Gómez | AD789GH |

#### Clientes

Se incluyen tres clientes de ejemplo:

- Ana Pérez.
- Pedro García.
- María Rodríguez.

#### Reserva de prueba

Se preparó una reserva de prueba identificada con el número `1`, correspondiente a la salida `101`, con dos pasajeros:

- Juan Pérez.
- Lucía Gómez.

Esta reserva se utilizó para comprobar las funcionalidades de ocupación, generación de listas de pasajeros e inicio y finalización de salidas.

Los datos iniciales están destinados a facilitar las pruebas y demostraciones del sistema.

### 8. Pruebas del proyecto

El proyecto utiliza JUnit 5 para las pruebas unitarias y también contempla pruebas funcionales mediante la ejecución del programa.

#### Ejecutar pruebas en IntelliJ IDEA

1. Abrir el proyecto.
2. Buscar la carpeta `test`.
3. Hacer clic derecho sobre la carpeta.
4. Seleccionar **Run 'All Tests'**.

Para ejecutar una sola clase de prueba, utilizar el triángulo verde que aparece junto a su nombre.

#### Ejecutar pruebas en Visual Studio Code

1. Instalar la extensión **Extension Pack for Java**.
2. Abrir la carpeta del repositorio.
3. Esperar a que se cargue el proyecto Maven.
4. Abrir el panel **Testing**.
5. Seleccionar **Run Tests**.

#### Ejecutar pruebas desde la consola

Si Maven está instalado, ejecutar desde la carpeta del proyecto:

```bash
mvn test
```

#### Pruebas funcionales realizadas

Se verificaron las siguientes operaciones:

- Consulta de salidas programadas.
- Consulta de ocupación de una salida.
- Creación y confirmación de una reserva de prueba con dos pasajeros.
- Generación de la lista de pasajeros.
- Exportación de la lista al archivo `pasajeros_salida_101.txt`.
- Inicio de una salida.
- Finalización de una salida.
- Control de cupo mínimo fuera del plazo de cancelación.
- Recuperación de información previamente guardada.

Las pruebas funcionales mencionadas finalizaron satisfactoriamente.

El caso positivo de cancelación por cupo mínimo se verifica con las pruebas unitarias de `ControladorSalidasTest` y `ControladorSalidasOperacionTest`.

### 9. Organización del trabajo

El proyecto se divide en dos etapas.

#### Etapa 1 — Modelado y diseño del sistema

Incluye:

1. Análisis del enunciado, requisitos y supuestos.
2. Descripción y diagrama de casos de uso.
3. Diagrama de clases inicial.
4. Dos diagramas de secuencia.
5. Aplicación y justificación de tres principios SOLID.
6. Aplicación y justificación de cuatro patrones GRASP.
7. Diagrama de clases final y explicación de las mejoras realizadas.

#### Etapa 2 — Implementación y pruebas

Incluye:

1. Implementación del sistema en Java.
2. Desarrollo de las funcionalidades definidas.
3. Manejo de colecciones, excepciones y persistencia.
4. Interfaz de usuario por consola y Java Swing.
5. Pruebas unitarias y funcionales.
6. Actualización de diagramas UML según la implementación.
7. Documentación final y preparación de la defensa.

### 10. Documentación

La carpeta `docs/` contiene la documentación de análisis, diseño e implementación del sistema.

Los archivos de documentación incluyen:

- `enunciado.md`: identificación y descripción de la propuesta seleccionada.
- `requisitos.md`: requisitos funcionales y reglas de negocio.
- `supuestos.md`: decisiones adoptadas para resolver aspectos no especificados.
- Diagramas UML correspondientes a la Etapa 1.
- Diagramas y documentación de la Etapa 2.
- Planes de pruebas funcionales.

Los diagramas de la Etapa 2 reflejan las modificaciones realizadas durante la implementación en Java.

### 11. Estado del proyecto

**Etapa actual: Etapa 2 — Implementación, integración y documentación final.**

Se implementaron las funcionalidades principales del sistema y se realizaron pruebas unitarias y funcionales.

El módulo de operaciones se encuentra integrado con el menú principal y permite gestionar las salidas programadas.

Se continúa trabajando en la integración final de los módulos, la actualización de los diagramas UML y la documentación de la entrega.

### 12. Objetivo académico

Aplicar los conceptos de Program
