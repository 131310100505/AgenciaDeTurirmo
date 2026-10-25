# Aplicación de principios SOLID y patrones GRASP
## Agencia de Turismo — Rumbo Sur

### 1. Introducción

El objetivo de este documento es analizar el modelo de clases inicial del sistema Rumbo Sur e identificar oportunidades de mejora mediante la aplicación de tres principios SOLID y cuatro patrones GRASP.

Las decisiones propuestas buscan distribuir mejor las responsabilidades, reducir el acoplamiento y facilitar el mantenimiento y la extensión del sistema.

Estas mejoras se incorporarán al modelo de clases final y se reflejarán en el diagrama UML correspondiente.

---

## 2. Principios SOLID

### 2.1. SRP — Principio de Responsabilidad Única

**Problema detectado**

En el modelo inicial, la clase `Reserva` concentra responsabilidades relacionadas con el registro de pasajeros, el cálculo de importes, los pagos y la confirmación o anulación de reservas.

Esto provoca que una modificación en las reglas de precios o promociones pueda afectar directamente a una clase que también administra el estado de la reserva.

**Decisión de diseño**

Se propone incorporar la clase `CalculadoraPrecio`, encargada exclusivamente de calcular los importes de las reservas.

La clase `Reserva` conservará la información de la contratación, sus pasajeros, adicionales, pagos y estado, pero delegará el cálculo del precio a `CalculadoraPrecio`.

**Clases involucradas**

- `Reserva`
- `CalculadoraPrecio`
- `Tarifa`
- `Promocion`
- `Pasajero`
- `AdicionalContratado`

**Beneficio obtenido**

Se separa la responsabilidad de calcular precios de la responsabilidad de administrar una reserva.

De esta manera, las modificaciones en tarifas y promociones pueden realizarse sin alterar innecesariamente la lógica de gestión de reservas.

### 2.2. OCP — Principio Abierto/Cerrado

**Problema detectado**

En el modelo inicial, la clase `Cancelacion` contiene la operación `calcularDevolucion()`.

Si las políticas de cancelación cambian o se incorporan nuevas modalidades, sería necesario modificar directamente su lógica de cálculo.

**Decisión de diseño**

Se propone crear la interfaz `PoliticaCancelacion`, que define la operación:

`+calcularDevolucion(reserva: Reserva, fechaCancelacion: LocalDate): double`

Se incorporarán dos implementaciones:

- `PoliticaCancelacionCliente`: calcula la devolución según los días de anticipación.
- `PoliticaCancelacionAgencia`: establece una devolución del 100 % de lo efectivamente abonado.

La clase `Cancelacion` utilizará una implementación de `PoliticaCancelacion` para determinar el importe correspondiente.

**Clases involucradas**

- `Cancelacion`
- `PoliticaCancelacion`
- `PoliticaCancelacionCliente`
- `PoliticaCancelacionAgencia`
- `Reserva`

**Beneficio obtenido**

El sistema podrá incorporar nuevas políticas de cancelación mediante nuevas implementaciones, sin modificar la lógica principal que registra las cancelaciones.

Esto facilita la extensión del comportamiento y reduce el riesgo de introducir errores.

### 2.3. DIP — Principio de Inversión de Dependencias

**Problema detectado**

Si la clase `Cancelacion` dependiera directamente de una clase concreta para calcular las devoluciones, quedaría acoplada a una implementación específica.

Esto dificultaría sustituir o probar distintas políticas de cancelación.

**Decisión de diseño**

La clase `Cancelacion` dependerá de la abstracción `PoliticaCancelacion`, y no de las implementaciones concretas.

Las clases `PoliticaCancelacionCliente` y `PoliticaCancelacionAgencia` implementarán esa interfaz.

La política correspondiente se seleccionará según el origen de la cancelación.

**Clases involucradas**

- `Cancelacion`
- `PoliticaCancelacion`
- `PoliticaCancelacionCliente`
- `PoliticaCancelacionAgencia`

**Beneficio obtenido**

Se reduce el acoplamiento entre la gestión de cancelaciones y las reglas concretas de devolución.

Además, se facilita la prueba independiente de cada política y su reemplazo sin modificar las clases que la utilizan.

---

## 3. Patrones GRASP

### 3.1. Experto en Información (Information Expert)

**Problema detectado**

El sistema necesita conocer la cantidad de lugares ocupados en cada salida para evitar sobreventas.

Si esta responsabilidad se asignara a una clase externa sin acceso directo a las reservas, se necesitarían consultas y dependencias adicionales.

**Decisión de diseño**

Se asigna a la clase `Salida` la responsabilidad de calcular la ocupación y verificar la disponibilidad de cupos.

Operaciones:

- `+calcularOcupacion(): int`
- `+verificarCupo(cantidad: int): boolean`

`Salida` conoce sus reservas asociadas y puede contar los pasajeros de aquellas que mantienen lugares asignados.

**Clases involucradas**

- `Salida`
- `Reserva`
- `Pasajero`

**Beneficio obtenido**

La responsabilidad queda ubicada en la clase que dispone de la información necesaria.

Esto mejora la cohesión y evita duplicar la lógica de control de cupos.

### 3.2. Creador (Creator)

**Problema detectado**

