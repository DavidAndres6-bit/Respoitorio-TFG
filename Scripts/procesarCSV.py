import os                           # Trabaja con rutas y carpetas del sistema
import hashlib                      # Genera hashes MD5 a partir de cadenas de texto
import csv                          # Lee archivos delimitados por pipe '|'
import re                           # Limpieza de caracteres no deseados
from collections import defaultdict # Diccionario con valor por defecto (evita KeyError al incrementar)

# Carpeta donde están los TXT extraídos de los ZIPs
RUTA_TXT = "/home/DJF/temp/txt"

# Índices (base 0) de las columnas que usamos para construir el hash
# Campos originales (base 1): 1,4,5,6,7,10,11,12,14,15,16,17,18,24,25,28,29,31,32,36,37..45
INDICES_HASH = [
    0,   # PROVINCIA
    3,   # MARCA
    4,   # MODELO
    5,   # TIPO
    6,   # VARIANTE
    9,   # FECHA_MATR
    10,  # FEC_PRIM_MATR
    11,  # CLASE_MATR
    13,  # NUEVO_USADO
    14,  # TIPO_TITULAR
    15,  # NUM_TITULARES
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


def limpiar_valor(valor):
    """
    Normalizamos un valor del TXT: devolvemos '0' si es vacío o basura,
    o el valor original si contiene texto o números legibles.
    """
    # Patrón de valores que consideramos nulos o sin sentido
    patron_vacio = re.compile(r"^\s*$|^nan$|^null$|^none$|^n/a$|^-$|^\.$", re.IGNORECASE)

    valor = valor.strip()

    # Filtro 1: vacío, nulo, NaN, etc. → "0"
    if patron_vacio.match(valor):
        return "0"

    # Filtro 2: caracteres no imprimibles (ej: M-!) → "0"
    # \x20-\x7E cubre ASCII estándar; añadimos acentos y ñ del español
    if re.search(r"[^\x20-\x7EáéíóúÁÉÍÓÚñÑüÜ]", valor):
        return "0"

    # Filtro 3: sin ningún alfanumérico (ej: "!", "?", ">><<") → "0"
    # Preservamos valores como "ALFA-ROMEO" o "CLASE A (W177)"
    if not re.search(r"[a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ]", valor):
        return "0"

    return valor


def calcular_hash(campos):
    """
    Unimos los campos limpios con '|' y devolvemos su hash MD5.
    El separador '|' evita colisiones entre valores adyacentes.
    """
    cadena = "|".join(campos)
    return hashlib.md5(cadena.encode("utf-8")).hexdigest()


def procesar_archivo(ruta_archivo, mapa_hashes):
    """
    Leemos un TXT fila a fila, limpiamos los campos elegidos,
    calculamos el hash de cada fila y acumulamos el conteo en mapa_hashes.
    Devolvemos el número de filas procesadas correctamente.
    """
    print(f"  · Procesando: {os.path.basename(ruta_archivo)}")
    filas_ok = 0
    filas_error = 0

    # Abrimos en latin-1 porque es la codificación que usa la DGT en sus TXT
    with open(ruta_archivo, "r", encoding="latin-1") as f:
        lector = csv.reader(f, delimiter="|")
        next(lector)  # Saltamos la cabecera

        for num_fila, fila in enumerate(lector, start=2):
            try:
                # Extraemos y limpiamos solo los campos que nos interesan
                valores = [limpiar_valor(fila[idx]) for idx in INDICES_HASH]

                # Calculamos el hash e incrementamos su contador en el mapa
                mapa_hashes[calcular_hash(valores)] += 1
                filas_ok += 1

            except Exception as e:
                filas_error += 1
                print(f"    [AVISO] Fila {num_fila} omitida: {e}")

    print(f"    → {filas_ok} filas procesadas, {filas_error} errores")
    return filas_ok


def procesar_todos_los_txt():
    """
    Construimos la lista de TXT disponibles, los procesamos uno a uno
    y devolvemos el mapa global {hash_md5: número_de_apariciones}.
    """
    # Mapa donde acumulamos los resultados de todos los archivos
    # defaultdict(int) inicializa a 0 cualquier clave nueva automáticamente
    mapa_hashes = defaultdict(int)

    # Como en RUTA_TXT solo hay TXT, no filtramos por extensión
    archivos = sorted([os.path.join(RUTA_TXT, f) for f in os.listdir(RUTA_TXT)])

    if not archivos:
        print(f"[AVISO] No se encontraron archivos en: {RUTA_TXT}")
        return mapa_hashes

    print(f"[INFO] {len(archivos)} archivo(s) encontrado(s):\n")
    for ruta in archivos:
        print(f"  · {os.path.basename(ruta)}")
    print()

    total_filas = 0
    for ruta in archivos:
        total_filas += procesar_archivo(ruta, mapa_hashes)

    print(f"\n[INFO] Proceso completado.")
    print(f"  · Total filas procesadas : {total_filas}")
    print(f"  · Hashes únicos generados: {len(mapa_hashes)}")

    return mapa_hashes


def main():
    """
    Lanzamos el procesado completo y mostramos los 10 hashes más repetidos.
    """
    print("=== PROCESADOR DE TXT DGT - CyL ===\n")

    mapa = procesar_todos_los_txt()

    print("\n--- Top 10 hashes más repetidos ---")
    # Ordenamos el mapa por conteo de mayor a menor y nos quedamos con los 10 primeros
    top10 = sorted(mapa.items(), key=lambda x: x[1], reverse=True)[:10]
    for hash_val, count in top10:
        print(f"  {hash_val}  →  {count} veces")

    # Devolvemos el mapa por si otros módulos necesitan usarlo
    return mapa


# Solo ejecutamos main() si llamamos directamente a este fichero
# Si otro módulo nos importa, este bloque no se ejecuta
if __name__ == "__main__":
    main()
