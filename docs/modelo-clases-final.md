# Modelo de clases final — Agencia de Turismo Rumbo Sur

## 1. Introducción

Este documento presenta el modelo de clases final del sistema Rumbo Sur, elaborado a partir del modelo inicial y de las mejoras propuestas mediante tres principios SOLID y cuatro patrones GRASP.

Se mantienen las entidades y reglas de negocio del modelo inicial, incorporando nuevas clases y abstracciones para distribuir mejor las responsabilidades.

Las justificaciones se encuentran en `principios-solid-grasp.md`.

## 2. Convenciones

- `+` Público.
- `-` Privado.
- `#` Protegido.
- `1` Exactamente uno.
- `0..1` Cero o uno.
- `0..*` Cero o muchos.
- `1..*` Uno o muchos.

Se utilizarán asociaciones, composiciones, dependencias, herencias y relaciones de implementación de interfaces.

## 3. Clases del dominio

### 3.1. Tour (abstracta)

**Atributos:**
- `-codigo: int`
- `-nombre: String`
- `-descripcion: String`
- `-duracionDias: int`
- `-cupoMinimo: int`
- `-cupoMaximo: int`

**Métodos:**
- `+obtenerCategoria(): CategoriaTour`
- `+validarPasajero(pasajero: Pasajero, fechaSalida: LocalDate): boolean`
- `+agregarActividad(actividad: Actividad): void`
- `+agregarTarifa(tarifa: Tarifa): void`

### 3.2. TourAventura

Hereda de `Tour`.

**Atributos:**
- `-edadMinima: int`
- `-dificultad: String`

**Métodos sobrescritos:**
- `+obtenerCategoria(): CategoriaTour`
- `+validarPasajero(pasajero: Pasajero, fechaSalida: LocalDate): boolean`

### 3.3. TourCultural

Hereda de `Tour`.

**Métodos sobrescritos:**
- `+obtenerCategoria(): CategoriaTour`
- `+validarPasajero(pasajero: Pasajero, fechaSalida: LocalDate): boolean`

### 3.4. TourNaturaleza

Hereda de `Tour`.

**Métodos sobrescritos:**
- `+obtenerCategoria(): CategoriaTour`
- `+validarPasajero(pasajero: Pasajero, fechaSalida: LocalDate): boolean`

### 3.5. Actividad

**Atributos:**
- `-codigo: int`
- `-dia: int`
- `-orden: int`
- `-horario: LocalTime`
- `-descripcion: String`
- `-lugar: String`
- `-tipo: TipoActividad`

**Métodos:**
- `+modificarHorario(horario: LocalTime): void`
- `+asignarProveedor(proveedor: Proveedor): void`

### 3.6. Proveedor

**Atributos:**
- `-codigo: int`
- `-nombre: String`
- `-cuit: String`
- `-contacto: String`
- `-tipoServicio: String`

**Métodos:**
- `+actualizarContacto(contacto: String): void`

### 3.7. Tarifa

**Atributos:**
- `-codigo: int`
- `-temporada: Temporada`
- `-fechaInicioVigencia: LocalDate`
- `-fechaFinVigencia: LocalDate`
- `-precioBase: double`

**Métodos:**
- `+estaVigente(fecha: LocalDate): boolean`
- `+obtenerPrecioBase(): double`

### 3.8. Salida

**Atributos:**
- `-codigo: int`
- `-fechaInicio: LocalDate`
- `-fechaFin: LocalDate`
- `-cupoMinimo: int`
- `-cupoMaximo: int`
- `-estado: EstadoSalida`
- `-listaPasajerosGenerada: boolean`

**Métodos:**
- `+calcularOcupacion(): int`
- `+verificarCupo(cantidad: int): boolean`
- `+actualizarEstado(): void`
- `+asignarGuia(guia: Guia): void`
- `+asignarVehiculo(vehiculo: Vehiculo): void`
- `+generarListaPasajeros(): void`
- `+iniciarSalida(): void`
- `+finalizarSalida(): void`
- `+cancelarSalida(motivo: String): void`

La clase `Salida` conserva la responsabilidad de calcular ocupación y controlar cupos, aplicando GRASP Experto en Información.

### 3.9. Vehiculo

**Atributos:**
- `-patente: String`
- `-capacidad: int`
- `-tipo: TipoVehiculo`

**Métodos:**
- `+tieneCapacidad(cupoRequerido: int): boolean`

### 3.10. Empleado (abstracta)

