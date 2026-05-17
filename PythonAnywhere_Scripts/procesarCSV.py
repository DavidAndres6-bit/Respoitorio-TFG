import os                           # Trabaja con rutas y carpetas del sistema
import hashlib                      # Genera hashes MD5 a partir de cadenas de texto
import re                           # Limpieza de caracteres no deseados
import pandas as pd                 # Lectura y procesado de datos en tabla
from collections import defaultdict # Diccionario con valor por defecto (evita KeyError al incrementar)
from datetime import date           # Obtiene la fecha actual para el campo fecha_actualizacion

# Carpeta donde están los TXT extraídos de los ZIPs
RUTA_TXT = "/home/DJF/temp/txt"

# Carpeta donde se guardarán los CSVs generados
RUTA_CSV = "/home/DJF/temp/csv"

# Alfabeto válido para matrículas españolas: 25 letras (sin Ñ)
LETRAS = "BCDFGHJKLMNPRSTUVWXYZ"   # 21 consonantes
LETRAS += "AEIOU"                   # + 5 vocales = 26 - 1(Ñ) = 25 letras
N_LETRAS = len(LETRAS)              # 25 — lo usamos en las funciones de matrícula

# Índices (base 0) de las columnas que usamos para construir el hash
INDICES_HASH = [
    0,   # PROVINCIA
    3,   # MARCA
    4,   # MODELO
    5,   # TIPO
    6,   # VARIANTE
    10,  # FEC_PRIM_MATR
    16,  # SUBTIPO_DGT
    17,  # TIPO_DGT
    23,  # TARA
    24,  # PESO_MAX
    27,  # CILINDRADA
    28,  # POTENCIA
    30,  # PROPULSION
    31,  # CATELECT
    35,  # TIPO_DISTINTIVO
    36,  # EMISIONES_EURO
    37,  # EMISIONES_CO2
    38,  # CARROCERIA
    39,  # DISTANCIA_EJES
    40,  # EJE_ANTERIOR
    41,  # EJE_POSTERIOR
    42,  # PLAZAS
    43,  # PLAZAS_MAX
    44,  # PLAZAS_PIE
]

# Índice de la fecha de primera matriculación — lo usamos en generar_matricula()
IDX_FEC_PRIM_MATR = 10  # Formato DD/MM/YYYY

# Columnas que incluimos en los CSVs de provincia (sin MATRICULA, que la añadimos nosotros)
COLUMNAS_CSV = [
    'PROVINCIA',
    'MARCA',
    'MODELO',
    'FECHA_MATR',       # fecha de matriculación
    'PROCEDENCIA',
    'NUEVO_USADO',
    'TIPO_DGT',
    'TIPO_DISTINTIVO',
    'EMISIONES_CO2',
    'PLAZAS',
]

# Diccionario con códigos y nombres de las provincias de CyL
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

# Diccionario con códigos y descripciones del campo PROCEDENCIA
CODIGOS_PROCEDENCIA = {
    '0': 'Fabricación Nacional',
    '1': 'Fabricación Extracomunitaria',
    '2': 'Subasta',
    '3': 'Fabricación Comunitaria',
}

# Diccionario con códigos y descripciones del campo NUEVO_USADO
CODIGOS_NUEVO_USADO = {
    'N': 'Nuevo',
    'U': 'Usado',
}

# Año de corte: antes de 2000 → formato antiguo LLNNNNLL, desde 2000 → NNNNLLL
ANYO_CORTE = 2000

# Fecha de hoy — se calcula una sola vez y se añade a todos los CSVs de provincia
FECHA_ACTUALIZACION = date.today().strftime("%d/%m/%Y")

# Patrón de valores vacíos o nulos — compilado una sola vez para reutilizarlo eficientemente
PATRON_VACIO = re.compile(r"^\s*$|^nan$|^null$|^none$|^n/a$|^-$|^\.$", re.IGNORECASE)

# Mapa global de matrículas ya asignadas — compartido por ambos tipos de matrícula
# Cuenta cuántas veces se ha generado cada matrícula entre todas las provincias
# Si una matrícula ya existe, la nueva se guarda como matricula_1, matricula_2, etc.
matriculas_usadas = defaultdict(int)

# Mapa global de hashes — acumula {hash1: número_de_instancias} entre todos los TXT
mapa_hashes = defaultdict(int)


# ─── LIMPIEZA ────────────────────────────────────────────────────────────────

