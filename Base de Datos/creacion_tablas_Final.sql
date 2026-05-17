/* SCRIPT DE CREACIÓN DE BASE DE DATOS — ITV CyL
   Base de datos: DJF$TFG (MySQL — PythonAnywhere) */


/* Tabla para almacenar los datos de los vehículos procesados
   mensualmente desde los ficheros de la DGT */
CREATE TABLE vehiculos_cyl (
    MATRICULA            VARCHAR(12)  NOT NULL PRIMARY KEY,
    PROVINCIA            VARCHAR(12),
    MARCA                VARCHAR(100),
    MODELO               VARCHAR(100),
    FECHA_MATR           DATE,
    PROCEDENCIA          VARCHAR(40),
    NUEVO_USADO          VARCHAR(10),
    TIPO_DGT             VARCHAR(40),
    TIPO_DISTINTIVO      VARCHAR(20),
    EMISIONES_CO2        VARCHAR(10),
    PLAZAS               VARCHAR(10),
    FECHA_ACTUALIZACION  DATETIME,
    ESTADO_ACTUAL        CHAR(1) DEFAULT 'A'
);


/* Tabla para almacenar los usuarios de la aplicación */
CREATE TABLE usuarios (
    id                  INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario      VARCHAR(100) UNIQUE,
    contrasenia         VARCHAR(30),
    correo_electronico  VARCHAR(200) UNIQUE,
    telefono            INT,
    rol                 VARCHAR(50)
);


/* Tabla para almacenar los clientes que realizan inspecciones */
CREATE TABLE clientes (
    id      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    dni     VARCHAR(15)  NOT NULL,
    nombre  VARCHAR(100)
);


/* Tabla intermedia para la relación N:M entre vehículos y clientes.
   Un mismo vehículo puede ser inspeccionado por distintas personas
   a lo largo del tiempo */
CREATE TABLE vehiculo_cliente (
    id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    matricula   VARCHAR(12)  NOT NULL,
    id_cliente  INT          NOT NULL,
    CONSTRAINT vehiculo_cliente_ibfk_1 FOREIGN KEY (matricula)   REFERENCES vehiculos_cyl (MATRICULA),
    CONSTRAINT vehiculo_cliente_ibfk_2 FOREIGN KEY (id_cliente)  REFERENCES clientes (id)
);


/* Tabla para guardar las inspecciones realizadas sobre cada vehículo */
CREATE TABLE inspecciones (
    id                          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    matricula_coche             VARCHAR(9)   NOT NULL,
    km_anterior_inspeccion      INT,
    km_actuales                 INT          NOT NULL,
    acondicionamiento_exterior  TINYINT(1),
    acondicionamiento_interior  TINYINT(1),
    alumbrado_senializacion     TINYINT(1),
    emisiones                   TINYINT(1),
    frenos                      TINYINT(1),
    ejes_ruedas_neumaticos      TINYINT(1),
    motor_transmision           TINYINT(1),
    resultado_inspeccion        VARCHAR(20),
    fecha_proxima_inspeccion    DATE,
    observaciones               TEXT,
    fecha_inspeccion            DATE,
    CONSTRAINT fk_inspeccion_vehiculo FOREIGN KEY (matricula_coche) REFERENCES vehiculos_cyl (MATRICULA)
);


/* Tabla para almacenar los defectos detectados en cada inspección */
CREATE TABLE inspeccion_defectos (
    id             INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_inspeccion  INT          NOT NULL,
    unidad         VARCHAR(50),
    descripcion    TEXT,
    calificacion   VARCHAR(20),
    CONSTRAINT fk_inspeccion_defectos FOREIGN KEY (id_inspeccion) REFERENCES inspecciones (id) ON DELETE CASCADE
);


/* Tabla para almacenar las tarifas de ITV por tipo de vehículo */
CREATE TABLE tarifas (
    ID             INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tipo_vehiculo  TEXT,
    precio         DECIMAL(10,2)
);

/* Insertar las tarifas oficiales de ITV de Castilla y León para el año 2026,
   obtenidas de la página web de referencia incluida en la bibliografía */
INSERT INTO tarifas (tipo_vehiculo, precio) VALUES
    ('Ciclomotor',                      23.03),
    ('Vehículos Eléctricos e Híbridos', 51.00),
    ('Vehículos sin etiqueta',          65.00),
    ('Vehículos con etiqueta B',        55.00),
    ('Vehículos con etiqueta C',        45.00),
    ('Vehículos con etiqueta 0',        35.00),
    ('Vehículos pesados',               81.09),
    ('Tractores',                       81.09);