**Atributos:**
- `-legajo: int`
- `-nombre: String`
- `-documento: String`
- `-telefono: String`

**Métodos:**
- `+obtenerDatos(): String`

### 3.11. Vendedor

Hereda de `Empleado`.

**Atributos:**
- `-porcentajeComision: double`

### 3.12. Guia

Hereda de `Empleado`.

**Atributos:**
- `-idiomas: List<String>`
- `-categoriasHabilitadas: Set<CategoriaTour>`
- `-honorarioPorDia: double`

**Métodos:**
- `+estaHabilitado(categoria: CategoriaTour): boolean`
- `+estaDisponible(inicio: LocalDate, fin: LocalDate): boolean`
- `+calcularHonorarios(salida: Salida): double`

### 3.13. Coordinador

Hereda de `Empleado`.

### 3.14. Administrador

Hereda de `Empleado`.

Las operaciones generales del sistema se coordinan mediante controladores, evitando concentrarlas en los empleados.

### 3.15. Cliente

**Atributos:**
- `-codigo: int`
- `-nombre: String`
- `-apellido: String`
- `-documento: String`
- `-telefono: String`
- `-correo: String`
- `-paisResidencia: String`

**Métodos:**
- `+actualizarContacto(telefono: String, correo: String): void`
- `+crearReserva(salida: Salida, vendedor: Vendedor, pasajeros: List<Pasajero>): Reserva`

La creación de reservas se asigna a `Cliente` por el patrón GRASP Creador.

### 3.16. Pasajero

**Atributos:**
- `-codigo: int`
- `-nombre: String`
- `-documento: String`
- `-fechaNacimiento: LocalDate`
- `-tipo: TipoPasajero`
- `-contactoEmergencia: String`
- `-restricciones: String`

**Métodos:**
- `+calcularEdad(fecha: LocalDate): int`
- `+cumpleEdadMinima(edadMinima: int, fecha: LocalDate): boolean`

### 3.17. Reserva

**Atributos:**
- `-numero: int`
- `-fechaCreacion: LocalDate`
- `-estado: EstadoReserva`
- `-total: double`

**Métodos:**
- `+agregarPasajero(pasajero: Pasajero): void`
- `+reemplazarPasajero(actual: Pasajero, nuevo: Pasajero): void`
- `+calcularTotalPagado(): double`
- `+registrarPago(pago: Pago): void`
- `+confirmar(): void`
- `+anular(motivo: String): void`

A diferencia del modelo inicial, `Reserva` ya no calcula directamente los precios. Esa responsabilidad se delega a `CalculadoraPrecio`.

### 3.18. ServicioAdicional

**Atributos:**
- `-codigo: int`
- `-nombre: String`
- `-precio: double`
- `-tipo: TipoAdicional`

**Métodos:**
- `+obtenerPrecio(): double`

### 3.19. AdicionalContratado

**Atributos:**
- `-codigo: int`
- `-cantidad: int`
- `-precioUnitarioAplicado: double`

**Métodos:**
- `+calcularSubtotal(): double`

### 3.20. Promocion

**Atributos:**
- `-codigo: int`
- `-nombre: String`
- `-tipo: TipoPromocion`
- `-porcentajeDescuento: double`

**Métodos:**
- `+esAplicable(reserva: Reserva, pasajero: Pasajero): boolean`
- `+calcularDescuento(importeBase: double): double`

### 3.21. Pago

**Atributos:**
- `-codigo: int`
- `-fecha: LocalDate`
- `-importe: double`
- `-medio: MedioPago`
- `-cantidadCuotas: int`
- `-recargo: double`

**Métodos:**
- `+calcularRecargo(): double`
- `+obtenerImporteTotal(): double`

### 3.22. AjustePago

**Atributos:**
- `-codigo: int`
- `-fecha: LocalDate`
- `-importeAjuste: double`
- `-motivo: String`

**Métodos:**
- `+obtenerImporteAjuste(): double`

### 3.23. Cancelacion

**Atributos:**
- `-codigo: int`
- `-fecha: LocalDate`
- `-motivo: String`
- `-origen: OrigenCancelacion`
- `-importeDevuelto: double`

**Métodos:**
- `+registrarDevolucion(): void`

La devolución se calcula utilizando una implementación de la interfaz `PoliticaCancelacion`.

### 3.24. Liquidacion

**Atributos:**
- `-codigo: int`
- `-fecha: LocalDate`
- `-diasTrabajados: int`
- `-importeFijo: double`
- `-comisionAdicionales: double`
- `-total: double`

