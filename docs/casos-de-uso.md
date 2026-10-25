# Casos de uso — Agencia de Turismo Rumbo Sur

## 1. Introducción

Este documento describe los casos de uso del sistema de gestión Rumbo Sur, correspondientes a la Propuesta B del Trabajo Práctico Obligatorio de Programación Orientada a Objetos.

Los casos de uso se definieron a partir de los requisitos funcionales y las reglas de negocio documentadas en `requisitos.md` y `supuestos.md`.

Cada caso de uso identifica al actor principal, sus precondiciones, el flujo principal y los posibles flujos alternativos.

## 2. Actores

### Vendedor

Registra clientes y pasajeros, crea y modifica reservas, contrata adicionales, registra pagos y gestiona cancelaciones.

### Coordinador de operaciones

Programa salidas, asigna guías y vehículos, genera listas de pasajeros y realiza el seguimiento y cierre de las salidas.

### Administrador

Administra tours, itinerarios, tarifas y reportes.

El cliente no se representa como actor directo porque, según los supuestos del sistema, sus solicitudes se realizan a través del vendedor.

## 3. Descripción de casos de uso

### CU01 — Administrar tours

**Actor principal:** Administrador.

**Precondiciones:** el administrador tiene acceso al sistema.

**Flujo principal:**
1. El administrador selecciona la gestión de tours.
2. El sistema muestra los tours registrados.
3. El administrador registra, modifica o consulta un tour.
4. El sistema valida los datos ingresados.
5. El sistema guarda o muestra la información solicitada.

**Flujos alternativos:**
- Si faltan datos obligatorios, el sistema informa el error.
- Si se intenta registrar un código existente, se rechaza la operación.

### CU02 — Administrar itinerarios y tarifas

**Actor principal:** Administrador.

**Precondiciones:** existe un tour registrado.

**Flujo principal:**
1. El administrador selecciona un tour.
2. Agrega o modifica las actividades del itinerario.
3. Define el día, orden, horario y lugar de cada actividad.
4. Registra las tarifas y sus períodos de vigencia.
5. El sistema valida y guarda los cambios.

**Flujos alternativos:**
- Si una actividad tiene datos incompletos, se solicita su corrección.
- Si las tarifas presentan períodos incompatibles, el sistema informa el conflicto.

### CU03 — Programar salida

**Actor principal:** Coordinador de operaciones.

**Precondiciones:** existe un tour disponible.

**Flujo principal:**
1. El coordinador selecciona un tour.
2. Ingresa las fechas de inicio y finalización.
3. Define los cupos mínimo y máximo.
4. El sistema valida los datos.
5. Registra la salida con estado `PROGRAMADA`.

**Flujos alternativos:**
- Si la fecha de finalización es anterior a la de inicio, se rechaza la operación.
- Si el cupo mínimo supera el máximo, se solicita corregirlo.

### CU04 — Registrar cliente y pasajero

**Actor principal:** Vendedor.

**Precondiciones:** el vendedor tiene acceso al sistema.

**Flujo principal:**
1. El vendedor selecciona la opción de registro.
2. Ingresa los datos del cliente.
3. Registra los datos de uno o varios pasajeros.
4. El sistema valida la información.
5. Guarda los registros.

**Flujos alternativos:**
- Si falta información obligatoria, se solicita completarla.
- Si ya existe un registro con el mismo identificador, el sistema evita duplicarlo.

### CU05 — Crear reserva

**Actor principal:** Vendedor.

**Precondiciones:** existe un cliente registrado y una salida que admite reservas.

**Flujo principal:**
1. El vendedor selecciona el cliente.
2. Selecciona una salida programada.
3. Incorpora uno o más pasajeros.
4. El sistema valida los cupos y las restricciones del tour.
5. El sistema calcula el importe de la reserva.
6. Registra la reserva con estado `PENDIENTE`.
7. Los pasajeros ocupan los lugares correspondientes de la salida.

**Flujos alternativos:**
- Si no hay cupos suficientes, se rechaza la reserva.
- Si un pasajero no cumple la edad mínima, se informa el motivo.
- Si faltan datos obligatorios, se solicita completarlos.

### CU06 — Calcular precio y promociones

**Actor principal:** Vendedor.

**Precondiciones:** se seleccionaron una salida y los pasajeros de la reserva.

**Flujo principal:**
1. El sistema identifica la tarifa vigente.
2. Determina la categoría de cada pasajero.
3. Calcula la tarifa correspondiente.
4. Evalúa los descuentos y promociones aplicables.
5. Aplica únicamente el beneficio más conveniente.
6. Incorpora los importes de los servicios adicionales.
7. Presenta el total calculado.

**Flujos alternativos:**
- Si no existe una tarifa vigente, el sistema informa el error.
- Si una promoción no cumple sus condiciones, no se aplica.

### CU07 — Contratar servicios adicionales

**Actor principal:** Vendedor.

**Precondiciones:** existe una reserva registrada.

