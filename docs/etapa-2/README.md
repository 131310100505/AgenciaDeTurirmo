# Diagramas de la Etapa 2

Diagramas de secuencia actualizados para que coincidan con el modelo de clases final (SOLID y GRASP) y con la implementación en Java.

- `03_secuencia_crear_reserva_etapa2.jpg`: CU05 Crear reserva. ControladorReservas coordina, Cliente crea la Reserva (Creador) y CalculadoraPrecio calcula el total y elige la mejor promoción (SRP).
- `04_secuencia_cancelar_reserva_etapa2.jpg`: CU12 Cancelar reserva. ControladorReservas coordina y la devolución se delega a la interfaz PoliticaCancelacion, implementada por PoliticaCancelacionCliente y PoliticaCancelacionAgencia (OCP y DIP).

Los diagramas de secuencia de la Etapa 1 se conservan en la carpeta anterior como referencia del diseño previo a los patrones.
