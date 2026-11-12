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

## 2. Consultar ocupación

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| O1 | Consultar salida 101 sin reservas | Ocupación 0 | Ocupación actual: 0 |
| O2 | Consultar salida 101 con reserva de 2 pasajeros | Ocupación 2 | Ocupación actual: 2 |

## 3. Generar lista de pasajeros

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| P1 | Generar lista de salida 101 sin confirmar | Rechazar la operación | `Error: La salida debe estar confirmada` |
| P2 | Generar lista de salida 101 con reserva confirmada y condiciones cumplidas | Generar lista correctamente | `Lista de pasajeros generada.` |

## 4. Exportar lista de pasajeros

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| E1 | Exportar lista sin haberla generado | Rechazar la exportación | `Error: Primero debe generarse la lista de pasajeros` |
| E2 | Exportar lista generada de salida 101 | Crear archivo de texto | `Archivo generado: pasajeros_salida_101.txt` |
| E3 | Abrir el archivo exportado | Mostrar los datos de la salida y sus pasajeros | Se verificaron los datos de Juan Pérez y Lucía Gómez, documentos y contactos de emergencia |

## 5. Iniciar salida

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| I1 | Iniciar salida 101 con guía, vehículo y lista generada | Iniciar la salida correctamente | `Salida iniciada correctamente.` |

## 6. Finalizar salida

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| F1 | Finalizar salida 101 previamente iniciada | Finalizar la salida correctamente | `Salida finalizada correctamente.` |

## 7. Controlar cupo mínimo

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| C1 | Ejecutar control de cupo mínimo con salidas fuera del plazo de cancelación | No cancelar ninguna salida | `No hay salidas para cancelar por cupo mínimo.` |
| C2 | Ejecutar control dentro del plazo establecido con ocupación inferior al mínimo | Cancelar la salida correspondiente | Pendiente de ejecutar |

## 8. Persistencia de datos

| **Caso** | **Entrada** | **Resultado esperado** | **Resultado obtenido** |
|---|---|---|---|
| D1 | Salir mediante opción 0 y volver a ejecutar el programa | Recuperar los datos guardados | `Datos cargados del archivo.` y se conservaron las salidas |
| D2 | Reiniciar el programa con datos iniciales existentes | Evitar duplicar tours y salidas | Se conservaron las salidas 101, 102 y 103 |
| D3 | Reiniciar después de guardar la reserva y la salida finalizada | Recuperar reserva, pasajeros y estado finalizado | Pendiente de verificar tras reiniciar |

## Resultado general

Se realizaron **13 comprobaciones funcionales satisfactorias** del módulo de operaciones.

Las pruebas permitieron verificar el funcionamiento de la consulta de salidas, ocupación, generación y exportación de listas de pasajeros, inicio y finalización de salidas, control de cupo mínimo fuera del plazo y persistencia inicial.

**Pruebas pendientes:**

- Cancelación efectiva de una salida por incumplimiento del cupo mínimo.
- Verificación de persistencia de la reserva y del estado finalizado después del último reinicio.

**Conclusión:** las funcionalidades probadas respondieron de acuerdo con los resultados esperados. Quedan pendientes las pruebas indicadas para completar la cobertura funcional del módulo.
