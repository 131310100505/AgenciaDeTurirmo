# Supuestos del sistema — Agencia de Turismo Rumbo Sur

## 1. Introducción

El presente documento establece los supuestos adoptados para el análisis y diseño del sistema de gestión de la agencia de turismo receptivo Rumbo Sur.

Estos supuestos permiten resolver aspectos que no se encuentran completamente especificados en el enunciado y mantener la coherencia entre los requisitos, los diagramas UML y la posterior implementación en Java.

Las reglas de negocio obligatorias se encuentran documentadas en `requisitos.md`.

## 2. Supuestos generales

**S01 — Usuarios del sistema:** la aplicación será utilizada internamente por vendedores, coordinadores de operaciones y administradores. Los clientes realizarán solicitudes a través del personal.

**S02 — Moneda:** todas las tarifas, pagos, devoluciones y liquidaciones se expresarán en pesos argentinos.

**S03 — Identificadores:** cada tour, salida, cliente, pasajero, reserva y pago tendrá un identificador único.

**S04 — Fechas:** las fechas se manejarán utilizando fechas calendario. Para verificar superposiciones de guías se considerarán los intervalos completos de las salidas.

## 3. Reservas y pasajeros

**S05 — Ocupación:** cada pasajero registrado en una reserva ocupará un lugar del cupo, incluso cuando no pague tarifa base por ser menor de tres años.

**S06 — Reserva pendiente:** una reserva recién creada quedará en estado `PENDIENTE` hasta que se registre una seña suficiente para su confirmación.

**S07 — Control de cupos:** las reservas pendientes con lugares asignados ocuparán cupo. Cuando una reserva sea anulada, sus lugares quedarán disponibles nuevamente. No se establecerá un vencimiento automático adicional para reservas pendientes, salvo que se documente posteriormente.

**S08 — Categorías de pasajeros:** la edad se calculará a la fecha de inicio de la salida. Para aplicar la condición de jubilado se utilizará un dato registrado en el pasajero.

**S09 — Restricciones de aventura:** la edad mínima se verificará para cada pasajero y el seguro obligatorio se incorporará a todos los pasajeros de un tour de aventura.

**S10 — Reemplazo de pasajeros:** el reemplazo podrá realizarse hasta tres días antes del inicio de la salida, siempre que el nuevo pasajero cumpla las restricciones del tour. La modificación deberá conservar el historial de la reserva.

## 4. Tarifas y promociones

**S11 — Tarifa base:** el precio inicial se determinará según el tour, la temporada y la categoría del pasajero.

**S12 — Aplicación de descuentos:** primero se calculará la tarifa correspondiente a la categoría del pasajero. Luego se evaluarán los descuentos aplicables sobre esa tarifa, antes de incorporar los servicios adicionales.

**S13 — Promociones no acumulables:** los descuentos por jubilación, reserva anticipada y grupo se evaluarán como beneficios alternativos. Se aplicará únicamente el descuento válido que resulte más favorable para el pasajero.

**S14 — Reserva anticipada:** los días de anticipación se calcularán entre la fecha de creación de la reserva y la fecha de inicio de la salida. La promoción corresponde cuando la anticipación sea superior a 60 días.

**S15 — Grupo:** una reserva será considerada grupal cuando incluya seis o más pasajeros que ocupen cupo.

**S16 — Servicios adicionales:** los adicionales se calcularán por pasajero y se incorporarán después de aplicar los descuentos sobre la tarifa base. Los seguros obligatorios de aventura también se registrarán como adicionales.

## 5. Pagos y cancelaciones

**S17 — Seña:** la reserva se confirmará cuando los pagos netos registrados alcancen al menos el 30 % del importe total exigible.

**S18 — Saldo:** el saldo deberá estar abonado diez días antes del inicio de la salida. Si no se cumple este requisito, la reserva se anulará y liberará los lugares ocupados, registrando el motivo de la anulación.

**S19 — Recargo:** el recargo del 7 % se aplicará únicamente al importe abonado mediante tarjeta en cuotas. No se aplicará sobre pagos realizados mediante otros medios.

**S20 — Cancelaciones:** los porcentajes de devolución se determinarán según los días de anticipación y el origen de la cancelación, respetando las reglas de `requisitos.md`.

**S21 — Devoluciones:** el porcentaje correspondiente se aplicará sobre el importe neto efectivamente abonado, considerando los ajustes registrados. La devolución no podrá superar dicho importe.

**S22 — Cancelación de la agencia:** cuando la agencia cancele una salida, se registrará el motivo y se devolverá el 100 % de los importes efectivamente abonados por las reservas afectadas.

**S23 — Historial de pagos:** los pagos registrados no se eliminarán ni se sobrescribirán. Toda corrección se realizará mediante una nota de ajuste asociada al movimiento correspondiente.

**S24 — Registro de devoluciones:** las devoluciones se registrarán como movimientos identificables, vinculados con la reserva y la cancelación correspondiente, conservando el historial de pagos.

## 6. Operaciones

**S25 — Confirmación de salidas:** una salida se confirmará cuando alcance el cupo mínimo establecido. Cuando alcance el cupo máximo, pasará al estado `COMPLETA`.

**S26 — Cancelación por cupo insuficiente:** siete días antes del inicio se verificará el cupo mínimo y, si no se alcanza, la salida se cancelará y corresponderá la devolución completa de los importes efectivamente abonados.

**S27 — Asignación de recursos:** un guía deberá estar habilitado y disponible durante todo el intervalo de la salida. El vehículo deberá tener capacidad al menos igual al cupo máximo de la salida.

**S28 — Inicio de salida:** no se permitirá iniciar una salida sin guía asignado ni lista de pasajeros generada. También deberán cumplirse las demás condiciones operativas aplicables.

**S29 — Honorarios:** la liquidación del guía se calculará mediante un importe fijo por día de servicio más el 5 % de las ventas de servicios adicionales correspondientes a la salida.

**S30 — Historial de estados:** los cambios relevantes de estado de reservas y salidas deberán quedar registrados para facilitar el seguimiento y la auditoría.

## 7. Consideraciones finales

Los supuestos definidos se utilizarán como referencia para elaborar los diagramas de casos de uso, clases y secuencia.

Cualquier modificación posterior deberá documentarse para mantener la coherencia entre los requisitos, el modelo de clases, los diagramas UML y la implementación en Java.
