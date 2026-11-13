# Plan de pruebas funcionales: Módulo de operaciones

**Responsable:** Germán Rocha.

**Fecha de ejecución:** 09/11/2026.

**Módulo:** Gestión de salidas programadas, ocupación, listas de pasajeros, inicio y finalización de salidas y control de cupo mínimo.

**Cómo se ejecutó:** los casos se probaron ejecutando `Principal.java` desde IntelliJ IDEA y utilizando el menú de `VistaCoordinador`. También se verificó el contenido del archivo exportado y la persistencia de los datos.

## Datos comunes

Se utilizaron los siguientes datos iniciales:

- **Tour:** Lagos de Bariloche.
- **Salida:** código 101.
- **Fecha de inicio:** 29/11/2026.
- **Fecha de finalización:** 01/12/2026.
- **Cupo mínimo:** 2 pasajeros.
- **Cupo máximo:** 20 pasajeros.
- **Guía asignado:** Martín López.
- **Vehículo asignado:** AB123CD, capacidad de 20 pasajeros.
- **Cliente:** Ana Pérez.
- **Vendedor:** Sofía Martínez.
- **Pasajeros:** Juan Pérez y Lucía Gómez.

Se creó una reserva con dos pasajeros, se registró un pago mediante transferencia y se confirmó correctamente.

## 1. Consultar salidas programadas

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| S1 | Seleccionar opción 1 del coordinador | Mostrar las salidas registradas | Se mostraron las salidas 101, 102 y 103 |
| S2 | Consultar salidas con guía y vehículo asignados | Mostrar las salidas disponibles | Se listaron correctamente las salidas registradas |

## 2. Emitir lista de pasajeros

Desde el menú, la opción "Emitir lista de pasajeros" genera la lista y la exporta al archivo en un solo paso.

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| P1 | Emitir lista de salida 101 sin confirmar | Rechazar la operación | `Error: La salida debe estar confirmada` |
| P2 | Emitir lista de salida 101 con reserva confirmada y condiciones cumplidas | Generar la lista y el archivo de texto | `Lista de pasajeros generada en el archivo pasajeros_salida_101.txt` |
| E1 | Exportar lista sin haberla generado | Rechazar la exportación | Ya no se puede hacer desde el menú; lo verifica la prueba unitaria de `ControladorSalidasExportacionTest` |
| E3 | Abrir el archivo exportado | Mostrar los datos de la salida y sus pasajeros | Se verificaron los datos de Juan Pérez y Lucía Gómez, documentos y contactos de emergencia |

## 3. Iniciar salida

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| I1 | Iniciar salida 101 con guía, vehículo y lista generada | Iniciar la salida correctamente | `Salida iniciada correctamente.` |

## 4. Finalizar salida y liquidar al guía

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| F1 | Finalizar salida 101 previamente iniciada | Finalizar la salida y liquidar al guía | `Salida finalizada.` y la liquidación: "Total a pagar: $135000.00" |

## 5. Control de cupo mínimo

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| C1 | Iniciar el programa con todas las salidas a más de 7 días | No cancelar ninguna salida | No se canceló ninguna salida y el menú abrió normalmente |
| C2 | Salida a 7 días o menos con ocupación inferior al mínimo | Cancelar la salida y devolver el 100 % | Verificado con las pruebas unitarias de `ControladorSalidasTest` y `ControladorSalidasOperacionTest` |

El control de cupo mínimo se hace automáticamente al iniciar el programa (ya no es una opción del menú).

## 6. Persistencia de datos

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| D1 | Salir mediante opción 0 y volver a ejecutar el programa | Recuperar los datos guardados | `Datos cargados del archivo.` y se conservaron las salidas |
| D2 | Reiniciar el programa con datos iniciales existentes | Evitar duplicar tours y salidas | Se conservaron las salidas 101, 102 y 103 |
| D3 | Reiniciar después de guardar la reserva y la salida finalizada | Recuperar reserva, pasajeros y estado finalizado | Pendiente de verificar tras reiniciar |

## Resultado general

Se realizaron **12 comprobaciones funcionales satisfactorias** del módulo de operaciones.

Las pruebas permitieron verificar el funcionamiento de la consulta de salidas con su ocupación, generación y exportación de listas de pasajeros, inicio y finalización de salidas, control de cupo mínimo y persistencia inicial.

**Pruebas pendientes:**

- Verificación de persistencia de la reserva y del estado finalizado después del último reinicio.

**Conclusión:** las funcionalidades probadas respondieron de acuerdo con los resultados esperados. Quedan pendientes las pruebas indicadas para completar la cobertura funcional del módulo.