def limpiar_valor(valor):
    """
    Normalizamos un valor del TXT: devolvemos '0' si es vacío o basura,
    o el valor original si contiene texto o números legibles.
    """
    valor = str(valor).strip()

    # Filtro 1: vacío, nulo, NaN, etc. → "0"
    if PATRON_VACIO.match(valor):
        return "0"

    # Filtro 2: caracteres no imprimibles (ej: M-!) → "0"
    if re.search(r"[^\x20-\x7EáéíóúÁÉÍÓÚñÑüÜ]", valor):
        return "0"

    # Filtro 3: sin ningún alfanumérico → "0"
    if not re.search(r"[a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ]", valor):
        return "0"

    return valor


# ─── HASH Y MATRÍCULA ────────────────────────────────────────────────────────

def calcular_hash(campos):
    """
    Unimos los campos con '|' y devolvemos su hash MD5.
    El separador evita colisiones entre valores adyacentes.
    """
    return hashlib.md5("|".join(campos).encode("utf-8")).hexdigest()


def extraer_anyo(fecha_str):
    """
    Extraemos el año de FEC_PRIM_MATR (formato DD/MM/YYYY).
    Devolvemos 9999 si el formato es inválido → tratamos como moderno.
    """
    try:
        return int(str(fecha_str).strip().split("/")[2])
    except (IndexError, ValueError):
        return 9999


