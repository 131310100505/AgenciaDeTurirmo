# Plan de pruebas funcionales: CU04, CU05, CU06, CU07, CU09, CU10 y CU11

Responsable: Valentín. Ejecutado el 10/11/2026 sobre `main`.

**Cómo se ejecutó:** los casos R, C, P, A, F, M y E se corrieron llamando a `ControladorReservas` con datos nuevos, y los casos V desde el menú de consola (`VistaVendedor`) y la ventana Swing (`VentanaReserva`). Los importes están redondeados a pesos.

**Datos comunes:**
- Tour de aventura *Trekking Fitz Roy*: tarifa $200.000, edad mínima 16 años, cupo de 2 a 10. Salida A1 dentro de 30 días.
- Tour cultural *Bariloche histórico*: tarifa $150.000. Salidas B1 (dentro de 30 días, cupo 20), B2 (dentro de 90 días, cupo 20), B3 (dentro de 20 días, cupo 4) y B4 (dentro de 2 días).
- Promociones: jubilado 10 %, anticipada (más de 60 días) 15 %, grupo (6 o más) 10 %. Seguro de aventura $15.000.
- Vendedor Luis Gómez y clienta Ana Pérez.

## CU04: Registrar cliente y pasajero

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| R1 | Clienta Ana, documento 30111222 | Queda registrada y se encuentra por documento | Registrada; `buscarCliente("30111222")` devuelve Pérez |
| R2 | Otro cliente con el documento 30111222 | Se rechaza | `IllegalArgumentException`: "Ya existe un cliente con documento 30111222." |
| R3 | Pasajero con un documento ya registrado | Se rechaza | `IllegalArgumentException`: "Ya existe un pasajero con documento …" |
| R4 | Pasajero sin documento | Se rechaza | `IllegalArgumentException`: "El documento del pasajero es obligatorio." |

## CU05: Crear reserva

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| C1 | Salida A1 (aventura), 2 adultos | Total 2 × $200.000 + 2 seguros de $15.000 = $430.000, PENDIENTE, ocupa 2 lugares | Reserva 1, total $430.000, PENDIENTE, 2 seguros, ocupación 2/10, seña mínima $129.000 |
| C2 | Salida A1, un adulto y un menor de 11 años | Se rechaza por la edad mínima y no se crea nada | `PasajeroNoHabilitadoException`: "… no cumple las condiciones del tour Trekking Fitz Roy." Reservas del cliente y ocupación sin cambios |
| C3 | Salida B3 (cupo 4), 5 pasajeros | Se rechaza por falta de cupo | `CupoInsuficienteException`: "La salida no tiene lugar para 5 pasajero(s)." Ocupación 0/4 |
| C4 | Salida B1, el mismo pasajero cargado dos veces | Se rechaza y no se crea nada | **Primera ejecución: falló.** Se rechazaba, pero quedaba una reserva a medio armar en el cliente (1 → 2 reservas). **Corregido** (ver abajo). Segunda ejecución: `IllegalArgumentException`: "Hay un pasajero cargado más de una vez en la reserva." Reservas del cliente sin cambios |
| C5 | Reserva sin pasajeros | Se rechaza | `IllegalArgumentException`: "Faltan datos: cliente, salida, vendedor y al menos un pasajero." |

## CU06: Calcular precio y aplicar promociones

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| P1 | Salida B1: adulto, menor de 8 años y bebé de 1 año | $150.000 + 60 % ($90.000) + $0 = $240.000 | Total $240.000 |
| P2 | Salida B1: un jubilado | 10 % de descuento: $135.000 | Total $135.000 |
| P3 | Salida B2 (más de 60 días): un adulto | 15 % de descuento: $127.500 | Total $127.500 |
| P4 | Salida B2: un jubilado (aplican jubilado 10 % y anticipada 15 %) | No se acumulan: se aplica solo el 15 %, $127.500 | Total $127.500 |
| P5 | Salida B1: 6 adultos | 10 % por grupo a cada uno: 6 × $135.000 = $810.000 | Total $810.000 |
| P6 | Salida B1: pasajero de 11 años que cumple 12 antes de la salida | Paga como adulto (S08: edad a la fecha de la salida), $150.000 | Hoy tiene 11, a la salida 12; total $150.000 |
| P7 | Salida dentro de 400 días, sin tarifa vigente | Se informa el error | `IllegalArgumentException`: "El tour no tiene una tarifa vigente para el …" |

