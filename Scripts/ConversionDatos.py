import pandas as pd         # Librería para análisis de datos
import os                   # Para trabajar con rutas y archivos del sistema operativo
import hashlib              # Para generar hashes y crear matrículas únicas


# Carpeta donde están los TXT filtrados de los ZIP de la DGT
RUTA_ENTRADA = "/home/DJF/temp/txt"

# Ruta a la carpeta de los CSV
RUTA_BASE = "/home/DJF/temp/csv"

# Columnas que nos interesan del TXT de la DGT
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

# Diccionario con nombres y codigos de las provincias
CODIGOS_PROVINCIAS = {
    '05': 'ÁVILA',
    '09': 'BURGOS',
    '24': 'LEÓN',
    '34': 'PALENCIA',
    '37': 'SALAMANCA',
    '40': 'SEGOVIA',
    '42': 'SORIA',
    '47': 'VALLADOLID',
    '49': 'ZAMORA'
}

# Funcion que comprueba que existe la carpeta para almacenar los csv
# En caso de no existir la crearia
def asegurar_carpeta_csv():

    # En caso de no existir la creamos
    if not os.path.exists(RUTA_BASE):
        os.makedirs(RUTA_BASE)
        print(f" Carpeta creada: {RUTA_BASE}")
    else:
        print(f" La carpeta", {RUTA_BASE}, " ya existe")


# Funcion para procesar un TXT individual
def procesar_archivo_txt(archivoTxt, columnas_interes):

    print(f'Tratando el archivo: {archivoTxt}')
    ruta_completa = os.path.join(RUTA_ENTRADA, archivoTxt)

    # Leemos el txt con el separador y codificacion de la dgt
    df_provincia = pd.read_csv(ruta_completa,sep="|",encoding="latin-1",low_memory=False)

    # Filtramos las columnas con las que nos queremos quedar

    # Lista para almacenar las columnas con las que nos quedaremos
    columnas_validas = []

    # Recorrer las columnas que nos interesan y las vamos añadiendo si las encontramos
    for col in columnas_interes:
        if col in df_provincia.columns:
            columnas_validas.append(col)

    # Creamos una copia que contiene las columnas que nos interesan
    df_final_provincia = df_provincia[columnas_validas].copy()

    # Lo pasamos a texto y limpiamos el ".0" para que coincida con nuestro diccionario.
    df_final_provincia["PROVINCIA"] = df_final_provincia["PROVINCIA"].astype(str).str.replace('.0', '', regex=False).str.zfill(2)

    # Cambiamos los códigos por los nombres usando nuestro diccionario
    df_final_provincia["PROVINCIA"] = df_final_provincia["PROVINCIA"].map(CODIGOS_PROVINCIAS)

    # Controlamos el caso de que un codigo no estuviese en nuestro diccionario
    df_final_provincia["PROVINCIA"] = df_final_provincia["PROVINCIA"].fillna("Provincia no identificada")

    return df_final_provincia


# Funcion para crear el csv a partir del txt procesado
def procesar_datos():

    # Comprobamos que archivos hay en la carpeta de los txt
    listado_archivos = os.listdir(RUTA_ENTRADA)

    for archivoTxt in listado_archivos:

        # Asegurarnos que el archivo es un txt
        if(archivoTxt.endswith('txt')):

            print(f'Procesando: {archivoTxt} .....')

            # Llamada a la funcion que procesa los txt
            df_prov = procesar_archivo_txt(archivoTxt, columnas)

            # Limpiar los datos incorrectos o incompletos del archivo
            df_prov = df_prov.astype(str)
            df_prov = df_prov.replace('¡', 'SIN DATOS', regex=False)
            df_prov = df_prov.replace('ND', 'SIN DATOS', regex=False)
            df_prov = df_prov.replace('NAN', 'SIN DATOS', regex=False)
            df_prov = df_prov.replace('nan', 'SIN DATOS')
            df_prov = df_prov.replace('', 'SIN DATOS', regex=False)
            df_prov['EMISIONES_CO2'] = df_prov['EMISIONES_CO2'].replace('nan', 'SIN DATOS')

            # Guardar el CSV con los datos filtrados y limpios de esa provincia

            # Cogemos el nombre de la provincia del archivo txt que estamos recorriendo (es la provincia correspondiente)

            # Coger el codigo de la provincia de ese txt
            codigo = archivoTxt[:2]

            # Con ese codigo coger el nombre
            nombre_prov = CODIGOS_PROVINCIAS.get(codigo)

            # Nombre de cada csv con su provincia
            nombre_csv = f"datos_{nombre_prov}.csv"
            ruta_csv = os.path.join(RUTA_BASE, nombre_csv)

            # Generar el CSV
            df_prov.to_csv(ruta_csv,index=False,sep=";",encoding="UTF-8")

            # Mostrar el proceso por pantalla
            print('Generando el archivo ', nombre_csv, '....')

        else:
            print('No se han encontrado archivos para procesar')



# Funcion principal
def main():
    # Asegurar carpeta del CSV
    asegurar_carpeta_csv()
    # Procesar datos y generar CSV final
    procesar_datos()

# Blque principal del script
if __name__ == "__main__":
    main()