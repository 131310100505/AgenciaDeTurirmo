# Modelo de clases — Agencia de Turismo Rumbo Sur

## 1. Introducción

Este documento presenta el modelo de clases propuesto para el sistema de gestión de la agencia de turismo receptivo Rumbo Sur.

El objetivo es identificar las entidades principales, sus atributos, métodos, responsabilidades y relaciones, estableciendo una base consistente para la elaboración de los diagramas UML y la implementación posterior en Java.

El modelo contempla la administración de tours, salidas programadas, clientes, pasajeros, reservas, tarifas, promociones, pagos, servicios adicionales, guías, vehículos y liquidaciones.

## 2. Clases principales del sistema

### 2.1. Clase Tour

**Responsabilidad:** representar un producto turístico ofrecido por la agencia.

**Atributos:**
- `idTour: int`
- `nombre: String`
- `descripcion: String`
- `categoria: CategoriaTour`
- `duracionDias: int`
- `edadMinima: int`
- `dificultad: Dificultad`
- `itinerario: List<Actividad>`

**Métodos:**
- `agregarActividad(actividad: Actividad): void`
- `obtenerItinerario(): List<Actividad>`
- `requiereSeguro(): boolean`
- `validarEdad(edad: int): boolean`

**Relaciones:**
- Un Tour contiene una o varias Actividades.
- Un Tour puede tener múltiples Salidas.
- Un Tour puede disponer de varias Tarifas.

### 2.2. Clase Actividad

**Responsabilidad:** representar una actividad que forma parte del itinerario de un tour.

**Atributos:**
- `idActividad: int`
- `nombre: String`
- `descripcion: String`
- `dia: int`
- `orden: int`

**Métodos:**
- `obtenerDescripcion(): String`

**Relaciones:**
- Cada Actividad pertenece al itinerario de un Tour.

### 2.3. Clase Salida

**Responsabilidad:** representar una salida programada de un tour.

**Atributos:**
- `idSalida: int`
- `fechaInicio: LocalDate`
- `fechaFin: LocalDate`
- `cupoMinimo: int`
- `cupoMaximo: int`
- `estado: EstadoSalida`
- `tour: Tour`
- `guia: Guia`
- `vehiculo: Vehiculo`
- `reservas: List<Reserva>`
- `listaPasajerosGenerada: boolean`

**Métodos:**
- `calcularCuposDisponibles(): int`
- `agregarReserva(reserva: Reserva): void`
- `alcanzoCupoMinimo(): boolean`
- `confirmarSalida(): void`
- `cancelarSalida(): void`
- `asignarGuia(guia: Guia): void`
- `asignarVehiculo(vehiculo: Vehiculo): void`
- `generarListaPasajeros(): List<Pasajero>`
- `puedeIniciarse(): boolean`

**Relaciones:**
- Cada Salida corresponde a un Tour.
- Una Salida puede tener múltiples Reservas.
- Una Salida puede tener un Guía asignado.
- Una Salida puede tener un Vehículo asignado.

### 2.4. Clase Cliente

**Responsabilidad:** representar a la persona que contrata los servicios de la agencia.

**Atributos:**
- `idCliente: int`
- `nombre: String`
- `apellido: String`
- `dni: String`
- `email: String`
- `telefono: String`
- `reservas: List<Reserva>`

**Métodos:**
- `obtenerNombreCompleto(): String`
- `agregarReserva(reserva: Reserva): void`
- `consultarReservas(): List<Reserva>`

**Relaciones:**
- Un Cliente puede realizar múltiples Reservas.
- Cada Reserva corresponde a un Cliente.

### 2.5. Clase Pasajero

**Responsabilidad:** representar a una persona que participa de una salida turística.

**Atributos:**
- `idPasajero: int`
- `nombre: String`
- `apellido: String`
- `dni: String`
- `fechaNacimiento: LocalDate`
- `jubilado: boolean`

**Métodos:**
- `calcularEdad(fechaReferencia: LocalDate): int`
- `esMenorDeTres(fechaReferencia: LocalDate): boolean`
- `esMenorDeDoce(fechaReferencia: LocalDate): boolean`
- `cumpleEdadMinima(tour: Tour, fechaSalida: LocalDate): boolean`

**Relaciones:**
- Un Pasajero puede participar en distintas Reservas.
- Una Reserva incluye uno o varios Pasajeros.

### 2.6. Clase Reserva

**Responsabilidad:** administrar la contratación de una salida por parte de un cliente.

**Atributos:**
- `idReserva: int`
- `fechaReserva: LocalDate`
- `estado: EstadoReserva`
- `cliente: Cliente`
- `salida: Salida`
- `pasajeros: List<Pasajero>`
- `pagos: List<Pago>`
- `contrataciones: List<AdicionalContratado>`
- `importeTotal: BigDecimal`