## CU07: Contratar servicios adicionales

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| A1 | Excursión de $25.000 para el pasajero de la reserva de P2 | El total sube $25.000 | Total $135.000 → $160.000 |
| A2 | Excursión para un pasajero que no está en la reserva | Se rechaza | `IllegalArgumentException`: "El pasajero del adicional no está en la reserva." |

## CU09: Confirmar reserva

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| F1 | Reserva de P1 ($240.000) con $71.999 pagados | No alcanza la seña de $72.000 (30 %): sigue PENDIENTE | `confirmar()` = false, PENDIENTE |
| F2 | Se paga $1 más: $72.000 | Se confirma | `confirmar()` = true, CONFIRMADA, saldo $168.000 |
| F3 | Confirmar otra vez la misma reserva | No cambia | `confirmar()` = false, sigue CONFIRMADA |
| F4 | Reserva anulada con $50.000 pagados | No se puede confirmar | `confirmar()` = false, sigue ANULADA |

## CU10: Modificar reserva (agregar pasajero)

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| M1 | Agregar un adulto a la reserva de P3 | Se agrega y se recalcula el total | Pasajeros 1 → 2, total $127.500 → $255.000 |
| M2 | Agregar un pasajero en la salida B3, que está llena (4/4) | Se rechaza por cupo | `CupoInsuficienteException`: "La salida no tiene lugar para otro pasajero." |
| M3 | Agregar un menor de 14 años a la reserva de aventura C1 | Se rechaza por la edad mínima | `PasajeroNoHabilitadoException` |
| M4 | Agregar un pasajero a una reserva anulada | Se rechaza | `IllegalArgumentException`: "La reserva no existe o no está vigente." |

## CU11: Reemplazar pasajero

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| E1 | Reserva C1 (aventura, faltan 30 días): reemplazar un adulto por otro | Se reemplaza; el seguro pasa al nuevo pasajero y el total no cambia | Entra el nuevo y sale el anterior, 2 seguros, total $430.000 → $430.000 |
| E2 | Reserva C1: reemplazar por un menor de 14 años | Se rechaza por la edad mínima | `PasajeroNoHabilitadoException` |
| E3 | Reserva en la salida B4 (faltan 2 días) | Se rechaza: solo hasta 3 días antes (S10) | `IllegalArgumentException`: "Solo se puede reemplazar un pasajero hasta 3 días antes de la salida." |
| E4 | Reemplazar un pasajero por él mismo | Se rechaza | `IllegalArgumentException`: "El nuevo pasajero ya está en la reserva." |

## Pruebas desde la interfaz

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| V1 | En el menú del vendedor se escribe `abc` donde se pide una opción | Avisa el error y vuelve a preguntar, sin cortar el programa | "Error: debe ingresar un número entero." y vuelve a pedir la opción |
| V2 | Desde el menú: crear una reserva de 2 adultos en aventura, pagar $150.000 con tarjeta en 3 cuotas, confirmar y cancelar (pedido del cliente, faltan 30 días) | Total $430.000; confirma (supera la seña de $129.000); devuelve el 90 % de lo pagado | Total $430.000, CONFIRMADA, importe a devolver $135.000 |
| V3 | Salir del programa y volver a abrirlo | Los datos se conservan y la numeración sigue | La reserva cancelada aparece ANULADA con su historial, la salida liberó los lugares y la reserva nueva toma el número 2 |
| V4 | Ventana Swing: cliente 30111222 y dos pasajeros registrados | Se crea la reserva | "Reserva 2 creada - Total $430000,00 - PENDIENTE" |
| V5 | Ventana Swing: cliente con documento inexistente | Muestra el error en la ventana | "No existe un cliente con ese documento." en rojo |

