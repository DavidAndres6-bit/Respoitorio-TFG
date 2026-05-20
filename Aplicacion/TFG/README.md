# ITV Digital CYL

**ITV Digital CYL** es una aplicación de escritorio diseñada específicamente para optimizar la gestión operativa y el proceso de inspección técnica de vehículos en las estaciones de Castilla y León.

Aplicación desarrollada como trabajo final de grado del ciclo de **Desarrollo de Aplicaciones Multiplataforma** utitlizando JavaFX para su desarrollo

## Organización y Distribución de Recursos

Para mantener la organización y separación de funciones en la aplicación, el codigo se ha organizado siguiendo la siguiente estructura:

├── 📁 bin/                  # Binarios nativos y librerías dinámicas (.dll)
├── 📁 lib/                  # Librerías externas del proyecto (JARs adicionales)
└── 📁 src/                  # Código fuente principal de la aplicación
    ├── 📁 clases/           # Lógica de persistencia de datos e interacción con la Base de Datos
    │   └── 📄 Connection    # Conexión remota al servidor MySQL en PythonAnywhere
    ├── 📁 controllers/      # Controladores de JavaFX que gestionan la lógica de las ventanas
    ├── 📁 img/              # Almacenamiento de recursos gráficos e imágenes de la interfaz
    ├── 📁 resources/        # Plantilla html para el PDF del informe de la inspección
    ├── 📁 utilities/        # Clases de soporte general (validaciones, aperturas y cierres de ventanas..)
    └── 📁 vistas/           # Archivos FXML que definen la estructura visual de las interfaces
    
    
 