**Métodos:**
- `agregarPasajero(pasajero: Pasajero): void`
- `reemplazarPasajero(anterior: Pasajero, nuevo: Pasajero): void`
- `calcularImporteTotal(): BigDecimal`
- `calcularTotalPagado(): BigDecimal`
- `calcularSaldoPendiente(): BigDecimal`
- `registrarPago(pago: Pago): void`
- `confirmarReserva(): void`
- `cancelarReserva(): void`

**Relaciones:**
- Cada Reserva pertenece a un Cliente.
- Cada Reserva corresponde a una Salida.
- Una Reserva contiene uno o varios Pasajeros.
- Una Reserva puede registrar múltiples Pagos.
- Una Reserva puede incluir múltiples AdicionalesContratados.

### 2.7. Clase Tarifa

**Responsabilidad:** establecer el precio base de un tour según su temporada.

**Atributos:**
- `idTarifa: int`
- `fechaDesde: LocalDate`
- `fechaHasta: LocalDate`
- `precioBase: BigDecimal`
- `tour: Tour`

**Métodos:**
- `estaVigente(fecha: LocalDate): boolean`
- `obtenerPrecioBase(): BigDecimal`

**Relaciones:**
- Cada Tarifa corresponde a un Tour.
- Un Tour puede tener varias Tarifas.

### 2.8. Clase Promocion

**Responsabilidad:** representar una promoción aplicable a la tarifa turística.

**Atributos:**
- `idPromocion: int`
- `nombre: String`
- `porcentajeDescuento: BigDecimal`
- `tipo: TipoPromocion`

**Métodos:**
- `esAplicable(reserva: Reserva): boolean`
- `calcularDescuento(importe: BigDecimal): BigDecimal`

**Relaciones:**
- Las promociones se evalúan para una Reserva.
- Solo se aplica la promoción válida más conveniente.

### 2.9. Clase Pago

**Responsabilidad:** representar un pago efectuado por una reserva.

**Atributos:**
- `idPago: int`
- `fecha: LocalDate`
- `importe: BigDecimal`
- `medioPago: MedioPago`
- `enCuotas: boolean`
- `recargo: BigDecimal`

**Métodos:**
- `calcularRecargo(): BigDecimal`
- `calcularImporteFinal(): BigDecimal`

**Relaciones:**
- Cada Pago corresponde a una Reserva.
- Una Reserva puede registrar múltiples Pagos.

### 2.10. Clase AjustePago

**Responsabilidad:** registrar correcciones de pagos sin eliminar el historial.

**Atributos:**
- `idAjuste: int`
- `fecha: LocalDate`
- `importeAjuste: BigDecimal`
- `motivo: String`
- `pagoOriginal: Pago`

**Métodos:**
- `obtenerImporteAjuste(): BigDecimal`

**Relaciones:**
- Cada AjustePago referencia un Pago original.
- Un Pago puede tener múltiples ajustes.

### 2.11. Clase ServicioAdicional

**Responsabilidad:** representar un servicio complementario ofrecido por la agencia.

**Atributos:**
- `idServicio: int`
- `nombre: String`
- `descripcion: String`
- `precio: BigDecimal`
- `tipo: TipoServicio`

**Métodos:**
- `obtenerPrecio(): BigDecimal`
- `esSeguro(): boolean`

**Relaciones:**
- Un ServicioAdicional puede contratarse en distintas Reservas.

### 2.12. Clase AdicionalContratado

**Responsabilidad:** registrar un servicio adicional contratado para una reserva.

**Atributos:**
- `idContratacion: int`
- `servicio: ServicioAdicional`
- `pasajero: Pasajero`
- `precioAplicado: BigDecimal`

**Métodos:**
- `calcularSubtotal(): BigDecimal`

**Relaciones:**
- Cada AdicionalContratado pertenece a una Reserva.
- Cada AdicionalContratado corresponde a un ServicioAdicional.
- Cada AdicionalContratado se asigna a un Pasajero.

### 2.13. Clase Guia

**Responsabilidad:** representar a un guía turístico habilitado.

**Atributos:**
- `idGuia: int`
- `nombre: String`
- `apellido: String`
- `dni: String`
- `categoriasHabilitadas: Set<CategoriaTour>`
- `honorarioDiario: BigDecimal`

**Métodos:**
- `estaHabilitado(categoria: CategoriaTour): boolean`
- `estaDisponible(inicio: LocalDate, fin: LocalDate): boolean`
- `calcularHonorario(dias: int, ventasAdicionales: BigDecimal): BigDecimal`

**Relaciones:**
- Un Guia puede estar asignado a múltiples Salidas, siempre que sus fechas no se superpongan.
- Un Guia puede tener múltiples Liquidaciones.

### 2.14. Clase Vehiculo

**Responsabilidad:** representar un vehículo utilizado para las excursiones.

**Atributos:**
- `idVehiculo: int`
- `patente: String`
- `descripcion: String`
- `capacidad: int`
- `habilitado: boolean`

**Métodos:**
- `tieneCapacidad(cantidadPasajeros: int): boolean`
- `estaDisponible(inicio: LocalDate, fin: LocalDate): boolean`