**Flujo principal:**
1. El vendedor selecciona la reserva.
2. Consulta los servicios adicionales disponibles.
3. Selecciona los servicios para los pasajeros correspondientes.
4. El sistema verifica los seguros obligatorios.
5. Registra los adicionales contratados.
6. Actualiza el importe de la reserva.

**Flujos alternativos:**
- Si falta el seguro obligatorio de un tour de aventura, el sistema exige incorporarlo antes de completar la contratación.
- Si un servicio no está disponible, informa el inconveniente.

### CU08 — Registrar pago

**Actor principal:** Vendedor.

**Precondiciones:** existe una reserva que admite pagos.

**Flujo principal:**
1. El vendedor selecciona la reserva.
2. Ingresa el importe y el medio de pago.
3. Si corresponde, indica la cantidad de cuotas.
4. El sistema calcula el recargo aplicable.
5. Registra el pago.
6. Actualiza el saldo pendiente.

**Flujos alternativos:**
- Si el importe es inválido, se rechaza el registro.
- Si el pago es con tarjeta en cuotas, se aplica el recargo del 7 % sobre el importe abonado mediante esa modalidad.

### CU09 — Confirmar reserva

**Actor principal:** Vendedor.

**Precondiciones:** existe una reserva en estado `PENDIENTE`.

**Flujo principal:**
1. El vendedor selecciona la reserva.
2. El sistema calcula el total neto abonado.
3. Verifica que alcance al menos el 30 % del importe exigible.
4. Cambia el estado de la reserva a `CONFIRMADA`.
5. Informa el saldo pendiente.

**Flujos alternativos:**
- Si los pagos no alcanzan la seña mínima, la reserva continúa `PENDIENTE`.
- Si la reserva fue anulada, no puede confirmarse.

### CU10 — Modificar reserva

**Actor principal:** Vendedor.

**Precondiciones:** existe una reserva vigente.

**Flujo principal:**
1. El vendedor busca la reserva.
2. Selecciona los datos o servicios que desea modificar.
3. El sistema valida las restricciones y los cupos.
4. Recalcula el importe cuando corresponda.
5. Registra los cambios.

**Flujos alternativos:**
- Si una modificación supera los cupos, se rechaza.
- Si la modificación afecta importes ya pagados, se registra el ajuste correspondiente.
- Si la reserva está anulada o finalizada, no se permite una modificación operativa.

### CU11 — Reemplazar pasajero

**Actor principal:** Vendedor.

**Precondiciones:** existe una reserva vigente y faltan al menos tres días para la salida.

**Flujo principal:**
1. El vendedor selecciona la reserva.
2. Identifica al pasajero que será reemplazado.
3. Ingresa o selecciona al nuevo pasajero.
4. El sistema valida la fecha límite y las restricciones del tour.
5. Actualiza la reserva y conserva el registro de la modificación.

**Flujos alternativos:**
- Si faltan menos de tres días, se rechaza el reemplazo.
- Si el nuevo pasajero no cumple los requisitos del tour, se rechaza la operación.

### CU12 — Cancelar reserva

**Actor principal:** Vendedor.

**Precondiciones:** existe una reserva que puede ser cancelada.

**Flujo principal:**
1. El vendedor selecciona la reserva.
2. Indica el motivo y el origen de la cancelación.
3. El sistema calcula los días de anticipación.
4. Determina el porcentaje de devolución aplicable.
5. Calcula el importe a devolver sobre los pagos netos registrados.
6. Registra la cancelación y la devolución correspondiente.
7. Cambia la reserva a estado `ANULADA`.
8. Libera los lugares ocupados.

**Flujos alternativos:**
- Si cancela la agencia, corresponde devolver el 100 % de lo efectivamente abonado.
- Si la cancelación es solicitada por el cliente, se aplica la política de devolución según la anticipación.
- Si no existen pagos, el importe a devolver es cero.

### CU13 — Controlar vencimientos de pago

**Actor principal:** Coordinador de operaciones.

**Precondiciones:** existen reservas registradas para salidas futuras.

**Flujo principal:**
1. El coordinador solicita revisar los vencimientos.
2. El sistema identifica las reservas cuyo saldo debía estar abonado diez días antes de la salida.
3. Verifica los importes pendientes.
4. Anula las reservas que incumplen el plazo.
5. Registra el motivo de la anulación.
6. Libera los cupos correspondientes.

**Flujos alternativos:**
- Si la reserva está totalmente pagada, no se modifica.
- Si todavía no llegó la fecha límite, permanece vigente.

### CU14 — Asignar guía y vehículo

**Actor principal:** Coordinador de operaciones.

**Precondiciones:** existe una salida programada.

**Flujo principal:**
1. El coordinador selecciona la salida.
2. Consulta los guías disponibles.
3. El sistema verifica habilitaciones y superposiciones.
4. Selecciona un guía válido.
5. Selecciona un vehículo.
6. El sistema verifica que su capacidad alcance el cupo máximo de la salida.
7. Registra las asignaciones.

