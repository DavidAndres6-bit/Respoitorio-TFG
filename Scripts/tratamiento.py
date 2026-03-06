import pandas as pd      #Libreria utilizada para el analisis de datos
import zipfile          #Libreria que permite leer archivos zip y acceder a su contenido
import os               # Trabaja con rutas y archivos del sistema operativo


# Configurar la ruta en la que se nos descargo el zip del script de descarga
# Cambiar los parametros para que coincida con la ruta
RUTA_CARPETA = os.path.join("C:\\Users", "david", "Desktop", "DGT", "zips")

#Configurar ruta en la se generaran los csv con los datos filtrados
RUTA_SALIDA = os.path.join("C:\\Users", "david", "Desktop", "DGT", "datos_CyL_limpios.csv")

# Nombre del archivo para que el script pueda acceder a el
nombre_archivo = "parque_vehiculos_202602_PROVINCIA.zip"

# Ruta completa al archivo AZIP
RUTA_ZIP_COMPLETA = os.path.join(RUTA_CARPETA, nombre_archivo)


# Array con los codigos de las provincias de Castilla y León
# Lo utilizaremos para quedarnos con los txt que nos interesan
# 05 -> Ávila
# 09 -> Burgos
# 24 -> León
# 34 -> Palencia
# 37 -> Salamanca
# 40 -> Segovia
# 42 -> Soria
# 47 -> Valladolid
# 49 -> Zamora
CODIGOS_PROVINCIAS = ['05', '09', '24', '34', '37', '40', '42', '47', '49']

# Definir las columnas que necesitamos 
columnas = ['PROVINCIA','MARCA','MODELO','FECHA_MATR','PROCEDENCIA','NUEVO_USADO','TIPO_DGT','TIPO_DISTINTIVO','EMISIONES_CO2','PLAZAS']


# Función para obtener solo los nombres de los archivos que nos interesan 
def obtener_listado_archivos(z, codigos_provincias):

    # Obtener el nombre de los ficheros
    nombre_ficheros = z.namelist()
    
    # Recorrer los nombres de los archivos
    # El [:2] coge los dos primeros caracteres del nombre (los digitos que nos interesan)
    archivos_cyl = []
    for nom in nombre_ficheros:
        if nom[:2] in codigos_provincias:
            archivos_cyl.append(nom)
            
    return archivos_cyl


# Función para abrir y filtrar un archivo TXT individual
def procesar_archivo_txt(z, archivoTxt, columnas_interes):
    print('Tratando el archivo ', archivoTxt)
    
    # Abrir el archivo Txt
    with z.open(archivoTxt) as arc:
        # Leer el txt indicando el separador para poder quedarnos con los datos
        df_provincia = pd.read_csv(arc, sep="|", encoding="latin-1", low_memory=False)
        
        # Creamos una lista vacía para almacenar solo las columnas que necesitamos
        df_filtrado = []

        # Recorremos la lista de columnas que definiste arriba
        for c in columnas_interes:
            # Comprobamos si la columna existe en el archivo original (df_provincia)
            if c in df_provincia.columns:
                # Si existe, la añadimos a nuestra lista de filtrado
                df_filtrado.append(df_provincia[c])

        # Convertimos la lista de columnas de vuelta a un DataFrame (Objeto de tipo tabla)
        df_final_provincia = pd.concat(df_filtrado, axis=1)
        
    return df_final_provincia

# Funcion que recorre el archivo zip indicado y se queda con los archivos de las provincias
# que nos interesan filtrando por su codigo con el array
def extraer_datos_provincias(ruta_zip, codigos_provincias):

    # Lista para almacenar los DataFrames de cada provincia
    lista_dfs = []

    # Abrir el archivo para ver el contenido
    # whit -> Abre el archivo
    # zipFile.ZipFile -> Crea un "objeto" de tipo zip para manejarlo
    # r -> Indica que queremos hacer con el zip, en este caso solo lectura para quedarnos con los txt que nos interesan 
    # as z -> Para identificar ese "objeto"
    with zipfile.ZipFile(ruta_zip, 'r') as z:

        # Llamada a la función para obtener los nombres filtrados
        archivos_cyl = obtener_listado_archivos(z, codigos_provincias)

        # Imprimir los archivos que hemos encontrado
        print('Procesando los archivos ', archivos_cyl, '.....')    

        # Bucle para recorrer los archivos
        for archivoTxt in archivos_cyl:
            
            # Llamada a la función que trata cada TXT
            df_resultado = procesar_archivo_txt(z, archivoTxt, columnas)
            
            # Añadir el resultado a nuestra lista para unirlos luego
            lista_dfs.append(df_resultado)


        # Unir todos los DataFrame recogidos y filtrados del txt si los hay
        if lista_dfs:
            print("Uniendo todos los datos de las provincias...")
           
            # pd.concat -> une los DataFrame
            # lista_dfs -> nuestra lista de tablas con las columnas
            # index = True -> crea una numeracion para los datos del archivo 
            # para que no haya lios con la del txt de la dgt
            df_final_cyl = pd.concat(lista_dfs,ignore_index=True)


            # Guardar los datos en la ruta de salida en formato csv
            # index = False -> Para que no escriba en el csv los indices de los datos
            # solo las columnas que queremos 
            df_final_cyl.to_csv(RUTA_SALIDA, index=False, sep=";",encoding="UTF-8")

            print('Archivo guardado correctamente en ', RUTA_SALIDA)

        else:
            print('No se han encontrado archivos para procesar')    
    
    return lista_dfs


# Ejecución del script
extraer_datos_provincias(RUTA_ZIP_COMPLETA, CODIGOS_PROVINCIAS)