Al registrar una reserva, el sistema debe crear el objeto correspondiente y asociarlo correctamente con el cliente, la salida y el vendedor.

La creación dispersa de reservas en distintas partes del sistema podría provocar inconsistencias.

**Decisión de diseño**

Se asigna al `ControladorReservas` la coordinación del caso de uso, pero la creación de la reserva se delega a `Cliente`, que mantiene una colección de sus reservas.

Se incorpora en `Cliente` la operación:

`+crearReserva(salida: Salida, vendedor: Vendedor, pasajeros: List<Pasajero>): Reserva`

La operación crea una reserva vinculada con el cliente y los demás participantes, luego de recibir los datos validados por el controlador.

**Clases involucradas**

- `Cliente`
- `Reserva`
- `Salida`
- `Vendedor`
- `ControladorReservas`

**Beneficio obtenido**

La creación se asigna a una entidad que mantiene una relación directa con las reservas y las agrupa.

Se centraliza su asociación con el cliente y se evita dispersar la lógica de creación.

### 3.3. Controlador (Controller)

**Problema detectado**

En el modelo inicial, clases como `Vendedor` y `Coordinador` incluyen métodos que representan operaciones completas del sistema.

Esto mezcla las características de los empleados con la coordinación de los casos de uso.

**Decisión de diseño**

Se incorporan controladores de aplicación:

- `ControladorReservas`
- `ControladorSalidas`
- `ControladorReportes`

`ControladorReservas` coordinará la creación, modificación, confirmación y cancelación de reservas.

`ControladorSalidas` coordinará la programación de salidas, asignación de recursos y cierre operativo.

`ControladorReportes` coordinará la generación de información administrativa.

Los empleados continuarán representando a las personas responsables de las operaciones, pero no concentrarán la lógica de coordinación.

**Clases involucradas**

- `ControladorReservas`
- `ControladorSalidas`
- `ControladorReportes`
- `Vendedor`
- `Coordinador`
- `Administrador`
- `Reserva`
- `Salida`

**Beneficio obtenido**

Se separa la coordinación de los casos de uso de las entidades del dominio.

Esto mejora la organización del sistema y facilita su posterior conexión con una interfaz de usuario.

### 3.4. Bajo Acoplamiento (Low Coupling)

**Problema detectado**

Si cada controlador implementara directamente las reglas de cálculo de precios y devoluciones, dependería de numerosas clases y condiciones de negocio.

Cualquier modificación en estas reglas podría afectar varias partes del sistema.

**Decisión de diseño**

Se propone que `ControladorReservas` delegue el cálculo de precios a `CalculadoraPrecio`.

Para las devoluciones, se utilizará la abstracción `PoliticaCancelacion`, mediante la clase `Cancelacion`.

De esta manera, el controlador coordina las operaciones sin conocer todos los detalles de los algoritmos.

**Clases involucradas**

- `ControladorReservas`
- `CalculadoraPrecio`
- `Cancelacion`
- `PoliticaCancelacion`
- `Reserva`

**Beneficio obtenido**

Se reducen las dependencias entre las clases.

Los cambios en el cálculo de precios o en las políticas de cancelación quedan localizados en componentes específicos, disminuyendo el impacto sobre el resto del sistema.

---

## 4. Resumen de principios y patrones aplicados

| Tipo | Principio o patrón | Aplicación principal |
|---|---|---|
| SOLID | SRP | Separar `CalculadoraPrecio` de `Reserva` |
| SOLID | OCP | Extender políticas mediante `PoliticaCancelacion` |
| SOLID | DIP | Depender de la interfaz `PoliticaCancelacion` |
| GRASP | Experto en Información | `Salida` calcula ocupación y cupos |
| GRASP | Creador | `Cliente` crea y agrupa sus reservas |
| GRASP | Controlador | Incorporar controladores de casos de uso |
| GRASP | Bajo Acoplamiento | Delegar cálculos y utilizar abstracciones |

## 5. Modificaciones previstas para el modelo final

Como resultado de las decisiones anteriores, el diagrama de clases final incorporará:

**Nuevas clases:**

- `CalculadoraPrecio`
- `ControladorReservas`
- `ControladorSalidas`
- `ControladorReportes`
- `PoliticaCancelacionCliente`
- `PoliticaCancelacionAgencia`

**Nueva interfaz:**

- `PoliticaCancelacion`

**Cambios en clases existentes:**

- `Reserva` delegará el cálculo de importes.
- `Cancelacion` delegará el cálculo de devoluciones a una política.
- `Cliente` incorporará la creación de reservas.
- `Vendedor`, `Coordinador` y `Administrador` dejarán de concentrar la coordinación de los casos de uso.
- `Salida` conservará la responsabilidad de controlar la ocupación y los cupos.

## 6. Conclusión

La aplicación de los tres principios SOLID y los cuatro patrones GRASP permite mejorar el diseño inicial de Rumbo Sur mediante una distribución más clara de responsabilidades.

Las decisiones adoptadas favorecen la cohesión, reducen el acoplamiento y facilitan la incorporación de nuevas reglas de negocio.

Estas mejoras se reflejarán en el diagrama de clases final y se documentarán en la comparación entre ambos modelos.
