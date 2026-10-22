# Análisis de requisitos — Agencia de Turismo Rumbo Sur

## 1. Descripción del sistema

El sistema Rumbo Sur tiene como objetivo administrar las operaciones de una agencia de turismo receptivo, incluyendo la gestión de tours, salidas programadas, clientes, pasajeros, reservas, pagos, servicios adicionales y cancelaciones.

También permitirá organizar los recursos necesarios para cada salida, como guías y vehículos, y generar información administrativa.

## 2. Actores del sistema

**Vendedor:** registra clientes, gestiona reservas, incorpora pasajeros, registra pagos y tramita cancelaciones.

**Coordinador de operaciones:** programa salidas, asigna guías y vehículos, genera listas de pasajeros y administra el cierre operativo.

**Administrador:** gestiona el catálogo de tours, tarifas y reportes administrativos.

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
- Cada salida tiene un cupo mínimo y máximo.
- La salida se confirma cuando alcanza el cupo mínimo.
- Si faltan siete días para la salida y no se alcanzó el cupo mínimo, se cancela y se devuelve el 100 %.
- No se aceptan reservas que superen el cupo máximo.
- Los guías deben estar habilitados para la categoría del tour y no pueden tener salidas superpuestas.
- Los vehículos deben contar con capacidad suficiente.

### 4.2. Tarifas y promociones

- Los adultos pagan el 100 % de la tarifa.
- Los menores de 12 años pagan el 60 %.
- Los menores de 3 años no pagan.
- Los jubilados reciben un descuento del 10 %.
- Las reservas realizadas con más de 60 días de anticipación reciben un descuento del 15 %.
- Los grupos de seis o más pasajeros reciben un descuento del 10 %.
- Las promociones no son acumulables y se aplica la más favorable.

### 4.3. Adicionales y pagos

- Los servicios adicionales se suman al precio por pasajero.
- El seguro es obligatorio para los tours de aventura.
- Se requiere una seña mínima del 30 % para confirmar la reserva.
- El saldo debe estar abonado diez días antes de la salida.
- Los pagos con tarjeta en cuotas tienen un recargo del 7 %.
- Los pagos registrados no pueden eliminarse; deben corregirse mediante notas de ajuste.

### 4.4. Cancelaciones

- Con 30 días o más de anticipación, se devuelve el 90 %.
- Entre 29 y 15 días, se devuelve el 50 %.
- Entre 14 y 7 días, se devuelve el 20 %.
- Con menos de 7 días, no corresponde devolución.
- Si cancela la agencia, se devuelve el 100 %.
- Se permite reemplazar un pasajero hasta tres días antes de la salida, siempre que cumpla los requisitos.

### 4.5. Operaciones

- No puede iniciarse una salida sin guía asignado.
- No puede iniciarse una salida sin lista de pasajeros.
- Los honorarios del guía se calculan mediante un importe fijo por día más el 5 % de las ventas de adicionales.

## 5. Requisitos de diseño

El sistema deberá desarrollarse utilizando Programación Orientada a Objetos en Java.

Se deberán aplicar encapsulamiento, herencia, polimorfismo y abstracción, además de tres principios SOLID y cuatro patrones GRASP.

Los diagramas UML deberán representar correctamente las clases, relaciones, atributos, métodos y casos de uso del sistema.

El diseño deberá permitir una posterior implementación con persistencia de datos, manejo de excepciones y pruebas.
