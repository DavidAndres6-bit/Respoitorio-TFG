package utilities;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import java.time.format.DateTimeFormatter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import clases.DAOS.TarifaDAO;
import clases.POJOS.Cliente;
import clases.POJOS.Defecto;
import clases.POJOS.Inspeccion;

public class GenerarInforme {

    //Funcion para generar el informe
    public void generarInformeITV(Inspeccion inspeccion, List<Defecto> defectos, String rutaSalida) {

        //Configuraciones necesarias de la libreria Thymeleaf

        //Localiza los archivos en el proyecto
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        //Accede a nuestra plantilla html del informe
        resolver.setPrefix("resources/");

        //Definir la extension del tipo de archivo que tiene que buscar
        resolver.setSuffix(".html");

        //Indicar que el documento es un HTML estándar
        resolver.setTemplateMode("HTML");

        //Codificacion de caracteres    
        resolver.setCharacterEncoding("UTF-8");

        //Creamos la instancia para usar la libreria para poder leer el html y escribir los datos en el
        TemplateEngine engine = new TemplateEngine();

        //Asignarle a la libreria las caracteristicas de busqueda que hemos definido
        engine.setTemplateResolver(resolver);


        // Crear el Contexto de datos, para pasarle los valores al html
        Context context = new Context();


        //Datos del apartado de identificacion
        context.setVariable("matricula", BufferInspeccion.getVehiculoActual().getMatricula());
        context.setVariable("marcaModelo", BufferInspeccion.getVehiculoActual().getMarca() + " " + BufferInspeccion.getVehiculoActual().getModelo());
        context.setVariable("tipoVehiculo", BufferInspeccion.getVehiculoActual().getTipoDgt());
        context.setVariable("kmActuales", inspeccion.getKmActuales()); 
        context.setVariable("numInforme", inspeccion.getId());

        // Convertir el formato de la fecha para mostrarlo mejor
        DateTimeFormatter formatoCyl = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        context.setVariable("fechaInspeccion", LocalDateTime.now().format(formatoCyl));

        // Convertir fecha de matriculacion para mostrarla

        // Recuperamos la fecha de matriculacion del vehículo
        String fechaMatriStr = "N/A";
        if (BufferInspeccion.getVehiculoActual().getFechaMatriculacion() != null) {
            fechaMatriStr = BufferInspeccion.getVehiculoActual().getFechaMatriculacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        
        context.setVariable("fechaMatriculacion", fechaMatriStr);

        context.setVariable("fechaProximaInspeccion", inspeccion.getFechaProximaInspeccion());
        
        // Recoger el cliente
        Cliente cliente = BufferInspeccion.getClienteActual();

        if(cliente != null){
            String nombre = cliente.getNombre();
            String dni = cliente.getDni();

            // Pasar las variables
            context.setVariable("nombreCliente",nombre);
            context.setVariable("dniCliente", dni);
        }

        
        /*
        *   Checks del Apartado de Pruebas
        */

        //Recuperar los checks marcados
        Map<String, Boolean> checks = BufferInspeccion.getChecksMarcados();
    
        //Pasamos el mapa de checks
        context.setVariable("detalles", checks);

        //Pasamos la lista de Defectos
        context.setVariable("defectos", defectos);

       //Pasamos los valores de las emisiones
        Map<String, String> emisiones = BufferInspeccion.getValoresEmisiones();
        
        context.setVariable("emisiones", emisiones);

        //Para las mediciones del apartado C
        context.setVariable("opacidad", emisiones.getOrDefault("opacidad", "N/A"));
        context.setVariable("resRalenti", emisiones.getOrDefault("coRalenti", "N/A"));
        context.setVariable("resRalentiAcelerado", emisiones.getOrDefault("coRalentiAcelerado", "N/A"));

        //Pasamos el Resultado Final y las observaciones
        context.setVariable("resultadoFinal", inspeccion.getResultadoInspeccion());
        context.setVariable("observaciones", inspeccion.getObservaciones());

        //Calcula el precio segun la tarifa
        double precioITV = obtenerPrecioITV(BufferInspeccion.getVehiculoActual().getTipoDgt());
        context.setVariable("tarifa", precioITV + " €");

        // Datos para el nombre del archivo
        DateTimeFormatter formatoNombreArchivo = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fecha = LocalDateTime.now().format(formatoNombreArchivo);
        String matricula = BufferInspeccion.getVehiculoActual().getMatricula();

        // Nombre del archivo 
        String nombreArchivo = "Informe_" + matricula + "_" + fecha + ".pdf";

        File archivoFinal = new File(rutaSalida, nombreArchivo);

        //Transformamos el HTML a PDF
        String htmlProcesado = engine.process("informe", context);

            try (OutputStream os = new FileOutputStream(archivoFinal)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                //Ruta a la carpeta para las imagenes
                String baseUri = new File("src/img/").toURI().toString(); 
    
                builder.withHtmlContent(htmlProcesado, baseUri);
                builder.toStream(os);
                builder.run();
        
            } catch (Exception e) {
                System.err.println("Error al generar el PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }

    //Funcion que calcula la tarifa segun la etiqueta
    private double obtenerPrecioITV(String etiqueta){
        
       //Instanciamos el DAO de tarifas
       TarifaDAO tarifaDAO = new TarifaDAO();
       
       //Recogemos el precio de la tarifa
       double precio = tarifaDAO.obtenerTarifaPorDistintivo(etiqueta);
       
       //Devolvemos el precio
       return precio;
    }
}