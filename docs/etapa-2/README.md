# Diagramas de la Etapa 2

Diagramas de secuencia actualizados para que coincidan con el modelo de clases final (SOLID y GRASP) y con la implementación en Java.

| Caso de uso | Imagen | Archivo editable |
|---|---|---|
| CU05 Crear reserva | `03_secuencia_crear_reserva_etapa2 (1).drawio (1).png` | `03_secuencia_crear_reserva_etapa2 (1).drawio` |
| CU12 Cancelar reserva | `04_secuencia_cancelar_reserva_etapa2_FINAL.drawio.png` | El mismo PNG: incluye el diagrama y se abre directamente en draw.io |

- **CU05 Crear reserva:** ControladorReservas coordina, Cliente crea la Reserva (Creador) y CalculadoraPrecio calcula el total y elige la mejor promoción (SRP).
- **CU12 Cancelar reserva:** ControladorReservas coordina y la devolución se delega a la interfaz PoliticaCancelacion, implementada por PoliticaCancelacionCliente y PoliticaCancelacionAgencia (OCP y DIP).

El diagrama de clases final de la Etapa 2 está en `docs/uml/06_clases_final_ETAPA.2.drawio`, con su imagen `06_clases_final_ETAPA.2.drawio.png`.

Los diagramas de secuencia de la Etapa 1 se conservan en `docs/uml` (`03_secuencia_crear_reserva` y `04_secuencia_cancelar_reserva`) como referencia del diseño previo a los patrones.
