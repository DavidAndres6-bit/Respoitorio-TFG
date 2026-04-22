package utilities;

import clases.POJOS.Inspeccion;
import clases.POJOS.Vehiculo;

public class BufferInspeccion {
    
    //Variable para guardar la inspeccion
    private static Inspeccion inspeccionActual;

    //Variable para guardar el vehiculo
    private static Vehiculo vehiculoActual;

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
    
    //Funcion para limpiar el buffer de la inspeccion cuando esta termine
    public static void limpiarBuffer() {
        inspeccionActual = null;
    }
}