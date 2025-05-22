import sqlite3
import os
import random
import pandas as pd
import logging
from datetime import datetime
from typing import List, Dict, Optional

# -------------------------
# CONFIGURACIÓN DE LOGGING
# -------------------------
logging.basicConfig(
    filename='volkswagen_project.log',
    filemode='a',
    format='%(asctime)s - %(levelname)s - %(message)s',
    level=logging.DEBUG
)
logger = logging.getLogger()

# -------------------------
# Simulación de extracción de datos desde PDFs masivos
# -------------------------

def extraer_datos_pdf_simulado(ruta_pdf: str) -> List[Dict]:
    try:
        logger.info(f"Extrayendo datos de PDF: {ruta_pdf}")
        # Simulación de extracción: generamos 100 registros por PDF
        datos = []
        for i in range(1, 101):
            datos.append({
                "pieza_id": f"PZ-{i:05d}",
                "nombre_pieza": f"Pieza VW {i} extraída de {os.path.basename(ruta_pdf)}",
                "descripcion": f"Descripción detallada de pieza {i}",
                "stock": random.randint(0, 1000),
                "precio": round(random.uniform(15.0, 1200.0), 2),
                "fecha_extraccion": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            })
        logger.info(f"Datos extraídos exitosamente: {len(datos)} registros")
        return datos
    except Exception as e:
        logger.error(f"Error extrayendo datos del PDF {ruta_pdf}: {e}")
        return []

# -------------------------
# Clase de manejo de base de datos SQLite
# -------------------------

