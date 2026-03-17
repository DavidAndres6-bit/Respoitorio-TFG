import pandas as pd         # Librería para análisis de datos
import os                   # Para trabajar con rutas y archivos del sistema operativo
import hashlib              # Para generar hashes y crear matrículas únicas

# ==========================
# RUTAS DE ENTRADA Y SALIDA
# ==========================
# Carpeta donde están los TXT filtrados de los ZIP de la DGT
RUTA_ENTRADA = "/home/DJF/temp/txt"

# Carpeta y archivo CSV donde se guardará el CSV final
#RUTA_SALIDA = "/home/DJF/temp/csv/datos_Automoviles.csv"


# Ruta de salida base para los csv de cada provincia
RUTA_BASE = "/home/DJF/temp/csv"


# ==========================
# COLUMNAS DE INTERÉS
# ==========================
# Columnas que realmente vamos a usar del TXT de la DGT
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

# ==========================
# LETRAS PERMITIDAS PARA MATRÍCULAS
# ==========================
LETRAS = "BCDFGHJKLMNPRSTVWXYZ"

# ==========================
# DICCIONARIO CÓDIGO -> NOMBRE DE PROVINCIA
# ==========================
# Esto permite reemplazar los códigos de provincia por su nombre completo
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

# ==========================
# FUNCIÓN PARA GENERAR MATRÍCULA FICTICIA Y ÚNICA
# ==========================
"""
def generar_matricula(fila):
   
    Genera una matrícula ficticia determinista a partir de los campos de cada vehículo

    Formato final: NNNNLLL
        - NNNN: números (0000-9999)
        - LLLL: letras del conjunto LETRAS
    
    # Concatenamos los campos identificativos + índice de la fila
    base = (
        fila["PROVINCIA"] +
        fila["MARCA"] +
        fila["MODELO"] +
        fila["FECHA_MATR"] +
        fila["TIPO_DGT"] +
        fila["PLAZAS"]
    )

    # Generamos hash SHA256
    hash_obj = hashlib.sha256(base.encode())
    hash_hex = hash_obj.hexdigest()

    # Números: primeros 4 caracteres del hash
    numeros = int(hash_hex[:4], 16) % 10000

    # Letras: siguientes 3 caracteres
    letras = ""
    for i in range(3):
        letras += LETRAS[int(hash_hex[4 + i], 16) % len(LETRAS)]

    matricula = f"{numeros:04d}{letras}"
    return matricula
"""
# ==========================
# FUNCIÓN PARA ASEGURAR CARPETA DEL CSV
# ==========================
def asegurar_carpeta_csv():
    """
    Comprueba que existe la carpeta donde guardaremos el CSV final.
    Si no existe, la crea.
    """
    carpeta_csv = os.path.dirname(RUTA_BASE)
    if not os.path.exists(carpeta_csv):
        os.makedirs(carpeta_csv)
        print(f"[INFO] Carpeta creada: {carpeta_csv}")
    else:
        print(f"[INFO] Carpeta ya existe: {carpeta_csv}")

# ==========================
# FUNCIÓN PARA PROCESAR UN TXT INDIVIDUAL
# ==========================
def procesar_archivo_txt(archivoTxt, columnas_interes):
    """
    Abre un archivo TXT y selecciona solo las columnas de interés.
    Además convierte los códigos de provincia en nombre completo.
    """
    print(f'Tratando el archivo: {archivoTxt}')
    ruta_completa = os.path.join(RUTA_ENTRADA, archivoTxt)

    # Leer TXT con separador '|' y codificación latin-1
    df_provincia = pd.read_csv(
        ruta_completa,
        sep="|",
        encoding="latin-1",
        low_memory=False
    )


    # Filtrar columnas deseadas
    df_filtrado = [df_provincia[c] for c in columnas_interes if c in df_provincia.columns]
    df_final_provincia = pd.concat(df_filtrado, axis=1)

    # Reemplazar códigos de provincia por nombres completos
    # Convertimos el número a string y añadimos 0 delante si es necesario
    df_final_provincia["PROVINCIA"] = df_final_provincia["PROVINCIA"].astype(str).str.zfill(2)

    # Reemplazamos código por nombre de provincia
    df_final_provincia["PROVINCIA"] = df_final_provincia["PROVINCIA"].map(CODIGOS_PROVINCIAS).fillna("SIN DATOS")

    return df_final_provincia

# ==========================
# FUNCIÓN PARA PROCESAR TODOS LOS TXT Y CREAR CSV FINAL
# ==========================
def procesar_datos():
    """
    Recorre todos los TXT de la carpeta de entrada, filtra columnas,
    genera matrículas ficticias y guarda el CSV final.
    """
    asegurar_carpeta_csv()
    nombre_ficheros = os.listdir(RUTA_ENTRADA)
    print(f'Procesando los archivos: {nombre_ficheros} .....')

    # Procesar solo archivos TXT
    for archivoTxt in nombre_ficheros:

        # Asegurarnos que el archivo es un txt
        if(archivoTxt.endswith('txt')):
            
            # Llamada a la funcion que procesa los txt
            df_prov = procesar_archivo_txt(archivoTxt, columnas)

            # Limpiar los datos incorrectos o incompletos del archivo
            df_prov = df_prov.astype(str)
            df_prov = df_prov.replace('¡', 'SIN DATOS', regex=False)
            df_prov = df_prov.replace('ND', 'SIN DATOS', regex=False)
            df_prov = df_prov.replace('NAN', 'SIN DATOS', regex=False)
            df_prov = df_prov.replace('', 'SIN DATOS', regex=False)

            # ====================================
            # BLOQUE DE GENERACION DE MATRICULAS
            # ====================================







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

# ==========================
# FUNCIÓN PRINCIPAL
# ==========================
def main():
    # Asegurar carpeta del CSV
    asegurar_carpeta_csv()
    # Procesar datos y generar CSV final
    procesar_datos()

# ==========================
# BLOQUE PRINCIPAL
# ==========================
if __name__ == "__main__":
    main()