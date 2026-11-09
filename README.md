# Agencia de Turismo — Rumbo Sur

## Trabajo Práctico Obligatorio — Programación Orientada a Objetos

### 1. Descripción del proyecto

Rumbo Sur es un sistema de gestión para una agencia de turismo receptivo, desarrollado como Trabajo Práctico Obligatorio de la materia Programación Orientada a Objetos.

El objetivo del sistema es administrar tours, salidas programadas, clientes, pasajeros, reservas, pagos, servicios adicionales y cancelaciones.

También permitirá organizar los recursos de cada salida, como guías y vehículos, realizar liquidaciones y generar reportes administrativos.

El proyecto se desarrolla utilizando Java y aplicando conceptos de Programación Orientada a Objetos, modelado UML, principios SOLID y patrones GRASP.

### 2. Integrantes del equipo

- Germán Rocha
- Francisco Miel
- Valentín Umbides

### 3. Propuesta seleccionada

**Propuesta B — Agencia de Turismo**

La agencia Rumbo Sur ofrece tours de aventura, culturales y de naturaleza, con itinerarios, tarifas por temporada y salidas programadas.

El sistema permitirá gestionar las operaciones comerciales y administrativas de la agencia, respetando las reglas de negocio establecidas en el enunciado.

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
- Emisión de listas de pasajeros.
- Liquidación de honorarios de guías.
- Generación de reportes administrativos.

### 5. Tecnologías y herramientas

- **Lenguaje:** Java 17 o superior.
- **Modelado:** UML.
- **Diagramas:** Draw.io.
- **Control de versiones:** Git y GitHub.
- **Principios de diseño:** SOLID y GRASP.
- **Pruebas unitarias:** JUnit 5, configurado con Maven en `pom.xml`.

### 6. Cómo abrir el proyecto y correr las pruebas

El código está en `src/` y las pruebas en `test/`, con los mismos paquetes. Maven descarga JUnit solo la primera vez que se abre el proyecto (hace falta internet). Se necesita un JDK 17 o superior.

**IntelliJ IDEA**

1. *File > Open* y elegir la carpeta del repositorio (la que tiene el `pom.xml`). Si pregunta, abrirlo como proyecto Maven.
2. Si el proyecto ya estaba abierto sin Maven: clic derecho sobre `pom.xml` > *Add as Maven Project*.
3. Para correr las pruebas: clic derecho sobre la carpeta `test` > *Run 'All Tests'*. Para una sola clase, el triángulo verde al lado de su nombre.

**VS Code**

1. Instalar la extensión *Extension Pack for Java* (incluye el soporte de Maven y el panel de pruebas).
2. *File > Open Folder* y elegir la carpeta del repositorio. La extensión detecta el `pom.xml` y carga el proyecto (puede tardar un poco la primera vez).
3. Para correr las pruebas: abrir el panel *Testing* (ícono del matraz en la barra lateral) y tocar *Run Tests*. También se puede correr una clase con el botón *Run Test* que aparece arriba de cada clase de prueba.

**Consola** (si Maven está instalado): `mvn test` desde la carpeta del repositorio.

### 7. Organización del trabajo

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
4. Interfaz de usuario.
5. Pruebas unitarias y funcionales.
6. Documentación final y preparación de la defensa.

### 8. Documentación

La carpeta `docs/` contiene la documentación de análisis y diseño del sistema.

Los archivos de documentación incluyen:

- `enunciado.md`: identificación y descripción de la propuesta seleccionada.
- `requisitos.md`: requisitos funcionales y reglas de negocio.
- `supuestos.md`: decisiones adoptadas para resolver aspectos no especificados.
- Diagramas UML y documentación de diseño correspondientes a la Etapa 1.

### 9. Estado del proyecto

**Etapa actual:** Etapa 1 — Modelado y diseño del sistema.

La implementación en Java y las pruebas se desarrollarán durante la Etapa 2.

### 10. Objetivo académico

Aplicar los conceptos de Programación Orientada a Objetos mediante el análisis, modelado, diseño, implementación y prueba de un sistema de gestión, manteniendo coherencia entre los requisitos, los diagramas UML y el código fuente.
