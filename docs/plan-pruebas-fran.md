# Plan de pruebas funcionales: CU08, CU12, CU13, CU17, CU18 y CU19

Responsable: Francisco. Ejecutado el 09/11/2026 sobre la rama `feature/reportes`.

**Cómo se ejecutó:** cada caso se corrió llamando a los controladores con datos nuevos. Los casos R8 a R10 se corrieron desde el menú de `VistaAdministrador`. Los importes están redondeados a centavos.

**Datos comunes:** tour Bariloche, salida 7 del 15/03/2027 al 17/03/2027, cupo de 1 a 10. Reserva de Ana con un pasajero y total de $10.000.

**Reserva con $9.800 pagados:** seña de $3.000 en efectivo con ajuste de −$200, y saldo de $7.000 con tarjeta en 3 cuotas.

## CU08: Registrar pago

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| P1 | $3.000 en efectivo | Recargo $0, pagado $3.000, saldo $7.000 | Recargo $0, pagado $3.000, saldo $7.000 |
| P2 | Después de P1, $7.000 con tarjeta en 3 cuotas | Recargo $490, se cobran $7.490, pagado $10.000, saldo $0 | Recargo $490, se cobran $7.490, pagado $10.000, saldo $0 |
| P3 | $1.000 con tarjeta en 1 cuota | Sin recargo, se cobran $1.000 | Recargo $0, se cobran $1.000 |
| P4 | Importe $0 | Se rechaza | `IllegalArgumentException`: "El importe del pago debe ser mayor que cero." |
| P5 | $1.000 sobre una reserva anulada | Se rechaza | `PagoInvalidoException`: la reserva no admite pagos porque está ANULADA |

## CU19: Registrar ajuste de pago

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| A1 | Ajuste de −$200 sobre un pago de $3.000 | El pago sigue en $3.000, neto $2.800, un ajuste | Importe $3.000, neto $2.800, un ajuste |
| A2 | Ajuste de $0 | Se rechaza | `PagoInvalidoException`: "El importe del ajuste no puede ser cero." |
| A3 | Ajuste de −$3.500 sobre un pago de $3.000 | Se rechaza por dejarlo en negativo | `PagoInvalidoException`: el ajuste no puede dejar el pago en negativo |
| A4 | Ajuste sobre un pago de otra reserva | Se rechaza | `PagoInvalidoException`: "El pago no existe en esa reserva." |

## CU12: Cancelar reserva

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| C1 | $9.800 pagados, cliente, 13/02/2027 (30 días) | Devuelve 90 %: $8.820, ANULADA, libera el cupo | $8.820, ANULADA, ocupación 0 |
| C2 | $9.800 pagados, cliente, 14/02/2027 (29 días) | Devuelve 50 %: $4.900, ANULADA | $4.900, ANULADA, ocupación 0 |
| C3 | $9.800 pagados, cliente, 28/02/2027 (15 días) | Devuelve 50 %: $4.900, ANULADA | $4.900, ANULADA, ocupación 0 |
| C4 | $9.800 pagados, cliente, 01/03/2027 (14 días) | Devuelve 20 %: $1.960, ANULADA | $1.960, ANULADA, ocupación 0 |
| C5 | $9.800 pagados, cliente, 08/03/2027 (7 días) | Devuelve 20 %: $1.960, ANULADA | $1.960, ANULADA, ocupación 0 |
| C6 | $9.800 pagados, cliente, 09/03/2027 (6 días) | Devuelve 0 %: $0, ANULADA | $0, ANULADA, ocupación 0 |
| C7 | $9.800 pagados, cliente, 15/03/2027 (día de inicio) | Devuelve 0 %: $0, ANULADA | $0, ANULADA, ocupación 0 |
| C8 | $9.800 pagados, agencia, 09/03/2027 | Devuelve 100 %: $9.800, ANULADA | $9.800, ANULADA, ocupación 0 |
| C9 | Sin pagos, cliente, 01/02/2027 | Devuelve $0, ANULADA | $0, ANULADA, ocupación 0 |
| C10 | Cancelar una reserva ya anulada | Se rechaza | `ReservaNoCancelableException`: la reserva ya está ANULADA |
| C11 | $9.800 pagados, cliente, 16/03/2027 | Se rechaza porque la salida ya comenzó | `ReservaNoCancelableException`: la salida ya comenzó |

## CU13: Controlar vencimientos de pago

Fecha límite del saldo: 05/03/2027, diez días antes del inicio.

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| V1 | $3.000 pagados, control el 05/03/2027 | Sigue vigente | CONFIRMADA, ocupación 1 |
| V2 | $3.000 pagados, control el 06/03/2027 | Se anula y libera el cupo | ANULADA, ocupación 0 |
| V3 | $10.000 pagados, control el 06/03/2027 | No cambia | CONFIRMADA, ocupación 1 |

## CU17: Cerrar salida y liquidar guía

Guía a $500 por día y $2.000 vendidos en adicionales.

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| L1 | Cerrar la salida finalizada | 3 días, fijo $1.500, comisión $100, total $1.600 | 3 días, fijo $1.500, comisión $100, total $1.600 |
| L2 | Cerrar una salida sin finalizar | Se rechaza | `TransicionEstadoInvalidaException`: "La salida 7 todavia no finalizo" |
| L3 | Cerrar dos veces la misma salida | Se rechaza la segunda | `TransicionEstadoInvalidaException`: "La salida 7 ya fue liquidada" |

## CU18: Generar reportes

Período de R1 a R4 y R6: del 01/01/2026 al 31/12/2027.

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| R1 | Ocupación, reserva confirmada | Salida 7: 1 de 10 lugares (10 %) | "Salida 7 - Bariloche - 2027-03-15 - 1 de 10 lugares (10 %) - CONFIRMADA" |
| R2 | Ventas, reserva de $10.000 | Luis: 1 reserva, $10.000 | "Luis (legajo 1) - 1 reserva(s) - $10000.00" |
| R3 | Ingresos, $9.800 pagados y $1.960 devueltos | Bariloche: $7.840 | "Bariloche - 1 reserva(s) - $7840.00" |
| R4 | Cancelaciones, una con $1.960 devueltos | Una cancelación, total $1.960 | "Total: 1 cancelación(es) - devuelto $1960.00" |
| R5 | Ventas del 01/01/2020 al 02/01/2020 | Informa que no hay resultados | "No hay resultados para el período indicado." |
| R8 | Menú: fecha 31/02/2027 | Avisa y vuelve a preguntar | "Error: fecha inválida. Use el formato dd/mm/aaaa…" y vuelve a pedir la fecha |
| R9 | Menú: hasta anterior a desde | Avisa y vuelve al menú | "La fecha final no puede ser anterior a la inicial." |

**Resultado:** los 33 casos dieron el resultado esperado.
