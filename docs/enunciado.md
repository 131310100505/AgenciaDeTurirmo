# Propuesta B — Agencia de Turismo

## Sistema de Gestión — Rumbo Sur

### 1. Propuesta seleccionada

Para el Trabajo Práctico Obligatorio de Programación Orientada a Objetos se seleccionó la **Propuesta B: Agencia de Turismo**.

El proyecto consiste en analizar, diseñar y posteriormente implementar un sistema de gestión para la agencia de turismo receptivo **Rumbo Sur**.

El sistema permitirá administrar las actividades comerciales, operativas y administrativas de la agencia, centralizando la información necesaria para organizar tours y gestionar las reservas de sus clientes.

### 2. Descripción general del sistema

Rumbo Sur ofrece tours de aventura, culturales y de naturaleza.

Cada tour posee información descriptiva, un itinerario compuesto por actividades, tarifas según la temporada y condiciones particulares para su contratación.

A partir de los tours disponibles, la agencia organiza salidas programadas con fechas, cupos mínimos y máximos, guías y vehículos.

Los clientes pueden solicitar reservas para estas salidas, incorporando uno o varios pasajeros, contratando servicios adicionales y realizando los pagos correspondientes.

El sistema también deberá contemplar modificaciones, cancelaciones, devoluciones y el seguimiento del estado de las reservas.

### 3. Usuarios del sistema

El sistema será utilizado internamente por tres tipos principales de usuarios:

**Vendedor:** responsable de registrar clientes y pasajeros, gestionar reservas, incorporar servicios adicionales, registrar pagos y tramitar modificaciones o cancelaciones.

**Coordinador de operaciones:** responsable de programar salidas, asignar guías y vehículos, generar listas de pasajeros y realizar el cierre operativo de las salidas.

**Administrador:** responsable de gestionar el catálogo de tours, las actividades, las tarifas y los reportes administrativos.

Los clientes no interactuarán directamente con la aplicación, sino que realizarán sus solicitudes a través del personal de la agencia.

### 4. Funcionalidades principales

El sistema deberá permitir:

- Administrar tours, itinerarios, actividades y tarifas.
- Programar salidas y controlar sus cupos.
- Registrar clientes y pasajeros.
- Crear, modificar, confirmar y cancelar reservas.
- Validar restricciones y condiciones de contratación.
- Calcular precios según temporadas y categorías de pasajeros.
- Aplicar promociones y descuentos.
- Contratar servicios adicionales y seguros.
- Registrar señas, pagos, saldos y ajustes.
- Calcular devoluciones por cancelación.
- Asignar guías habilitados y vehículos adecuados.
- Generar listas de pasajeros.
- Registrar el cierre de salidas y liquidar honorarios.
- Emitir reportes administrativos.

### 5. Reglas generales del negocio

El sistema deberá respetar las condiciones establecidas para cada tipo de tour y salida.

Entre las principales reglas se encuentran:

- Los tours pueden tener restricciones de edad y condiciones especiales.
- Las salidas poseen cupos mínimos y máximos.
- Las reservas no pueden superar la capacidad disponible.
- Los precios dependen de la temporada y de la categoría del pasajero.
- Las promociones no son acumulables.
- Los tours de aventura requieren seguro obligatorio.
- Las reservas necesitan una seña mínima para su confirmación.
- Las cancelaciones pueden generar devoluciones según la anticipación y su origen.
- Los guías deben estar habilitados y disponibles.
- Los pagos deben conservar su historial mediante ajustes, sin eliminar registros anteriores.

Las condiciones y porcentajes específicos se encuentran detallados en `requisitos.md`.

### 6. Alcance del proyecto

El trabajo se desarrollará en dos etapas.

**Etapa 1 — Análisis y diseño**

Se realizará el relevamiento de requisitos, la definición de supuestos, el modelado UML y la aplicación de principios SOLID y patrones GRASP.

El resultado será un diseño orientado a objetos que sirva como base para la implementación.

**Etapa 2 — Implementación**

Se desarrollará el sistema en Java a partir del diseño definido, incorporando las funcionalidades, validaciones, persistencia y pruebas requeridas.

### 7. Documentación relacionada

El análisis de la propuesta se complementa con los siguientes documentos:

- `requisitos.md`: requisitos funcionales y reglas de negocio.
- `supuestos.md`: decisiones y criterios adoptados para el diseño.
- `modelo-clases.md`: definición preliminar de las clases y relaciones del sistema.

Estos documentos se utilizarán como referencia para elaborar los diagramas UML y mantener la coherencia del diseño.

### 8. Objetivo del trabajo

Diseñar un sistema orientado a objetos que represente las necesidades de una agencia de turismo receptivo, aplicando encapsulamiento, abstracción, herencia, polimorfismo, principios SOLID y patrones GRASP.

El diseño deberá ser coherente con los requisitos establecidos y permitir una implementación posterior en Java.