## Error detectado y corregido

**Caso C4:** al crear una reserva con un pasajero repetido, el sistema la rechazaba, pero la reserva ya había sido creada por `Cliente.crearReserva()` y quedaba en la lista del cliente. Esto pasaba porque el control de repetidos se hacía recién al agregar los pasajeros.

**Corrección:** en `ControladorReservas.solicitarCrearReserva()` se controla antes de crear la reserva, junto con el cupo y la edad. Para eso se arma un `HashSet` con los pasajeros: como un `Set` no admite repetidos (usa `equals` y `hashCode` de `Pasajero`, por documento), si tiene menos elementos que la lista hay un pasajero repetido. Se agregó la prueba unitaria `unPasajeroRepetidoNoDejaUnaReservaAMedioArmar` y se volvió a ejecutar el caso con resultado correcto.

## CU01, CU02, CU03, CU14 y CU17: casos de uso agregados al menú

Ejecutado el 13/11/2026 desde los menús del administrador y del coordinador, con los datos iniciales. Estos casos de uso tenían la lógica pero no tenían opción en ningún menú; también se agregó el alta de tours, itinerarios y tarifas (`ControladorCatalogo` y `VistaCatalogo`).

| Caso | Entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| T1 | Administrador > Catálogo: tour cultural *Bodegas de Mendoza*, 2 días, cupo de 2 a 15 | Se registra con el código siguiente | "Tour registrado con el código 4" |
| T2 | Tarifa de temporada ALTA para el tour 4, de mañana a 200 días, $100.000 | Queda cargada | "Tarifa agregada"; el detalle la muestra con su vigencia |
| T3 | Actividades del día 1: orden 1 a las 9:30 y orden 2 a las 13:00 | Se agregan y se muestran ordenadas por día y orden | Itinerario: "Día 1 - 1. 09:30 Bodega Norton" y "Día 1 - 2. 13:00 Almuerzo" |
| T4 | Otra actividad en el día 1 con orden 2 | Se rechaza | "Error: Ya hay una actividad en el día 1 con el orden 2." |
| S1 | Coordinador > Programar salida del tour 4, dentro de 40 días | Se crea con fin según la duración y los cupos del tour | "Salida 104 programada del 23/12/2026 al 24/12/2026 (estado PROGRAMADA)" |
| S2 | Asignar a la salida 104 el guía 201 (habilitado solo para naturaleza) | Se rechaza por categoría | "Error: El guia no esta habilitado para este tour" |
| S3 | Asignar a la salida 104 el guía 203 y el vehículo `ac456ef` (en minúsculas) | Se asignan | Guía Carlos Gómez y vehículo AC456EF en el listado de salidas |
| S4 | Salir y volver a abrir el programa | Se conservan el tour, la tarifa, el itinerario y la salida 104 | Todo aparece igual al volver a entrar |
| L1 | Salida 101 (3 días, guía de $45.000 por día): generar lista, iniciar, finalizar y cerrar | Liquida 3 × $45.000 más el 5 % de los adicionales ($0) | "Total a pagar: $135000.00"; la reserva 1 queda FINALIZADA |
| L2 | Cerrar otra vez la salida 101 | Se rechaza | "Error: La salida 101 ya fue liquidada" |
| L3 | Reporte de ocupación después de finalizar | La salida sigue mostrando sus pasajeros | "Salida 101 ... 2 de 20 lugares (10 %) - FINALIZADA" |

**Cupo mínimo:** antes solo se cancelaba si el control se hacía exactamente 7 días antes de la salida. Ahora se cancela si faltan 7 días o menos, y el control también se hace al iniciar el programa. Lo cubren las pruebas unitarias `cancelaPorCupoMinimoAunqueFaltenMenosDeSieteDias` y `noCancelaSiFaltanMasDeSieteDias`.
