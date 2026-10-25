# Modelo de clases inicial — Agencia de Turismo Rumbo Sur

## 1. Introducción

Este documento presenta el modelo de clases inicial del sistema Rumbo Sur, elaborado a partir del enunciado, los requisitos funcionales y los supuestos definidos.

Se identifican las entidades principales, sus atributos, métodos, relaciones y multiplicidades.

Este modelo representa las responsabilidades del negocio antes de aplicar las mejoras de diseño mediante principios SOLID y patrones GRASP. Posteriormente se elaborará un modelo de clases final.

## 2. Convenciones UML

- `+` Público.
- `-` Privado.
- `#` Protegido.
- `0..1` Cero o uno.
- `0..*` Cero o muchos.
- `1..*` Uno o muchos.
- `1` Exactamente uno.

Se utilizarán asociaciones, composiciones, agregaciones y herencia según la dependencia entre los objetos.

Los atributos se consideran privados, salvo que se indique expresamente otra visibilidad.

## 3. Clases del sistema

### 3.1. Tour (clase abstracta)

Representa un producto turístico ofrecido por la agencia.

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

`obtenerCategoria()` y `validarPasajero()` serán sobrescritos por las subclases cuando corresponda.

### 3.2. TourAventura

Hereda de `Tour`.

**Atributos:**
- `-edadMinima: int`
- `-dificultad: String`

**Métodos:**
- `+obtenerCategoria(): CategoriaTour`
- `+validarPasajero(pasajero: Pasajero, fechaSalida: LocalDate): boolean`

La validación debe verificar la edad mínima del pasajero. El seguro obligatorio se controlará durante la contratación.

### 3.3. TourCultural

Hereda de `Tour`.

**Métodos:**
- `+obtenerCategoria(): CategoriaTour`
- `+validarPasajero(pasajero: Pasajero, fechaSalida: LocalDate): boolean`

### 3.4. TourNaturaleza

Hereda de `Tour`.

**Métodos:**
- `+obtenerCategoria(): CategoriaTour`
- `+validarPasajero(pasajero: Pasajero, fechaSalida: LocalDate): boolean`

### 3.5. Actividad

Representa una actividad incluida en el itinerario de un tour.

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

Representa un proveedor externo de servicios turísticos.

**Atributos:**
- `-codigo: int`
- `-nombre: String`
- `-cuit: String`
- `-contacto: String`
- `-tipoServicio: String`

**Métodos:**
- `+actualizarContacto(contacto: String): void`

### 3.7. Tarifa

Representa una tarifa de un tour para una temporada determinada.

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

Representa una realización programada de un tour.

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

La ocupación considera las reservas pendientes y confirmadas que conservan lugares asignados.

### 3.9. Vehiculo

**Atributos:**
- `-patente: String`
- `-capacidad: int`
- `-tipo: TipoVehiculo`

**Métodos:**
- `+tieneCapacidad(cupoRequerido: int): boolean`

### 3.10. Empleado (clase abstracta)

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

**Métodos:**
- `+registrarCliente(cliente: Cliente): void`
- `+crearReserva(cliente: Cliente, salida: Salida): Reserva`

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

**Métodos:**
- `+programarSalida(tour: Tour, inicio: LocalDate, fin: LocalDate): Salida`
- `+asignarRecursos(salida: Salida, guia: Guia, vehiculo: Vehiculo): void`
- `+cerrarSalida(salida: Salida): void`

### 3.14. Administrador

Hereda de `Empleado`.

**Métodos:**
- `+registrarTour(tour: Tour): void`
- `+actualizarTarifa(tarifa: Tarifa): void`
- `+generarReporte(tipo: TipoReporte): String`

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

### 3.16. Pasajero

Representa una persona que participa en una o varias reservas.

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
- `+calcularTotal(): double`
- `+calcularTotalPagado(): double`
- `+registrarPago(pago: Pago): void`
- `+confirmar(): void`
- `+anular(motivo: String): void`

La reserva pertenece a un cliente, a una salida y al vendedor que la gestionó.

### 3.18. ServicioAdicional

**Atributos:**
- `-codigo: int`
- `-nombre: String`
- `-precio: double`
- `-tipo: TipoAdicional`

**Métodos:**
- `+obtenerPrecio(): double`

La obligatoriedad del servicio se determinará según las condiciones del tour.

### 3.19. AdicionalContratado

Representa la contratación de un servicio adicional para un pasajero de una reserva.

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

Los pagos registrados se conservan y no se eliminan físicamente.

### 3.22. AjustePago

Representa una corrección de un pago registrado.

**Atributos:**
- `-codigo: int`
- `-fecha: LocalDate`
- `-importeAjuste: double`
- `-motivo: String`

**Métodos:**
- `+obtenerImporteAjuste(): double`

### 3.23. Cancelacion

Representa la cancelación de una reserva y la devolución correspondiente.