class BaseDatosVolkswagen:
    def __init__(self, db_name="volkswagen_masivo.db"):
        self.db_name = db_name
        self.conexion = None

    def conectar(self):
        try:
            self.conexion = sqlite3.connect(self.db_name)
            logger.info("Conexión a base de datos SQLite establecida")
        except Exception as e:
            logger.error(f"Error al conectar a la base de datos: {e}")

    def crear_tabla_piezas(self):
        try:
            cursor = self.conexion.cursor()
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS piezas (
                    pieza_id TEXT PRIMARY KEY,
                    nombre_pieza TEXT,
                    descripcion TEXT,
                    stock INTEGER,
                    precio REAL,
                    fecha_extraccion TEXT
                )
            """)
            self.conexion.commit()
            logger.info("Tabla 'piezas' creada o ya existía")
        except Exception as e:
            logger.error(f"Error al crear tabla 'piezas': {e}")

    def insertar_o_actualizar_piezas(self, datos: List[Dict]):
        try:
            cursor = self.conexion.cursor()
            for pieza in datos:
                cursor.execute("""
                    INSERT INTO piezas (pieza_id, nombre_pieza, descripcion, stock, precio, fecha_extraccion)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON CONFLICT(pieza_id) DO UPDATE SET
                        stock=excluded.stock,
                        precio=excluded.precio,
                        fecha_extraccion=excluded.fecha_extraccion
                """, (pieza["pieza_id"], pieza["nombre_pieza"], pieza["descripcion"],
                      pieza["stock"], pieza["precio"], pieza["fecha_extraccion"]))
            self.conexion.commit()
            logger.info(f"Insertados o actualizados {len(datos)} piezas")
        except Exception as e:
            logger.error(f"Error insertando/actualizando piezas: {e}")

    def consultar_stock_bajo(self, limite=50) -> List[Dict]:
        try:
            cursor = self.conexion.cursor()
            cursor.execute("""
                SELECT pieza_id, nombre_pieza, stock, precio FROM piezas WHERE stock < ? ORDER BY stock ASC
            """, (limite,))
            filas = cursor.fetchall()
            resultado = []
            for fila in filas:
                resultado.append({
                    "pieza_id": fila[0],
                    "nombre_pieza": fila[1],
                    "stock": fila[2],
                    "precio": fila[3]
                })
            logger.info(f"Consulta de stock bajo realizada con {len(resultado)} resultados")
            return resultado
        except Exception as e:
            logger.error(f"Error consultando piezas con stock bajo: {e}")
            return []

    def exportar_piezas_excel(self, ruta_archivo: str):
        try:
            df = pd.read_sql_query("SELECT * FROM piezas", self.conexion)
            df.to_excel(ruta_archivo, index=False)
            logger.info(f"Datos exportados a Excel en: {ruta_archivo}")
        except Exception as e:
            logger.error(f"Error exportando datos a Excel: {e}")

    def cerrar(self):
        try:
            if self.conexion:
                self.conexion.close()
                logger.info("Conexión a base de datos cerrada")
        except Exception as e:
            logger.error(f"Error cerrando conexión a base de datos: {e}")

# -------------------------
# Funciones de análisis con pandas
# -------------------------

def analisis_precios(db: BaseDatosVolkswagen):
    try:
        df = pd.read_sql_query("SELECT precio FROM piezas", db.conexion)
        logger.info("Iniciando análisis estadístico de precios")
        print("\n*** Análisis estadístico de precios de piezas ***")
        print(df.describe())

        df_todos = pd.read_sql_query("SELECT pieza_id, nombre_pieza, precio FROM piezas", db.conexion)
        top_5_caras = df_todos.sort_values(by='precio', ascending=False).head(5)
        print("\nTop 5 piezas más caras:")
        print(top_5_caras)
    except Exception as e:
        logger.error(f"Error realizando análisis de precios: {e}")

def generar_reporte_stock_bajo(db: BaseDatosVolkswagen, limite=50):
    piezas_stock_bajo = db.consultar_stock_bajo(limite=limite)
    if not piezas_stock_bajo:
        print("No hay piezas con stock bajo.")
        return
    print(f"\n*** Reporte de piezas con stock menor a {limite} ***")
    for pieza in piezas_stock_bajo:
        print(f"ID: {pieza['pieza_id']}, Nombre: {pieza['nombre_pieza']}, Stock: {pieza['stock']}, Precio: ${pieza['precio']}")

# -------------------------
# Simulación de múltiples PDFs y procesamiento masivo
# -------------------------

def procesamiento_masivo_pdfs(lista_pdfs: List[str], db: BaseDatosVolkswagen):
    for pdf in lista_pdfs:
        datos = extraer_datos_pdf_simulado(pdf)
        db.insertar_o_actualizar_piezas(datos)
        logger.info(f"Procesamiento completo para PDF: {pdf}")

# -------------------------
# Simulación de alertas
# -------------------------

def alertas_stock_bajo(db: BaseDatosVolkswagen, limite=20):
    piezas_bajo = db.consultar_stock_bajo(limite=limite)
    if piezas_bajo:
        logger.warning(f"ALERTA: Hay {len(piezas_bajo)} piezas con stock menor a {limite}")
        for pieza in piezas_bajo:
            print(f"ALERTA STOCK BAJO: {pieza['pieza_id']} - {pieza['nombre_pieza']} stock: {pieza['stock']}")

# -------------------------
# Programa principal
# -------------------------

def main():
    logger.info("Inicio del programa masivo de Volkswagen")

    db = BaseDatosVolkswagen()
    db.conectar()
    db.crear_tabla_piezas()

    # Simulamos 5 archivos PDF de catálogo diferentes
    pdfs = [f"volkswagen_catalogo_parte_{i}.pdf" for i in range(1,6)]

    procesamiento_masivo_pdfs(pdfs, db)

    generar_reporte_stock_bajo(db, limite=30)

    analisis_precios(db)

    alertas_stock_bajo(db, limite=15)

    # Exportar datos completos a Excel
    excel_path = "reporte_piezas_volkswagen.xlsx"
    db.exportar_piezas_excel(excel_path)

    db.cerrar()
    logger.info("Fin del programa")

if __name__ == "__main__":
    main()
