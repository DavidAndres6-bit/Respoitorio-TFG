import pandas as pd      # Libreria utilizada para el analisis de datos
import os                # Trabaja con rutas y archivos del sistema operativo


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