**Atributos:**
- `-codigo: int`
- `-fecha: LocalDate`
- `-motivo: String`
- `-origen: OrigenCancelacion`
- `-diasAnticipacion: int`
- `-porcentajeDevolucion: double`
- `-importeDevuelto: double`

**Métodos:**
- `+calcularDevolucion(importePagado: double): double`
- `+registrarDevolucion(): void`

El cálculo deberá respetar los porcentajes definidos en `requisitos.md`.

### 3.24. Liquidacion

Representa la liquidación de honorarios de un guía por una salida finalizada.

**Atributos:**
- `-codigo: int`
- `-fecha: LocalDate`
- `-diasTrabajados: int`
- `-importeFijo: double`
- `-comisionAdicionales: double`
- `-total: double`

**Métodos:**
- `+calcularTotal(): double`

El total corresponde al honorario diario multiplicado por los días trabajados, más el 5 % de las ventas de servicios adicionales de la salida.

## 4. Enumeraciones

### CategoriaTour
- `AVENTURA`
- `CULTURAL`
- `NATURALEZA`

### Temporada
- `BAJA`
- `MEDIA`
- `ALTA`

### TipoActividad
- `VISITA`
- `TRASLADO`
- `COMIDA`
- `ALOJAMIENTO`
- `OTRA`

### TipoVehiculo
- `MICRO`
- `COMBI`
- `CAMIONETA`

### TipoPasajero
- `ADULTO`
- `MENOR`
- `JUBILADO`

### EstadoSalida
- `PROGRAMADA`
- `CONFIRMADA`
- `COMPLETA`
- `EN_CURSO`
- `FINALIZADA`
- `CANCELADA`

### EstadoReserva
- `PENDIENTE`
- `CONFIRMADA`
- `ANULADA`
- `FINALIZADA`

### MedioPago
- `EFECTIVO`
- `TRANSFERENCIA`
- `TARJETA`

### TipoAdicional
- `EXCURSION`
- `TRASLADO`
- `SEGURO`
- `OTRO`

### TipoPromocion
- `ANTICIPADA`
- `GRUPO`
- `JUBILADO`

### OrigenCancelacion
- `CLIENTE`
- `AGENCIA`

### TipoReporte
- `OCUPACION`
- `VENTAS`
- `INGRESOS`
- `CANCELACIONES`

## 5. Relaciones y multiplicidades UML

Las multiplicidades expresan cuántas instancias de una clase pueden vincularse con una instancia de la otra.

| Clase A | Clase B | Relación | Cantidad de B por cada A |
|---|---|---|---|
| Tour | TourAventura, TourCultural, TourNaturaleza | Herencia | No aplica |
| Empleado | Vendedor, Guia, Coordinador, Administrador | Herencia | No aplica |
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

**Aclaraciones de multiplicidad:**

- Cada reserva pertenece exactamente a un cliente, un vendedor y una salida.
- Cada pago pertenece exactamente a una reserva.
- Cada ajuste corresponde a un pago.
- Cada contratación de adicional corresponde a una reserva, un pasajero de esa reserva y un servicio adicional.
- Cada cancelación corresponde a una reserva.
- Cada liquidación corresponde a una salida y un guía.
- Una misma persona registrada como pasajero puede participar en diferentes reservas.
- Las salidas pueden programarse antes de asignar guía o vehículo, pero no pueden comenzar sin cumplir las condiciones operativas establecidas.
- La composición de actividades y tarifas supone que sus registros forman parte del tour al que pertenecen, sin impedir que otros tours tengan actividades o tarifas con valores equivalentes.

## 6. Reglas que deberán implementarse

El modelo debe permitir controlar:

1. Cupos mínimos y máximos.
2. Validaciones de edad y restricciones.
3. Seguro obligatorio para tours de aventura.
4. Tarifas por temporada y categoría.
5. Descuentos no acumulables.
6. Seña mínima del 30 %.
7. Anulación por saldo impago.
8. Recargo por tarjeta en cuotas.
9. Cancelaciones y devoluciones.
10. Reemplazo de pasajeros.
11. Disponibilidad y habilitación de guías.
12. Capacidad de vehículos.
13. Generación de listas de pasajeros.
14. Liquidación de honorarios.
15. Conservación del historial de pagos y ajustes.

## 7. Consideraciones de diseño

Este documento representa el modelo inicial del sistema.

En la siguiente etapa del diseño se aplicarán tres principios SOLID y cuatro patrones GRASP para mejorar la distribución de responsabilidades, reducir el acoplamiento y facilitar la extensión del sistema.

Entre los aspectos a revisar se encuentran:

- Separar el cálculo de precios de la entidad Reserva.
- Separar la política de cancelación de su registro.
- Incorporar controladores para coordinar los casos de uso.
- Mejorar la distribución de responsabilidades entre entidades y servicios.

Las modificaciones se reflejarán en el diagrama de clases final y se documentarán indicando el problema detectado, la decisión tomada y la ventaja obtenida.
