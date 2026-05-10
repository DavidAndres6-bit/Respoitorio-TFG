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

    // Variable para guardar los checks marcados en la inspeccion para el informe
    private static Map<String, Boolean> checksMarcados = new HashMap<>();
    
    // Variable para guardar los valores de las emisiones de la inspeccion
    private static Map<String,String> valoresEmisiones = new HashMap<>();

    // Array para guardar las observaciones a lo largo de la inspeccion
    private static String[] observaciones = new String[8];

    // Variable para guardar el cliente que realiza la inspeccion
    private static Cliente clienteActual;

    //Funcion para crear una nueva inspeccion para guardar los datos
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

    //Getter y setter de los checks marcados
    public static Map<String, Boolean> getChecksMarcados() {
        return checksMarcados;
    }

    public static void setChecksMarcados(Map<String, Boolean> checksMarcados) {
        BufferInspeccion.checksMarcados = checksMarcados;
    }

    //Getter y Setter de los valores de emisiones
    public static Map<String, String> getValoresEmisiones() {
        return valoresEmisiones;
    }

    public static void setValoresEmisiones(Map<String, String> valoresEmisiones) {
        BufferInspeccion.valoresEmisiones = valoresEmisiones;
    }

    // Guardar las observaciones en su posicion correspondiente
    public static void guardarObservacion(int posicion, String texto) {
        if(posicion >= 0 && posicion < observaciones.length) {
            observaciones[posicion] = texto;
        }
    }

    // Recuperar las observaciones de una posicion concreta
    public static String getObservacionPosicion(int posicion) {
        String obs = "";
        if (posicion >= 0 && posicion < observaciones.length) {
            obs= observaciones[posicion];
        }

        return obs;
    }
    
    // Metodo para juntar las observaciones recogidas durante la inspeccion
    public static String juntarObservaciones() {
        String resultado = ""; 
        
        if (observaciones != null) {
            for (int i = 0; i < observaciones.length; i++) {
                if (observaciones[i] != null && !observaciones[i].trim().isEmpty()) {
                    // Vamos sumando los textos con un salto de línea
                    resultado += observaciones[i] + "<br/>";
                }
            }
        }
        return resultado.trim(); // quitamos el ultimo /n
    }

    // Getter y Setter de cliente
    public static Cliente getClienteActual() {
        return clienteActual;
    }

    public static void setClienteActual(Cliente cliente) {
        clienteActual = cliente;
    }

    //Funcion para limpiar el buffer de la inspeccion cuando esta termine
    public static void limpiarBuffer() {
        inspeccionActual = null;
        vehiculoActual = null;
        clienteActual = null;
        
        checksMarcados.clear(); // Vaciamos el mapa
        listaDefectos.clear(); // Vaciamos la lista de defectos
        valoresEmisiones.clear(); // Vaciamos los valores de las emisiones
    
        // Limpiar el array de observaciones
        if (observaciones != null) {
            for (int i = 0; i < observaciones.length; i++) {
                observaciones[i] = null;
            }
        }
    }
}