**Relaciones:**
- Un Vehiculo puede utilizarse en múltiples Salidas, siempre que no existan superposiciones.

### 2.15. Clase Liquidacion

**Responsabilidad:** registrar la liquidación de honorarios de un guía correspondiente a una salida.

**Atributos:**
- `idLiquidacion: int`
- `fecha: LocalDate`
- `guia: Guia`
- `salida: Salida`
- `importeFijo: BigDecimal`
- `comisionAdicionales: BigDecimal`
- `importeTotal: BigDecimal`

**Métodos:**
- `calcularImporteTotal(): BigDecimal`
- `obtenerDetalle(): String`

**Relaciones:**
- Cada Liquidacion corresponde a un Guia y una Salida.

## 3. Enumeraciones

Se utilizarán enumeraciones para representar valores predefinidos.

**CategoriaTour**
- AVENTURA
- CULTURAL
- NATURALEZA

**Dificultad**
- BAJA
- MEDIA
- ALTA

**EstadoSalida**
- PROGRAMADA
- CONFIRMADA
- CANCELADA
- EN_CURSO
- FINALIZADA

**EstadoReserva**
- PENDIENTE
- CONFIRMADA
- CANCELADA

**MedioPago**
- EFECTIVO
- TRANSFERENCIA
- TARJETA

**TipoPromocion**
- ANTICIPADA
- GRUPAL

**TipoServicio**
- SEGURO
- TRANSPORTE
- EQUIPAMIENTO
- OTRO

## 4. Relaciones UML y multiplicidades

| Clase origen | Clase destino | Relación | Multiplicidad |
|---|---|---|---|
| Tour | Actividad | Composición | 1 a 1..* |
| Tour | Salida | Asociación | 1 a 0..* |
| Tour | Tarifa | Composición | 1 a 0..* |
| Cliente | Reserva | Asociación | 1 a 0..* |
| Salida | Reserva | Asociación | 1 a 0..* |
| Reserva | Pasajero | Asociación | 1..* a 0..* |
| Reserva | Pago | Composición | 1 a 0..* |
| Pago | AjustePago | Composición | 1 a 0..* |
| Reserva | AdicionalContratado | Composición | 1 a 0..* |
| AdicionalContratado | ServicioAdicional | Asociación | 0..* a 1 |
| AdicionalContratado | Pasajero | Asociación | 0..* a 1 |
| Salida | Guia | Asociación | 0..* a 0..1 |
| Salida | Vehiculo | Asociación | 0..* a 0..1 |
| Guia | Liquidacion | Asociación | 1 a 0..* |
| Salida | Liquidacion | Asociación | 1 a 0..1 |

**Interpretación:** una salida tiene como máximo un guía y un vehículo asignados en este diseño inicial. Cada reserva corresponde a una única salida y a un único cliente.

## 5. Reglas principales que debe respetar el modelo

- No se pueden registrar reservas que excedan el cupo máximo.
- Todos los pasajeros ocupan un lugar, independientemente de su tarifa.
- La edad del pasajero se calcula a la fecha de inicio de la salida.
- Los menores de tres años tienen tarifa cero.
- Los menores de doce años pagan el 60 % de la tarifa base.
- Los jubilados reciben un descuento del 10 %.
- Las promociones por anticipación y grupo no se acumulan entre sí.
- Se aplica la promoción válida más conveniente.
- El seguro es obligatorio para los tours de aventura.
- La reserva requiere una seña mínima del 30 % para confirmarse.
- Los pagos deben conservar su historial mediante ajustes.
- Los guías deben estar habilitados y disponibles.
- Los vehículos deben tener capacidad suficiente.
- Las cancelaciones deben respetar los plazos y porcentajes establecidos.
- Una salida no puede iniciarse sin guía asignado ni lista de pasajeros.
- Los honorarios se calculan según los días trabajados y las ventas de adicionales.

## 6. Consideraciones de diseño

El modelo inicial identifica principalmente entidades del dominio y sus responsabilidades.

En una etapa posterior se incorporarán servicios y componentes de coordinación para separar las reglas de negocio de las operaciones del sistema.

Se evaluará la aplicación de principios SOLID, especialmente responsabilidad única, abierto/cerrado e inversión de dependencias.

También se analizará la aplicación de patrones GRASP, como Experto en Información, Creador, Controlador y Bajo Acoplamiento.

El diseño definitivo deberá conservar la coherencia con los requisitos funcionales, las reglas de negocio y los diagramas UML.

## 7. Conclusión

El modelo propuesto constituye una base para representar el funcionamiento de la agencia Rumbo Sur.

Las clases identificadas permiten administrar los principales procesos del negocio, desde la programación de tours hasta la reserva, el pago, la ejecución de salidas y la liquidación de honorarios.

Este documento servirá como referencia para elaborar el diagrama de clases inicial, los diagramas de secuencia y el modelo final.
