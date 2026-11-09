package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorReservas;
import ar.edu.uade.rumbosur.controlador.ControladorSalidas;
import ar.edu.uade.rumbosur.excepciones.CupoInsuficienteException;
import ar.edu.uade.rumbosur.excepciones.PasajeroNoHabilitadoException;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Vendedor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana Swing para crear una reserva (CU05), como alternativa al menú de consola.
 *
 * Componentes del material "Java Swing": JFrame, JPanel, JLabel, JTextField, JButton,
 * GridLayout y un ActionListener escrito con lambda. Nombres con prefijo (frm, pnl,
 * lbl, txt, btn, cmb) como recomienda el material.
 *
 * Es otra Vista del MVC: usa el mismo ControladorReservas que el menú de consola,
 * sin repetir ninguna regla de negocio.
 */
public class VentanaReserva {

    private ControladorReservas controladorReservas;
    private List<Vendedor> vendedores;
    private List<Salida> salidas;

    private JFrame frmReserva;
    private JComboBox<String> cmbVendedor;
    private JComboBox<String> cmbSalida;
    private JTextField txtDocumentoCliente;
    private JTextField txtDocumentosPasajeros;
    private JLabel lblResultado;

    public VentanaReserva(ControladorReservas controladorReservas, ControladorSalidas controladorSalidas,
                          List<Vendedor> vendedores) {
        this.controladorReservas = controladorReservas;
        this.vendedores = new ArrayList<>(vendedores);
        this.salidas = controladorSalidas.listarSalidas();
        construir();
    }

    /** Buena práctica del material: crear y mostrar la ventana en el Event Dispatch Thread. */
    public static void abrir(ControladorReservas controladorReservas, ControladorSalidas controladorSalidas,
                             List<Vendedor> vendedores) {
        SwingUtilities.invokeLater(() ->
                new VentanaReserva(controladorReservas, controladorSalidas, vendedores).mostrar());
    }

    private void construir() {
        frmReserva = new JFrame("Rumbo Sur - Nueva reserva");
        frmReserva.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // cierra solo esta ventana

        cmbVendedor = new JComboBox<>();
        for (Vendedor vendedor : vendedores) {
            cmbVendedor.addItem(vendedor.getNombre() + " (legajo " + vendedor.getLegajo() + ")");
        }
        cmbSalida = new JComboBox<>();
        for (Salida salida : salidas) {
            cmbSalida.addItem(salida.getCodigo() + " - " + salida.getTour().getNombre() + " - "
                    + salida.getFechaInicio() + " (" + salida.calcularOcupacion() + "/" + salida.getCupoMaximo() + ")");
        }
        txtDocumentoCliente = new JTextField(20);
        txtDocumentosPasajeros = new JTextField(20);
        JButton btnCrear = new JButton("Crear reserva");
        lblResultado = new JLabel(" ");

        JPanel pnlDatos = new JPanel(new GridLayout(0, 2, 8, 8));
        pnlDatos.add(new JLabel("Vendedor:"));
        pnlDatos.add(cmbVendedor);
        pnlDatos.add(new JLabel("Salida:"));
        pnlDatos.add(cmbSalida);
        pnlDatos.add(new JLabel("Documento del cliente:"));
        pnlDatos.add(txtDocumentoCliente);
        pnlDatos.add(new JLabel("Documentos de los pasajeros (separados por coma):"));
        pnlDatos.add(txtDocumentosPasajeros);

        JPanel pnlAcciones = new JPanel(new BorderLayout(8, 8));
        pnlAcciones.add(btnCrear, BorderLayout.NORTH);
        pnlAcciones.add(lblResultado, BorderLayout.CENTER);

        JPanel pnlPrincipal = new JPanel(new BorderLayout(10, 10));
        pnlPrincipal.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        pnlPrincipal.add(pnlDatos, BorderLayout.CENTER);
        pnlPrincipal.add(pnlAcciones, BorderLayout.SOUTH);
        frmReserva.add(pnlPrincipal);

        // Manejo de eventos: el botón es la fuente, la lambda es el oyente.
        btnCrear.addActionListener(e -> crearReserva());
    }

    private void mostrar() {
        frmReserva.pack();
        frmReserva.setLocationRelativeTo(null);
        frmReserva.setVisible(true);
    }

    /** Lo que pasa al tocar el botón: arma los datos y llama al controlador. */
    private void crearReserva() {
        if (cmbVendedor.getSelectedIndex() < 0 || cmbSalida.getSelectedIndex() < 0) {
            mostrarError("Elija un vendedor y una salida.");
            return;
        }
        Cliente cliente = controladorReservas.buscarCliente(txtDocumentoCliente.getText().trim());
        if (cliente == null) {
            mostrarError("No existe un cliente con ese documento.");
            return;
        }
        List<Pasajero> pasajeros = new ArrayList<>();
        for (String documento : txtDocumentosPasajeros.getText().split(",")) {
            if (documento.isBlank()) {
                continue;
            }
            Pasajero pasajero = controladorReservas.buscarPasajero(documento.trim());
            if (pasajero == null) {
                mostrarError("No existe el pasajero con documento " + documento.trim() + ".");
                return;
            }
            pasajeros.add(pasajero);
        }
        Vendedor vendedor = vendedores.get(cmbVendedor.getSelectedIndex());
        Salida salida = salidas.get(cmbSalida.getSelectedIndex());
        try {
            Reserva reserva = controladorReservas.solicitarCrearReserva(cliente, salida, vendedor, pasajeros);
            lblResultado.setForeground(new Color(0, 110, 50));
            lblResultado.setText(String.format("Reserva %d creada - Total $%.2f - %s",
                    reserva.getNumero(), reserva.getTotal(), reserva.getEstado()));
        } catch (CupoInsuficienteException | PasajeroNoHabilitadoException e) {
            mostrarError(e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        lblResultado.setForeground(Color.RED);
        lblResultado.setText(mensaje);
    }
}