**Flujos alternativos:**
- Si el guía no está habilitado o tiene otra salida superpuesta, se rechaza la asignación.
- Si el vehículo no tiene capacidad suficiente, se solicita seleccionar otro.

### CU15 — Generar lista de pasajeros

**Actor principal:** Coordinador de operaciones.

**Precondiciones:** existe una salida con reservas vigentes.

**Flujo principal:**
1. El coordinador selecciona una salida.
2. El sistema obtiene las reservas que conservan pasajeros habilitados para viajar.
3. Reúne los datos de los pasajeros.
4. Genera la lista de pasajeros.
5. Registra que la lista fue generada.

**Flujos alternativos:**
- Si no existen pasajeros habilitados, el sistema informa que no puede generar una lista operativa válida.

### CU16 — Gestionar estado de salida

**Actor principal:** Coordinador de operaciones.

**Precondiciones:** existe una salida registrada.

**Flujo principal:**
1. El coordinador selecciona la salida.
2. El sistema verifica su ocupación y estado actual.
3. Confirma la salida cuando alcanza el cupo mínimo.
4. Marca la salida como `COMPLETA` cuando alcanza el cupo máximo.
5. Permite iniciarla cuando cumple las condiciones operativas.
6. Registra su finalización cuando termina.

**Flujos alternativos:**
- Si siete días antes del inicio no se alcanzó el cupo mínimo, la salida se cancela y se tramitan devoluciones completas.
- Si no hay guía asignado o lista de pasajeros generada, no se permite iniciar la salida.
- Si la transición de estado no es válida, se rechaza.

### CU17 — Cerrar salida y liquidar guía

**Actor principal:** Coordinador de operaciones.

**Precondiciones:** la salida se encuentra finalizada.

**Flujo principal:**
1. El coordinador selecciona la salida finalizada.
2. El sistema determina los días trabajados por el guía.
3. Calcula el importe fijo correspondiente.
4. Calcula el 5 % de las ventas de servicios adicionales de la salida.
5. Obtiene el total de honorarios.
6. Registra la liquidación y el cierre operativo.

**Flujos alternativos:**
- Si la salida no finalizó, no se permite cerrarla.
- Si ya existe una liquidación para la salida, se evita duplicarla.

### CU18 — Generar reportes

**Actor principal:** Administrador.

**Precondiciones:** existen datos registrados en el sistema.

**Flujo principal:**
1. El administrador selecciona el tipo de reporte.
2. Indica los filtros o el período.
3. El sistema consulta la información.
4. Calcula los indicadores correspondientes.
5. Presenta el reporte.

**Flujos alternativos:**
- Si no existen datos para el período seleccionado, se informa que no hay resultados.

### CU19 — Registrar ajuste de pago

**Actor principal:** Vendedor.

**Precondiciones:** existe un pago previamente registrado.

**Flujo principal:**
1. El vendedor selecciona la reserva y el pago.
2. Indica el motivo de la corrección.
3. Ingresa el importe del ajuste.
4. El sistema valida la operación.
5. Registra el ajuste sin modificar el pago original.
6. Actualiza los importes netos de la reserva.

**Flujos alternativos:**
- Si el ajuste no es válido, se rechaza.
- Si el pago no existe, se informa el error.

## 4. Relaciones entre casos de uso

Para el diagrama UML se proponen las siguientes relaciones:

- **Crear reserva** `<<include>>` **Calcular precio y promociones**, porque el importe debe determinarse durante la creación.
- **Crear reserva** `<<include>>` **Validar cupos y restricciones**, como comportamiento obligatorio de la operación.
- **Confirmar reserva** `<<include>>` **Verificar seña mínima**, porque la confirmación depende del pago registrado.
- **Cancelar reserva** `<<include>>` **Calcular devolución**, porque toda cancelación requiere determinar el importe correspondiente, aunque sea cero.
- **Asignar guía y vehículo** `<<include>>` **Verificar disponibilidad y capacidad**, porque las asignaciones deben cumplir las condiciones operativas.
- **Cerrar salida y liquidar guía** `<<include>>` **Calcular honorarios**, porque es parte obligatoria del cierre.

Las validaciones incluidas pueden representarse como casos de uso auxiliares en el diagrama, aunque no tengan una descripción independiente como objetivos iniciados por un actor.

La contratación de adicionales y el registro de pagos se representan como casos de uso independientes, ya que también pueden ejecutarse después de crear una reserva.

## 5. Correspondencia con los requisitos

Los casos de uso CU01 a CU19 cubren las funcionalidades RF01 a RF19 documentadas en `requisitos.md`.

Los flujos alternativos reflejan las restricciones principales del negocio: control de cupos, edad mínima, seguros obligatorios, pagos, cancelaciones, disponibilidad de recursos y conservación de registros.

Este documento servirá como referencia para construir el diagrama de casos de uso y seleccionar los dos escenarios que se representarán mediante diagramas de secuencia.
