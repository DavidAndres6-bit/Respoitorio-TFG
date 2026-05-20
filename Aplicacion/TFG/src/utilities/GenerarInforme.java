package utilities;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

    // Funcion para generar el informe
    public void generarInformeITV(Inspeccion inspeccion, List<Defecto> defectos, String rutaSalida) {

        // Configuraciones necesarias de la libreria Thymeleaf

        // Localiza los archivos en el proyecto
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        // Accede a nuestra plantilla html del informe
        resolver.setPrefix("/resources/");
        
        // Definir la extension del tipo de archivo que tiene que buscar
        resolver.setSuffix(".html");

        // Indicar que el documento es un HTML estándar
        resolver.setTemplateMode("HTML");

        // Codificacion de caracteres
        resolver.setCharacterEncoding("UTF-8");

        // Creamos la instancia para usar la libreria para poder leer el html y escribir los datos en el
        TemplateEngine engine = new TemplateEngine();

        // Asignarle a la libreria las caracteristicas de busqueda que hemos definido
        engine.setTemplateResolver(resolver);

        // Crear el Contexto de datos, para pasarle los valores al html
        Context context = new Context();

        // Datos del apartado de identificacion
        context.setVariable("matricula", BufferInspeccion.getVehiculoActual().getMatricula());
        context.setVariable("marcaModelo", BufferInspeccion.getVehiculoActual().getMarca() + " " + BufferInspeccion.getVehiculoActual().getModelo());
        context.setVariable("tipoVehiculo", BufferInspeccion.getVehiculoActual().getTipoDgt());
        context.setVariable("kmActuales", inspeccion.getKmActuales());
        context.setVariable("numInforme", inspeccion.getId());

        // Convertir el formato de la fecha para mostrarlo mejor
        DateTimeFormatter formatoCyl = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        context.setVariable("fechaInspeccion", LocalDateTime.now().format(formatoCyl));

        // Convertir fecha de matriculacion para mostrarla
        String fechaMatriStr = "N/A";
        if (BufferInspeccion.getVehiculoActual().getFechaMatriculacion() != null) {
            fechaMatriStr = BufferInspeccion.getVehiculoActual().getFechaMatriculacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        context.setVariable("fechaMatriculacion", fechaMatriStr);

        // Para evitar errores con la base de datos con el formato de las fechas 
        String fechaProxStr = "---";

        if (inspeccion.getFechaProximaInspeccion() != null) {
            // Convertimos el java.sql.Date a LocalDate para mostrar en el formato que nos interesa
            java.time.LocalDate localDateProx = inspeccion.getFechaProximaInspeccion().toLocalDate();
            
            // Le aplicamos el formateador moderno igual que a la de matriculación
            fechaProxStr = localDateProx.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    
        // Pasamos la variable formateada de forma limpia
        context.setVariable("fechaProximaInspeccion", fechaProxStr);

        // Recoger el cliente
        Cliente cliente = BufferInspeccion.getClienteActual();

        // Recogemos los datos del cliente
        if (cliente != null) {
            String nombre = cliente.getNombre();
            String dni = cliente.getDni();

            // Pasar las variables
            context.setVariable("nombreCliente", nombre);
            context.setVariable("dniCliente", dni);
        }

        // Creamos un Set con los códigos de las unidades que tienen fallos
        Set<String> codigosConFallo = defectos.stream()
                .map(Defecto::getUnidad)
                .collect(Collectors.toSet());

        // Pasamos el Set al HTML
        context.setVariable("codigosConFallo", codigosConFallo);

        /*
         * Checks del Apartado de Pruebas
         */

        // Pasamos la lista de Defectos
        context.setVariable("defectos", defectos);

        // Pasamos los valores de las emisiones
        Map<String, String> emisiones = BufferInspeccion.getValoresEmisiones();

        context.setVariable("emisiones", emisiones);

        // Para las mediciones del apartado C
        context.setVariable("opacidad", emisiones.getOrDefault("opacidad", "N/A"));
        context.setVariable("resRalenti", emisiones.getOrDefault("coRalenti", "N/A"));
        context.setVariable("resRalentiAcelerado", emisiones.getOrDefault("coRalentiAcelerado", "N/A"));

        // Pasamos el Resultado Final y las observaciones
        context.setVariable("resultadoFinal", inspeccion.getResultadoInspeccion());

        // Calcula el precio segun la tarifa
        double precioITV = obtenerPrecioITV(BufferInspeccion.getVehiculoActual().getDistintivo());
        context.setVariable("tarifa", String.format("%.2f €", precioITV));

        // Datos para el nombre del archivo
        DateTimeFormatter formatoNombreArchivo = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fecha = LocalDateTime.now().format(formatoNombreArchivo);
        String matricula = BufferInspeccion.getVehiculoActual().getMatricula();

        // Nombre del archivo
        String nombreArchivo = "Informe_" + matricula + "_" + fecha + ".pdf";

        File archivoFinal = new File(rutaSalida, nombreArchivo);

        // Procesar la plantilla con el contexto de Thymeleaf
        String htmlProcesado = engine.process("Informe", context);

        try (OutputStream os = new FileOutputStream(archivoFinal)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            
            // Obtener la ruta base para las imagenes del documento
            String baseUri = "";
            try {
                if (getClass().getResource("/img/") != null) {
                    baseUri = getClass().getResource("/img/").toExternalForm();
                } else {
                    // Si no encuentra la carpeta de imagenes busca en la raiz
                    if (getClass().getResource("/") != null) {
                        baseUri = getClass().getResource("/").toExternalForm();
                    }
                }
            } catch (Exception eImg) {
                System.err.println("Error al cargar la ruta de los recursos: " + eImg.getMessage());
            }

            // Si baseUri esta vacio, se le pasa el directorio actual para que no falle
            builder.withHtmlContent(htmlProcesado, baseUri.isEmpty() ? "." : baseUri);
            builder.toStream(os);
            builder.run();

        } catch (Exception e) {
            System.err.println("Error crítico al renderizar el PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e); 
        }
    }

    // Funcion que calcula la tarifa segun la etiqueta traduciendo los nombres para MySQL
    private double obtenerPrecioITV(String etiqueta) {
        // Variable para almacenar el resultado
        double precioFinal = 53.00;

        if (etiqueta != null) {
            // Limpiamos el texto por si acaso
            String distintivoVehiculo = etiqueta.toUpperCase().trim();
            String nombreTarifa = "";

            // Hacemos la equivalencia estricta entre los nombres de ambas tablas
            switch (distintivoVehiculo) {
                case "DISTINTIVO B":
                    nombreTarifa = "Vehiculos con etiqueta B";
                    break;
                case "DISTINTIVO C":
                    nombreTarifa = "Vehiculos con etiqueta C";
                    break;
                case "ECO":
                    nombreTarifa = "Vehiculos Electricos e Hibridos";
                    break;
                case "CERO":
                    nombreTarifa = "Vehiculos con etiqueta 0";
                    break;
                case "SIN DISTINTIVO":
                    nombreTarifa = "Vehiculos sin etiqueta";
                    break;
                default:
                    nombreTarifa = "Vehiculos sin etiqueta"; // Fallback por si acaso
                    break;
            }

            // Instanciamos el DAO de tarifas
            TarifaDAO tarifaDAO = new TarifaDAO();

            // Asignamos el valor recuperado de la base de datos
            precioFinal = tarifaDAO.obtenerTarifaPorDistintivo(nombreTarifa);
        }
        return precioFinal;
    }
}