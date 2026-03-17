import pandas as pd      #Libreria utilizada para el analisis de datos
import os               # Trabaja con rutas y archivos del sistema operativo


# Configurar la ruta en la que estan los txt descargados en pythonAywere 
RUTA_ENTRADA = "/home/DJF/temp/zips"

#Configurar ruta en la se generaran los csv con los datos filtrados
RUTA_SALIDA = "/home/DJF/temp/datos_CyL_final.csv"


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
def obtener_listado_archivos(codigos_provincias):

    # Obtener el nombre de los ficheros
    nombre_ficheros = os.listdir(RUTA_ENTRADA)
    
    # Recorrer los nombres de los archivos
    # El [:2] coge los dos primeros caracteres del nombre (los digitos que nos interesan)
    archivos_cyl = []
    for nom in nombre_ficheros:
        # Filtrar que sea un txt y de la provincia que queremo
        if nom[:2] in codigos_provincias and nom.endswith('.txt'):
            archivos_cyl.append(nom)
            
    return archivos_cyl


# Función para abrir y filtrar un archivo TXT individual
def procesar_archivo_txt(archivoTxt, columnas_interes):
    print('Tratando el archivo ', archivoTxt)
    
    ruta_completa = os.path.join(RUTA_ENTRADA,archivoTxt)
    
     # Leer el txt indicando el separador para poder quedarnos con los datos
    df_provincia = pd.read_csv(ruta_completa, sep="|", encoding="latin-1", low_memory=False)
        
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
def extraer_datos_provincias(codigos_provincias):

# Ruta donde están los TXT ya filtrados al descomprimir el ZIP
RUTA_ENTRADA = "/home/DJF/temp/txt"

# Ruta donde se generará el CSV final
RUTA_SALIDA = "/home/DJF/temp/csv/datos_CyL_final.csv"


# Definir las columnas que necesitamos del TXT de la DGT
columnas = [
'PROVINCIA',
'MARCA',
'MODELO',
'FECHA_MATR',
'PROCEDENCIA',
'NUEVO_USADO',
'TIPO_DGT',
'TIPO_DISTINTIVO',
'EMISIONES_CO2',
'PLAZAS'
]

# Función para asegurar que la carpeta donde se guardará el CSV existe
def asegurar_carpeta_csv():

    carpeta_csv = os.path.dirname(RUTA_SALIDA)

    if not os.path.exists(carpeta_csv):
        os.makedirs(carpeta_csv)
        print(f"[INFO] Carpeta creada: {carpeta_csv}")

    else:
        print(f"[INFO] Carpeta ya existe: {carpeta_csv}")

# Función para abrir y filtrar un archivo TXT individual
def procesar_archivo_txt(archivoTxt, columnas_interes):

    print('Tratando el archivo ', archivoTxt)

    # Construir la ruta completa del archivo
    ruta_completa = os.path.join(RUTA_ENTRADA, archivoTxt)

    # Leer el TXT indicando el separador usado por la DGT
    df_provincia = pd.read_csv(
        ruta_completa,
        sep="|",
        encoding="latin-1",
        low_memory=False
    )

    # Creamos una lista vacía para almacenar solo las columnas que necesitamos
    df_filtrado = []

    # Recorremos la lista de columnas definidas arriba
    for c in columnas_interes:

        # Comprobamos si la columna existe en el archivo original
        if c in df_provincia.columns:

            # Si existe, la añadimos a nuestra lista
            df_filtrado.append(df_provincia[c])

    # Convertimos la lista de columnas de vuelta a un DataFrame
    df_final_provincia = pd.concat(df_filtrado, axis=1)

    return df_final_provincia



# Función que recorre todos los TXT y une los datos
def procesar_datos():


    # Lista para almacenar los DataFrames de cada provincia
    lista_dfs = []


    # Llamada a la función para obtener los nombres filtrados
    archivos_cyl = obtener_listado_archivos(codigos_provincias)

    # Imprimir los archivos que hemos encontrado
    print('Procesando los archivos ', archivos_cyl, '.....')    

    # Bucle para recorrer los archivos
    for archivoTxt in archivos_cyl:
            
        # Llamada a la función que trata cada TXT
        df_resultado = procesar_archivo_txt(archivoTxt, columnas)
        
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

        #  ----- LIMPIEZA DE DATOS INCORRECTOS EN LOS TXT DE LA DGT ---- 
        # Quitar valores incompletos por un valor comun
        valores_basura = ['¡', 'ND', 'NAN', 'EMPTY', 'NONE', '']

        df_final_cyl['TIPO_DISTINTIVO'] = df_final_cyl['TIPO_DISTINTIVO'].replace(valores_basura, 'SIN DATOS')

        # Guardar los datos en la ruta de salida en formato csv
        # index = False -> Para que no escriba en el csv los indices de los datos
        # solo las columnas que queremos 
        df_final_cyl.to_csv(RUTA_SALIDA, index=False, sep=";",encoding="UTF-8")

    # Obtener todos los archivos de la carpeta
    nombre_ficheros = os.listdir(RUTA_ENTRADA)

    print('Procesando los archivos ', nombre_ficheros, '.....')

    # Recorrer los archivos de la carpeta
    for archivoTxt in nombre_ficheros:

        # Solo procesar archivos TXT
        if archivoTxt.endswith('.txt'):

            # Llamada a la función que trata cada TXT
            df_resultado = procesar_archivo_txt(archivoTxt, columnas)

            # Añadir el resultado a nuestra lista
            lista_dfs.append(df_resultado)


    # Unir todos los DataFrame recogidos si existen
    if lista_dfs:

        print("Uniendo todos los datos de las provincias...")

        df_final_cyl = pd.concat(lista_dfs, ignore_index=True)

        # ----- LIMPIEZA DE DATOS INCORRECTOS EN LOS TXT DE LA DGT ----

        # Forzar conversion a tipo texto
        df_final_cyl = df_final_cyl.astype(str)

        # Sustituir valores incorrectos por un valor común
        df_final_cyl = df_final_cyl.replace('¡', 'SIN DATOS', regex=False)
        df_final_cyl = df_final_cyl.replace('ND', 'SIN DATOS', regex=False)
        df_final_cyl = df_final_cyl.replace('NAN', 'SIN DATOS', regex=False)
        df_final_cyl = df_final_cyl.replace('', 'SIN DATOS', regex=False)


        # Guardar los datos en formato CSV
        df_final_cyl.to_csv(
            RUTA_SALIDA,
            index=False,
            sep=";",
            encoding="UTF-8"
        )


        print('Archivo guardado correctamente en ', RUTA_SALIDA)

    else:

        print('No se han encontrado archivos para procesar')


# Función principal que centraliza la ejecución
def main():
    # Asegurar que existe la carpeta donde guardaremos el CSV
    asegurar_carpeta_csv()

    # Llamamos a la función que procesa todos los TXT
    procesar_datos()


# Ejecutar solo si se lanza este script directamente
if __name__ == "__main__":
    main()
