# Supuestos del sistema — Agencia de Turismo Rumbo Sur

## 1. Introducción

El presente documento establece los supuestos adoptados para el diseño del sistema Rumbo Sur.

Estos supuestos permiten resolver aspectos que no se encuentran completamente especificados en el enunciado y mantener la coherencia entre los requisitos, los diagramas UML y la posterior implementación en Java.

## 2. Supuestos generales

**S01 — Usuarios del sistema:** la aplicación será utilizada internamente por vendedores, coordinadores de operaciones y administradores. Los clientes realizarán solicitudes a través del personal.

**S02 — Moneda:** todas las tarifas, pagos, devoluciones y liquidaciones se expresarán en pesos argentinos.

**S03 — Identificadores:** cada tour, salida, cliente, pasajero, reserva y pago tendrá un identificador único.

**S04 — Fechas:** las fechas se manejarán utilizando fechas calendario. Para verificar superposiciones de guías se considerarán los intervalos completos de las salidas.

## 3. Reservas y pasajeros

**S05 — Ocupación:** cada pasajero registrado en una reserva ocupará un lugar del cupo, incluso cuando no pague tarifa por ser menor de tres años.

**S06 — Reserva pendiente:** una reserva recién creada quedará pendiente de confirmación hasta que se registre una seña suficiente.

**S07 — Control de cupos:** las reservas pendientes con lugares asignados ocuparán cupo. Si se incorpora un vencimiento de reservas pendientes, deberá documentarse antes de implementarlo.

**S08 — Categorías de pasajeros:** la edad se calculará a la fecha de inicio de la salida. Para aplicar la categoría de jubilado se utilizará una condición registrada en el pasajero.

**S09 — Restricciones de aventura:** la edad mínima se verificará para cada pasajero y el seguro obligatorio se incorporará a todos los pasajeros de un tour de aventura.

## 4. Tarifas y promociones

**S10 — Tarifa base:** el precio inicial se determinará según el tour, la temporada y la categoría del pasajero.

**S11 — Promociones:** las promociones se evaluarán sobre la tarifa del tour, antes de incorporar los servicios adicionales.

**S12 — Promociones no acumulables:** se aplicará únicamente la promoción que genere el mayor descuento válido.

**S13 — Reserva anticipada:** los días de anticipación se calcularán entre la fecha de creación de la reserva y la fecha de inicio de la salida.

**S14 — Grupo:** una reserva será considerada grupal cuando incluya seis o más pasajeros.

## 5. Pagos y cancelaciones

**S15 — Seña:** la reserva se confirmará cuando los pagos registrados alcancen al menos el 30 % del importe total exigible.

**S16 — Saldo:** el saldo deberá estar abonado diez días antes del inicio de la salida.

**S17 — Recargo:** el recargo del 7 % se aplicará únicamente al importe abonado mediante tarjeta en cuotas.

**S18 — Cancelaciones:** el porcentaje de devolución se determinará según los días de anticipación establecidos en el enunciado.

**S19 — Devoluciones:** el importe a devolver no podrá superar el dinero efectivamente abonado, considerando los ajustes registrados.

**S20 — Historial:** los pagos no se eliminarán físicamente. Toda corrección se realizará mediante una nota de ajuste que permita conservar el historial.

## 6. Operaciones

**S21 — Confirmación de salidas:** una salida se confirmará cuando alcance el cupo mínimo establecido.

**S22 — Cancelación por cupo insuficiente:** siete días antes del inicio se verificará el cupo mínimo y, si no se alcanza, la salida se cancelará y corresponderá la devolución completa.

**S23 — Asignación de recursos:** un guía deberá estar habilitado y disponible durante todo el intervalo de la salida. El vehículo deberá tener capacidad suficiente para los pasajeros.

**S24 — Inicio de salida:** no se permitirá iniciar una salida sin guía asignado ni lista de pasajeros generada.

**S25 — Honorarios:** la liquidación del guía se calculará mediante un importe fijo por día de servicio más el 5 % de las ventas de servicios adicionales correspondientes a la salida.

## 7. Consideraciones finales

Los supuestos definidos se utilizarán como referencia para elaborar los diagramas de casos de uso, clases y secuencia.

Cualquier modificación posterior deberá registrarse para mantener la coherencia del diseño y la implementación.
