package utilities;

import clases.POJOS.Cliente;
import clases.POJOS.Defecto;
import clases.POJOS.Inspeccion;
import clases.POJOS.Vehiculo;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BufferInspeccion {

    // Variable para guardar la inspeccion
    private static Inspeccion inspeccionActual;

    // Variable para guardar el vehiculo
    private static Vehiculo vehiculoActual;

    // Lista con los defectos del proceso de inspeccion
    private static List<Defecto> listaDefectos = new ArrayList<>();

    // Variable para guardar los valores de las emisiones de la inspeccion
    private static Map<String, String> valoresEmisiones = new HashMap<>();

    // Variable para guardar el cliente que realiza la inspeccion
    private static Cliente clienteActual;

    // Funcion para crear una nueva inspeccion para guardar los datos
    public static Inspeccion getInspeccionActual() {
        if (inspeccionActual == null) {
            inspeccionActual = new Inspeccion();
        }
        return inspeccionActual;
    }

    public static Vehiculo getVehiculoActual() {
        return vehiculoActual;
    }

    public static void setVehiculoActual(Vehiculo vehiculo) {
        vehiculoActual = vehiculo;
    }

    public static List<Defecto> getDefectosActuales() {
        return listaDefectos;
    }

    // Getter y Setter de los valores de emisiones
    public static Map<String, String> getValoresEmisiones() {
        return valoresEmisiones;
    }

    public static void setValoresEmisiones(Map<String, String> valoresEmisiones) {
        BufferInspeccion.valoresEmisiones = valoresEmisiones;
    }

    // Getter y Setter de cliente
    public static Cliente getClienteActual() {
        return clienteActual;
    }

    public static void setClienteActual(Cliente cliente) {
        clienteActual = cliente;
    }

    public static List<Defecto> getDefectos() {
        return listaDefectos;
    }

    // Metodo para guardar los defectos
    public static void aniadirDefecto(String unidad, String descripcion, String calificacion) {
        Defecto d = new Defecto(unidad, descripcion, calificacion);
        listaDefectos.add(d);
    }

    // Metodo para borrar un defecto de la lista
    public static void borrarDefecto(String codigo) {
        boolean encontrado = false;
        int i = 0;

        // Recorremos la lista buscando el defecto
        while (i < listaDefectos.size() && !encontrado) {
            if (listaDefectos.get(i).getUnidad().equalsIgnoreCase(codigo)) {
                listaDefectos.remove(i);
                encontrado = true;
            }
            i++;
        }
    }

    // Funcion para limpiar el buffer de la inspeccion cuando esta termine
    public static void limpiarBuffer() {
        inspeccionActual = null;
        vehiculoActual = null;
        clienteActual = null;

        listaDefectos.clear(); // Vaciamos la lista de defectos
        valoresEmisiones.clear(); // Vaciamos los valores de las emisiones
    }
}
