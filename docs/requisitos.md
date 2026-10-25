# Análisis de requisitos — Agencia de Turismo Rumbo Sur

## 1. Descripción del sistema

El sistema Rumbo Sur tiene como objetivo administrar las operaciones de una agencia de turismo receptivo, incluyendo la gestión de tours, salidas programadas, clientes, pasajeros, reservas, pagos, servicios adicionales y cancelaciones.

También permitirá organizar los recursos necesarios para cada salida, como guías y vehículos, realizar liquidaciones y generar información administrativa.

## 2. Actores del sistema

**Vendedor:** registra clientes, gestiona reservas, incorpora pasajeros, registra pagos y tramita cancelaciones.

**Coordinador de operaciones:** programa salidas, asigna guías y vehículos, genera listas de pasajeros y administra el cierre operativo.

**Administrador:** gestiona el catálogo de tours, actividades, tarifas y reportes administrativos.

**Cliente:** solicita reservas, modificaciones o cancelaciones a través del vendedor. No interactúa directamente con la aplicación en este diseño.

## 3. Requisitos funcionales

- **RF01:** Registrar, modificar y consultar tours.
- **RF02:** Administrar itinerarios, actividades y tarifas por temporada.
- **RF03:** Programar salidas con fechas, cupos mínimos y máximos.
- **RF04:** Registrar y consultar clientes y pasajeros.
- **RF05:** Crear reservas para una salida programada.
- **RF06:** Validar cupos y restricciones de los pasajeros.
- **RF07:** Calcular precios según temporada y categoría de pasajero.
- **RF08:** Aplicar la promoción más conveniente sin acumular descuentos.
- **RF09:** Contratar servicios adicionales y seguros.
- **RF10:** Registrar señas, pagos, saldos y recargos.
- **RF11:** Confirmar reservas que cumplan las condiciones de pago.
- **RF12:** Modificar o cancelar reservas y calcular devoluciones.
- **RF13:** Reemplazar pasajeros dentro del plazo permitido.
- **RF14:** Asignar guías habilitados y vehículos con capacidad suficiente.
- **RF15:** Generar listas de pasajeros para cada salida.
- **RF16:** Controlar el estado de las salidas programadas.
- **RF17:** Registrar el cierre de salidas y liquidar honorarios.
- **RF18:** Emitir reportes de ocupación, ventas, ingresos y cancelaciones.
- **RF19:** Registrar ajustes de pagos sin eliminar movimientos anteriores.

## 4. Reglas de negocio

### 4.1. Tours y salidas

- Los tours pueden ser de aventura, culturales o de naturaleza.
- Los tours de aventura poseen edad mínima y dificultad.
- Cada tour tiene un itinerario compuesto por actividades organizadas por día y horario.
- Cada salida tiene fechas de inicio y fin, un cupo mínimo y un cupo máximo.
- La salida se confirma cuando alcanza el cupo mínimo.
- Cuando alcanza el cupo máximo, pasa al estado `COMPLETA`.
- Si faltan siete días para la salida y no se alcanzó el cupo mínimo, se cancela y se devuelve el 100 % de lo abonado.
- No se aceptan reservas que superen el cupo máximo.
- Los pasajeros de reservas pendientes que tengan lugares asignados también ocupan cupo.
- Los guías deben estar habilitados para la categoría del tour y no pueden tener salidas superpuestas.
- Los vehículos deben tener capacidad suficiente para el cupo máximo de la salida.

**Estados de una salida:**

- `PROGRAMADA`: salida creada y pendiente de alcanzar el cupo mínimo.
- `CONFIRMADA`: salida que alcanzó el cupo mínimo.
- `COMPLETA`: salida que alcanzó el cupo máximo.
- `EN_CURSO`: salida que ya comenzó.
- `FINALIZADA`: salida que terminó.
- `CANCELADA`: salida anulada por la agencia.

### 4.2. Tarifas y promociones

