package ar.edu.uade.rumbosur.vista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Lectura de datos por teclado para todos los menús de consola.
 * Si el usuario escribe algo inválido, se le avisa y se le vuelve a preguntar,
 * así el programa nunca termina por un error de tipeo.
 *
 * La pueden usar las tres vistas (Vendedor, Coordinador y Administrador).
 */
public class EntradaConsola {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Scanner teclado;

    public EntradaConsola(Scanner teclado) {
        this.teclado = teclado;
    }

    /** Lee un número entero. Captura InputMismatchException (material "Excepciones en Java"). */
    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = teclado.nextInt();
                teclado.nextLine(); // descarta el Enter que queda pendiente
                return valor;
            } catch (InputMismatchException e) {
                teclado.nextLine(); // descarta lo que se escribió mal
                System.out.println("  Error: debe ingresar un número entero.");
            }
        }
    }

    /** Lee un entero dentro de un rango; sirve para elegir opciones de un menú. */
    public int leerOpcion(String mensaje, int minimo, int maximo) {
        int opcion = leerEntero(mensaje);
        while (opcion < minimo || opcion > maximo) {
            System.out.println("  Error: elija una opción entre " + minimo + " y " + maximo + ".");
            opcion = leerEntero(mensaje);
        }
        return opcion;
    }

    /** Lee un importe. Acepta coma o punto como separador decimal. */
    public double leerImporte(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (valor > 0) {
                    return valor;
                }
                System.out.println("  Error: el importe debe ser mayor que cero.");
            } catch (NumberFormatException e) {
                System.out.println("  Error: debe ingresar un número, por ejemplo 15000 o 15000,50.");
            }
        }
    }

    /** Lee un texto que no puede quedar vacío. */
    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        String texto = teclado.nextLine().trim();
        while (texto.isEmpty()) {
            System.out.print("  Error: no puede quedar vacío. " + mensaje);
            texto = teclado.nextLine().trim();
        }
        return texto;
    }

    /** Lee un texto que puede quedar vacío (datos opcionales). */
    public String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim();
    }

    /** Lee una fecha con formato dd/mm/aaaa. */
    public LocalDate leerFecha(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return LocalDate.parse(texto, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("  Error: la fecha debe tener el formato dd/mm/aaaa, por ejemplo 25/03/1990.");
            }
        }
    }

    /** Pregunta por sí o por no. */
    public boolean confirmar(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje + " (s/n): ").toLowerCase();
            if (respuesta.equals("s")) {
                return true;
            }
            if (respuesta.equals("n")) {
                return false;
            }
            System.out.println("  Error: responda s o n.");
        }
    }

    /**
     * El programa usa un único Scanner sobre System.in: si se crean dos y se cierra uno,
     * el otro deja de leer. Las vistas que trabajan con Scanner directamente usan este.
     */
    public Scanner getScanner() {
        return teclado;
    }

    public void cerrar() {
        teclado.close();
    }
}