**Métodos:**
- `+calcularTotal(): double`

## 4. Nuevas clases y abstracciones

### 4.1. CalculadoraPrecio

Responsable de calcular el importe de las reservas.

**Métodos:**
- `+calcularTarifaPasajero(pasajero: Pasajero, tarifa: Tarifa, fechaSalida: LocalDate): double`
- `+obtenerMejorDescuento(reserva: Reserva, pasajero: Pasajero): double`
- `+calcularAdicionales(reserva: Reserva): double`
- `+calcularTotal(reserva: Reserva): double`

**Aplicación:** SOLID — SRP.

### 4.2. PoliticaCancelacion (interfaz)

Define el contrato para calcular devoluciones.

**Método:**
- `+calcularDevolucion(reserva: Reserva, fechaCancelacion: LocalDate): double`

### 4.3. PoliticaCancelacionCliente

Implementa `PoliticaCancelacion`.

**Método:**
- `+calcularDevolucion(reserva: Reserva, fechaCancelacion: LocalDate): double`

Aplica los porcentajes correspondientes a la anticipación de la cancelación.

### 4.4. PoliticaCancelacionAgencia

Implementa `PoliticaCancelacion`.

**Método:**
- `+calcularDevolucion(reserva: Reserva, fechaCancelacion: LocalDate): double`

Calcula el 100 % del importe neto efectivamente abonado.

**Aplicación:** SOLID — OCP y DIP.

### 4.5. ControladorReservas

Coordina los casos de uso relacionados con reservas.

**Métodos:**
- `+crearReserva(cliente: Cliente, salida: Salida, vendedor: Vendedor, pasajeros: List<Pasajero>): Reserva`
- `+modificarReserva(reserva: Reserva): void`
- `+confirmarReserva(reserva: Reserva): void`
- `+cancelarReserva(reserva: Reserva, origen: OrigenCancelacion): Cancelacion`
- `+registrarPago(reserva: Reserva, pago: Pago): void`
- `+controlarVencimientos(): void`

Delega los cálculos a `CalculadoraPrecio` y a las políticas de cancelación.

### 4.6. ControladorSalidas

Coordina los casos de uso relacionados con salidas.

**Métodos:**
- `+programarSalida(tour: Tour, inicio: LocalDate, fin: LocalDate): Salida`
- `+asignarRecursos(salida: Salida, guia: Guia, vehiculo: Vehiculo): void`
- `+generarListaPasajeros(salida: Salida): void`
- `+actualizarEstado(salida: Salida): void`
- `+cerrarSalida(salida: Salida): Liquidacion`

### 4.7. ControladorReportes

Coordina la generación de reportes.

**Métodos:**
- `+generarReporte(tipo: TipoReporte, desde: LocalDate, hasta: LocalDate): String`

**Aplicación:** GRASP — Controlador y Bajo Acoplamiento.

## 5. Enumeraciones

Se mantienen las enumeraciones del modelo inicial:

- `CategoriaTour`: AVENTURA, CULTURAL, NATURALEZA.
- `Temporada`: BAJA, MEDIA, ALTA.
- `TipoActividad`: VISITA, TRASLADO, COMIDA, ALOJAMIENTO, OTRA.
- `TipoVehiculo`: MICRO, COMBI, CAMIONETA.
- `TipoPasajero`: ADULTO, MENOR, JUBILADO.
- `EstadoSalida`: PROGRAMADA, CONFIRMADA, COMPLETA, EN_CURSO, FINALIZADA, CANCELADA.
- `EstadoReserva`: PENDIENTE, CONFIRMADA, ANULADA, FINALIZADA.
- `MedioPago`: EFECTIVO, TRANSFERENCIA, TARJETA.
- `TipoAdicional`: EXCURSION, TRASLADO, SEGURO, OTRO.
- `TipoPromocion`: ANTICIPADA, GRUPO, JUBILADO.
- `OrigenCancelacion`: CLIENTE, AGENCIA.
- `TipoReporte`: OCUPACION, VENTAS, INGRESOS, CANCELACIONES.

## 6. Relaciones UML

### 6.1. Herencia

- `TourAventura` hereda de `Tour`.
- `TourCultural` hereda de `Tour`.
- `TourNaturaleza` hereda de `Tour`.
- `Vendedor` hereda de `Empleado`.
- `Guia` hereda de `Empleado`.
- `Coordinador` hereda de `Empleado`.
- `Administrador` hereda de `Empleado`.