def hash_a_matricula_moderna(hash_md5):
    """
    Convertimos hash MD5 → matrícula NNNNLLL.
    Si la matrícula ya existe en el mapa global, añadimos sufijo _N
    donde N es el número de veces que ya se ha generado esa matrícula.
    """
    numero         = int.from_bytes(bytes.fromhex(hash_md5), byteorder="big")
    digitos        = str(numero % 10000).zfill(4)
    letra1         = LETRAS[(numero // 10000) % N_LETRAS]
    letra2         = LETRAS[(numero // (10000 * N_LETRAS)) % N_LETRAS]
    letra3         = LETRAS[(numero // (10000 * N_LETRAS ** 2)) % N_LETRAS]
    matricula_base = f"{digitos}{letra1}{letra2}{letra3}"

    # Consultamos cuántas veces se ha generado esta matrícula antes
    count = matriculas_usadas[matricula_base]
    matriculas_usadas[matricula_base] += 1

    # Si es la primera vez → matrícula limpia, si no → añadimos sufijo
    return matricula_base if count == 0 else f"{matricula_base}_{count}"


def hash_a_matricula_antigua(hash_md5, letras_provincia):
    """
    Convertimos hash MD5 → matrícula LLNNNNLL.
    Las dos primeras letras son las de la provincia del fichero.
    Si la matrícula ya existe en el mapa global, añadimos sufijo _N
    donde N es el número de veces que ya se ha generado esa matrícula.
    """
    numero         = int.from_bytes(bytes.fromhex(hash_md5), byteorder="big")
    digitos        = str(numero % 10000).zfill(4)
    letra1         = LETRAS[(numero // 10000) % N_LETRAS]
    letra2         = LETRAS[(numero // (10000 * N_LETRAS)) % N_LETRAS]
    matricula_base = f"{letras_provincia}{digitos}{letra1}{letra2}"

    # Consultamos cuántas veces se ha generado esta matrícula antes
    count = matriculas_usadas[matricula_base]
    matriculas_usadas[matricula_base] += 1

    # Si es la primera vez → matrícula limpia, si no → añadimos sufijo
    return matricula_base if count == 0 else f"{matricula_base}_{count}"


def generar_matricula(fila, letras_provincia):
    """
    Recibe una fila del dataframe y devuelve su matrícula generada.
    Actualiza mapa_hashes con la instancia correspondiente.
    """
    # Extraemos y limpiamos los campos del hash
    valores = [limpiar_valor(fila.iloc[idx]) for idx in INDICES_HASH]
    hash1   = calcular_hash(valores)

    # Consultamos la instancia ANTES de incrementar
    instancia = mapa_hashes[hash1]
    mapa_hashes[hash1] += 1

    # Calculamos hash2 = MD5(hash1 + instancia)
    hash2 = calcular_hash([hash1, str(instancia)])

    # Decidimos el formato según el año de primera matriculación
    anyo = extraer_anyo(fila.iloc[IDX_FEC_PRIM_MATR])
    if anyo < ANYO_CORTE:
        return hash_a_matricula_antigua(hash2, letras_provincia)
    else:
        return hash_a_matricula_moderna(hash2)


# ─── PROCESADO ───────────────────────────────────────────────────────────────

def asegurar_carpeta_csv():
    """Nos aseguramos de que la carpeta de CSVs existe antes de escribir."""
    if not os.path.exists(RUTA_CSV):
        os.makedirs(RUTA_CSV)
        print(f"[INFO] Carpeta creada: {RUTA_CSV}")
    else:
        print(f"[INFO] Carpeta ya existe: {RUTA_CSV}")


def procesar_archivo(ruta_archivo):
    """
    Leemos un TXT de provincia, generamos las matrículas fila a fila
    y construimos el dataframe final con los campos pedidos.
    Devolvemos el dataframe listo para guardar como CSV.
    """
    nombre_fichero = os.path.basename(ruta_archivo)
    print(f"  · Procesando: {nombre_fichero}")

    # Extraemos las 2 letras de provincia del nombre del fichero
    # Ej: "42.SORIA.txt" → índices 3 y 4 → "SO"
    letras_provincia = nombre_fichero[3:5].upper()

    # Leemos el TXT completo con pandas
    df = pd.read_csv(ruta_archivo, sep="|", encoding="latin-1", low_memory=False)

    # Generamos las matrículas con apply() en lugar de iterrows()
    # apply() recorre las filas internamente de forma más eficiente que iterrows(),
    # que convierte cada fila en una Series antes de pasarla — con millones de registros
    # esa conversión acumula un coste significativo. apply() evita esa sobrecarga.
    # axis=1 indica que aplicamos la función fila a fila (no columna a columna).
    matriculas = df.apply(lambda fila: generar_matricula(fila, letras_provincia), axis=1)

    # Añadimos la matrícula al dataframe
    df['MATRICULA'] = matriculas

    # Filtramos solo las columnas que nos interesan para el CSV de provincia
    # Comprobamos que existen en el dataframe por si algún TXT tuviera columnas distintas
    columnas_validas = [c for c in COLUMNAS_CSV if c in df.columns]
    df_provincia = df[['MATRICULA'] + columnas_validas].copy()

    # Convertimos el código de provincia al nombre usando el diccionario
    df_provincia['PROVINCIA'] = (
        df_provincia['PROVINCIA']
        .astype(str)
        .str.replace('.0', '', regex=False)
        .str.zfill(2)
        .map(CODIGOS_PROVINCIAS)
        .fillna('Provincia no identificada')
    )

    # Convertimos el código de procedencia al nombre usando el diccionario
    df_provincia['PROCEDENCIA'] = (
        df_provincia['PROCEDENCIA']
        .astype(str)
        .str.replace('.0', '', regex=False)
        .map(CODIGOS_PROCEDENCIA)
        .fillna('Procedencia no identificada')
    )

    # Convertimos N/U al texto correspondiente usando el diccionario
    df_provincia['NUEVO_USADO'] = (
        df_provincia['NUEVO_USADO']
        .astype(str)
        .str.strip()
        .map(CODIGOS_NUEVO_USADO)
        .fillna('No identificado')
    )

    # Limpiamos valores basura del dataframe usando limpiar_valor() sobre cada celda
    df_provincia = df_provincia.map(limpiar_valor)

    # Añadimos la fecha de actualización
    df_provincia['FECHA_ACTUALIZACION'] = FECHA_ACTUALIZACION

    # Añadimos el estado actual: A (Activa) si la matrícula no tiene sufijo, D (Descartada) si lo tiene
    df_provincia['ESTADO_ACTUAL'] = df_provincia['MATRICULA'].apply(
        lambda m: 'D' if '_' in m else 'A'
    )

    print(f"    → {len(df_provincia)} filas procesadas")
    return df_provincia


def procesar_todos_los_txt():
    """
    Recorremos todos los TXT de RUTA_TXT y generamos los 9 CSVs de provincia.
    """
    archivos = sorted([os.path.join(RUTA_TXT, f) for f in os.listdir(RUTA_TXT)])

    if not archivos:
        print(f"[AVISO] No se encontraron archivos en: {RUTA_TXT}")
        return

    print(f"[INFO] {len(archivos)} archivo(s) encontrado(s):\n")
    for ruta in archivos:
        print(f"  · {os.path.basename(ruta)}")
    print()

    for ruta in archivos:
        df_provincia = procesar_archivo(ruta)

        # Extraemos el código de provincia del nombre del fichero para nombrar el CSV
        nombre_fichero = os.path.basename(ruta)
        codigo         = nombre_fichero[:2]
        nombre_prov    = CODIGOS_PROVINCIAS.get(codigo, codigo)
        ruta_csv       = os.path.join(RUTA_CSV, f"datos_{nombre_prov}.csv")

        # Guardamos el CSV de provincia
        df_provincia.to_csv(ruta_csv, index=False, sep=";", encoding="utf-8-sig")
        print(f"    → CSV guardado: {ruta_csv}")


def main():
    """Lanzamos el procesado completo y generamos los 9 CSVs."""
    print("=== GENERADOR DE CSVs DGT - CyL ===\n")
    asegurar_carpeta_csv()
    procesar_todos_los_txt()
    print("\n[INFO] Proceso completado.")


# Solo ejecutamos main() si llamamos directamente a este fichero
if __name__ == "__main__":
    main()