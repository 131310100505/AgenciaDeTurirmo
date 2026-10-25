# Evolución del modelo de clases — Rumbo Sur

## 1. Introducción

Durante la Etapa 1 del Trabajo Práctico Obligatorio de Programación Orientada a Objetos se elaboraron dos versiones del modelo de clases del sistema Rumbo Sur.

El **modelo inicial** representa las entidades principales y las relaciones necesarias para satisfacer los requisitos funcionales.

El **modelo final** incorpora mejoras de diseño basadas en tres principios SOLID y cuatro patrones GRASP, con el objetivo de lograr una mejor distribución de responsabilidades, menor acoplamiento y mayor facilidad de mantenimiento.

Este documento describe las principales modificaciones realizadas y sus beneficios.

## 2. Modificaciones realizadas

### Cambio 1 — Separación del cálculo de precios

**Modelo inicial:** la clase `Reserva` incluía el método `calcularTotal()`, por lo que concentraba la administración de la reserva y el cálculo de importes.

**Modelo final:** se incorpora `CalculadoraPrecio`, responsable de calcular tarifas, descuentos, servicios adicionales y el total de la reserva.

**Principio aplicado:** SOLID — Responsabilidad Única (SRP).

**Beneficio:** los cambios en las reglas de precios no requieren modificar directamente la lógica principal de gestión de reservas.

### Cambio 2 — Incorporación de políticas de cancelación

**Modelo inicial:** la clase `Cancelacion` calculaba directamente las devoluciones.

**Modelo final:** se incorpora la interfaz `PoliticaCancelacion`, con las implementaciones `PoliticaCancelacionCliente` y `PoliticaCancelacionAgencia`.

La clase `Cancelacion` delega el cálculo de devolución a la política correspondiente.

**Principios aplicados:** SOLID — Abierto/Cerrado (OCP) e Inversión de Dependencias (DIP).

**Beneficio:** es posible agregar nuevas políticas de cancelación sin modificar el funcionamiento principal de la clase `Cancelacion`. Además, se reduce la dependencia de implementaciones concretas.

### Cambio 3 — Incorporación de controladores

**Modelo inicial:** las clases `Vendedor`, `Coordinador` y `Administrador` contenían métodos que coordinaban operaciones completas del sistema.

**Modelo final:** se incorporan `ControladorReservas`, `ControladorSalidas` y `ControladorReportes`.

Estos controladores coordinan las operaciones de la aplicación y delegan las reglas específicas en las entidades y servicios correspondientes.

**Patrón aplicado:** GRASP — Controlador (Controller).

**Beneficio:** se separa la coordinación de los casos de uso de las clases que representan empleados, mejorando la cohesión y la organización del sistema.

### Cambio 4 — Creación de reservas

**Modelo inicial:** la clase `Vendedor` tenía la operación `crearReserva()`.

**Modelo final:** `ControladorReservas` coordina el caso de uso y la creación del objeto se delega a `Cliente`, mediante el método `crearReserva()`.

**Patrón aplicado:** GRASP — Creador (Creator).

**Beneficio:** la creación queda vinculada a una clase que agrupa y mantiene una relación directa con las reservas, mientras el controlador coordina las validaciones y el proceso.

### Cambio 5 — Control de cupos

**Modelo inicial:** la clase `Salida` ya tenía las operaciones `calcularOcupacion()` y `verificarCupo()`.

**Modelo final:** se conserva esta responsabilidad en `Salida`, por ser la entidad que conoce las reservas asociadas y el cupo máximo.

**Patrón aplicado:** GRASP — Experto en Información (Information Expert).

**Beneficio:** el control de ocupación permanece en la clase que dispone de la información necesaria, evitando duplicar las validaciones en otras partes del sistema.

**Aclaración:** este punto representa una decisión de diseño que se mantiene entre ambos modelos, no una clase o método nuevo.

### Cambio 6 — Reducción del acoplamiento

**Modelo inicial:** distintas operaciones del sistema concentraban validaciones y cálculos en clases del dominio.

**Modelo final:** los controladores delegan el cálculo de precios a `CalculadoraPrecio` y las devoluciones se resuelven mediante la abstracción `PoliticaCancelacion`.

**Patrón aplicado:** GRASP — Bajo Acoplamiento (Low Coupling).

**Beneficio:** los cambios en las reglas de negocio afectan a menos clases y se facilita la prueba de los componentes por separado.

## 3. Resumen comparativo

| Aspecto | Modelo inicial | Modelo final |
|---|---|---|
| Cálculo de precios | `Reserva` | `CalculadoraPrecio` |
| Cálculo de devoluciones | `Cancelacion` | `PoliticaCancelacion` y sus implementaciones |
| Creación de reservas | `Vendedor` | `ControladorReservas` coordina y `Cliente` crea |
| Coordinación de operaciones | Clases de empleados | Controladores específicos |
| Control de cupos | `Salida` | Se mantiene en `Salida` |
| Dependencias | Responsabilidades más concentradas | Delegación y abstracciones |
| Extensibilidad | Mayor necesidad de modificar clases existentes | Posibilidad de incorporar nuevas políticas |

## 4. Elementos que se mantienen

No todas las clases fueron modificadas. El modelo final conserva las principales entidades del negocio y sus relaciones:

- Tours y especializaciones: `Tour`, `TourAventura`, `TourCultural` y `TourNaturaleza`.
- Itinerarios, actividades, proveedores y tarifas.
- Salidas, guías y vehículos.
- Clientes, pasajeros y reservas.
- Servicios adicionales y promociones.
- Pagos, ajustes y cancelaciones.
- Liquidaciones y enumeraciones del sistema.

También se mantiene la jerarquía de herencia de `Empleado` y sus especializaciones.

Estas estructuras continúan siendo necesarias para representar los requisitos funcionales del sistema.

## 5. Correspondencia con SOLID y GRASP

| Principio o patrón | Aplicación en el modelo final |
|---|---|
| SRP | Separación de `CalculadoraPrecio` |
| OCP | Políticas de cancelación extensibles |
| DIP | Dependencia de `PoliticaCancelacion` |
| Information Expert | Control de ocupación en `Salida` |
| Creator | Creación de reservas en `Cliente` |
| Controller | Controladores de reservas, salidas y reportes |
| Low Coupling | Delegación de cálculos y uso de abstracciones |

## 6. Conclusión

El modelo final constituye una evolución del modelo inicial, manteniendo las funcionalidades requeridas y mejorando la distribución de responsabilidades.

La incorporación de clases especializadas, interfaces y controladores permite reducir el acoplamiento, mejorar la cohesión y facilitar futuras modificaciones.

Las decisiones realizadas se reflejarán en el diagrama de clases final y servirán como base para la implementación posterior del sistema en Java.