### 6.2. Implementación de interfaces

- `PoliticaCancelacionCliente` implementa `PoliticaCancelacion`.
- `PoliticaCancelacionAgencia` implementa `PoliticaCancelacion`.

### 6.3. Relaciones del dominio

| Clase A | Clase B | Relación | Cantidad de B por cada A |
|---|---|---|---|
| Tour | Actividad | Composición | 1..* |
| Tour | Tarifa | Composición | 1..* |
| Tour | Salida | Asociación | 0..* |
| Actividad | Proveedor | Asociación | 0..1 |
| Proveedor | Actividad | Asociación | 0..* |
| Salida | Reserva | Asociación | 0..* |
| Salida | Guia | Asociación | 0..1 |
| Guia | Salida | Asociación | 0..* |
| Salida | Vehiculo | Asociación | 0..1 |
| Vehiculo | Salida | Asociación | 0..* |
| Cliente | Reserva | Asociación | 0..* |
| Vendedor | Reserva | Asociación | 0..* |
| Reserva | Pasajero | Asociación | 1..* |
| Pasajero | Reserva | Asociación | 0..* |
| Reserva | Pago | Composición | 0..* |
| Pago | AjustePago | Composición | 0..* |
| Reserva | AdicionalContratado | Composición | 0..* |
| Pasajero | AdicionalContratado | Asociación | 0..* |
| ServicioAdicional | AdicionalContratado | Asociación | 0..* |
| Reserva | Cancelacion | Composición | 0..1 |
| Salida | Liquidacion | Asociación | 0..1 |
| Guia | Liquidacion | Asociación | 0..* |

Cada reserva pertenece a exactamente un cliente, una salida y un vendedor. Las demás multiplicidades inversas mantienen los criterios del modelo inicial.

### 6.4. Dependencias incorporadas

- `ControladorReservas` utiliza `Cliente`, `Reserva`, `Salida`, `CalculadoraPrecio` y `Cancelacion`.
- `ControladorSalidas` utiliza `Salida`, `Guia`, `Vehiculo` y `Liquidacion`.
- `ControladorReportes` consulta la información de reservas, pagos, salidas y cancelaciones.
- `CalculadoraPrecio` utiliza `Reserva`, `Pasajero`, `Tarifa`, `Promocion` y `AdicionalContratado`.
- `Cancelacion` utiliza `PoliticaCancelacion` para calcular devoluciones.

Estas dependencias representan colaboraciones necesarias para ejecutar operaciones, sin implicar que todas deban almacenarse como atributos permanentes.

## 7. Comparación con el modelo inicial

| Aspecto | Modelo inicial | Modelo final |
|---|---|---|
| Cálculo de precios | Dentro de `Reserva` | Delegado a `CalculadoraPrecio` |
| Cálculo de devoluciones | Dentro de `Cancelacion` | Delegado a `PoliticaCancelacion` |
| Políticas de cancelación | Lógica concentrada en una clase | Interfaz con implementaciones alternativas |
| Creación de reservas | Coordinada desde `Vendedor` | Coordinada por `ControladorReservas` y delegada a `Cliente` |
| Casos de uso | Métodos en clases de empleados | Controladores específicos |
| Control de cupos | Responsabilidad de `Salida` | Se conserva por Information Expert |
| Dependencias | Mayor concentración de responsabilidades | Delegación y uso de abstracciones |

## 8. Justificación del modelo final

El modelo final mantiene la estructura principal del negocio, pero distribuye mejor las responsabilidades.

La separación de cálculos, la incorporación de interfaces y el uso de controladores permiten que las clases del dominio se concentren en representar las entidades y sus reglas propias.

Las modificaciones aplican:

- **SRP:** `CalculadoraPrecio`.
- **OCP:** nuevas implementaciones de `PoliticaCancelacion`.
- **DIP:** dependencia de la interfaz `PoliticaCancelacion`.
- **Information Expert:** control de cupos en `Salida`.
- **Creator:** creación de reservas en `Cliente`.
- **Controller:** controladores de reservas, salidas y reportes.
- **Low Coupling:** delegación de responsabilidades y uso de abstracciones.

## 9. Conclusión

El modelo de clases final constituye una evolución del modelo inicial y conserva las funcionalidades establecidas para Rumbo Sur.

Las mejoras realizadas permiten construir un sistema más organizado, mantenible y extensible, y servirán como base para el diagrama de clases final y la posterior implementación en Java.