- Los adultos pagan el 100 % de la tarifa.
- Los menores de 12 años pagan el 60 %.
- Los menores de 3 años no pagan tarifa base.
- Los jubilados reciben un descuento del 10 %.
- Las reservas realizadas con más de 60 días de anticipación reciben un descuento del 15 %.
- Los grupos de seis o más pasajeros reciben un descuento del 10 %.
- Las promociones no son acumulables y se aplica la más favorable.
- Los descuentos se calculan sobre las tarifas correspondientes antes de incorporar los servicios adicionales.

### 4.3. Adicionales y pagos

- Los servicios adicionales se suman al precio de cada pasajero.
- El seguro es obligatorio para todos los pasajeros de tours de aventura.
- Se requiere una seña mínima del 30 % del importe total exigible para confirmar la reserva.
- El saldo debe estar abonado diez días antes del inicio de la salida.
- Si no se abona el saldo en el plazo establecido, la reserva se anula y se liberan sus cupos.
- Los pagos con tarjeta en cuotas tienen un recargo del 7 % sobre el importe abonado mediante esa modalidad.
- Los pagos registrados no pueden eliminarse ni modificarse directamente; las correcciones se realizan mediante notas de ajuste.
- Las devoluciones deben registrarse para mantener el historial de movimientos.

**Estados de una reserva:**

- `PENDIENTE`: reserva creada que todavía no cumple las condiciones de confirmación.
- `CONFIRMADA`: reserva con la seña mínima requerida registrada.
- `ANULADA`: reserva cancelada o anulada por incumplimiento de las condiciones.
- `FINALIZADA`: reserva correspondiente a una salida completada.

### 4.4. Cancelaciones

Para las cancelaciones solicitadas por el cliente:

- Con 30 días o más de anticipación, se devuelve el 90 %.
- Entre 29 y 15 días de anticipación, se devuelve el 50 %.
- Entre 14 y 7 días de anticipación, se devuelve el 20 %.
- Con menos de 7 días de anticipación, no corresponde devolución.

Cuando la cancelación es realizada por la agencia:

- Se devuelve el 100 % del importe efectivamente abonado.

**Condiciones adicionales:**

- Los porcentajes de devolución se aplican sobre el importe efectivamente abonado, considerando los ajustes registrados.
- El importe devuelto nunca puede superar el dinero efectivamente abonado.
- Se permite reemplazar un pasajero hasta tres días antes de la salida, siempre que el reemplazante cumpla las condiciones del tour.
- Las cancelaciones y devoluciones deben quedar registradas en el sistema.

### 4.5. Operaciones

- No puede iniciarse una salida sin guía asignado.
- No puede iniciarse una salida sin lista de pasajeros generada.
- El guía debe estar habilitado y disponible durante toda la duración de la salida.
- El vehículo asignado debe cumplir con la capacidad requerida.
- Al finalizar una salida se registra su cierre operativo.
- Los honorarios del guía se calculan mediante un importe fijo por día más el 5 % de las ventas de servicios adicionales correspondientes a la salida.
- El sistema debe permitir emitir reportes de ocupación, ventas, ingresos y cancelaciones.

## 5. Requisitos de diseño

El sistema deberá desarrollarse utilizando Programación Orientada a Objetos en Java.

Se deberán aplicar encapsulamiento, herencia, polimorfismo y abstracción, además de tres principios SOLID y cuatro patrones GRASP.

Los diagramas UML deberán representar correctamente las clases, relaciones, atributos, métodos y casos de uso del sistema.

El diseño deberá permitir una posterior implementación con persistencia de datos, manejo de excepciones y pruebas.

## 6. Relación con los supuestos

Los criterios adoptados para resolver aspectos no especificados completamente en el enunciado se encuentran documentados en `supuestos.md`.

Estos criterios deberán mantenerse consistentes con los diagramas UML y la posterior implementación del sistema.
