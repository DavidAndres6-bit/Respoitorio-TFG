/* Tabla para almacenar los datos de los vehiculos */
CREATE TABLE vehiculos_cyl(
    -> MATRICULA VARCHAR (7) PRIMARY KEY,
    -> PROVINCIA VARCHAR (50),
    -> MARCA VARCHAR (100),
    -> MODELO VARCHAR (100),
    -> FECHA_MATR VARCHAR (20),
    -> PROCEDENCIA VARCHAR (50),
    -> ESTADO VARCHAR (20),
    -> TIPO_DGT VARCHAR (100),
    -> TIPO_DISTINTIVO VARCHAR (50),
    -> EMISIONES_CO2 VARCHAR (50),
    -> PLAZAS VARCHAR (10),
    -> FECHA_ULTIMA_MODIFICACION DATETIME,
    -> ACTIVO TINYINT(1) DEFAULT 1
    -> );

/* Tabla para almacenar los usuarios de la aplicacion  */
CREATE TABLE usuarios (
    -> id INT AUTO_INCREMENT PRIMARY KEY,
    -> nombre_usuario VARCHAR (100),
    -> contrasenia VARCHAR (30),
    -> correo_electronico VARCHAR (200),
    -> telefono INT (9)
    -> rol VARCHAR(50)
    -> );

/* Tabla para guardar las inspecciones realizas en cada vehiculo */
CREATE TABLE inspecciones (
    ->     id INT AUTO_INCREMENT PRIMARY KEY,
    ->     matricula_coche VARCHAR(9) NOT NULL,
    ->     km_anterior_inspeccion INT,
    ->     km_actuales INT NOT NULL,
    ->     acondicionamiento_exterior TINYINT(1),
    ->     acondicionamiento_interior TINYINT(1),
    ->     alumbrado_senializacion TINYINT(1),
    ->     emisiones TINYINT(1),
    ->     frenos TINYINT(1),
    ->     ejes_ruedas_neumaticos TINYINT(1),
    ->     motor_transmision TINYINT(1),
    ->     resultado_inspeccion VARCHAR(20),
    ->     fecha_proxima_inspeccion DATE,
    ->     observaciones TEXT);

/* Relacion de la tabla inspecciones con la tabla vehiculos */
ALTER TABLE inspecciones ADD CONSTRAINT fk_inspeccion_vehiculo FOREIGN KEY (matricula_coche) REFERENCES vehiculos_cyl(MATRICULA);


/* Tabla para almacenar el precio de las Tarifas de ITV por tipo de vehiculo  */
CREATE TABLE tarifas(
    -> ID INT AUTO_INCREMENT PRIMARY KEY,
    -> tipo_vehiculo TEXT,
    -> precio DECIMAL(10,2));

/* Insertar datos obtenidos de pagina web con las tarifas de la DGT de Castilla y Leon del año 2026 */
 INSERT INTO tarifas (tipo_vehiculo, precio) VALUES 
    -> ('Ciclomotor', 23.03),
    -> ('Vehiculos Electricos e Hibridos', 51.00),
    -> ('Vehiculos sin etiqueta', 65.00),
    -> ('Vehiculos con etiqueta B', 55.00),
    -> ('Vehiculos con etiqueta C', 45.00),
    -> ('Vehiculos con etiqueta 0', 35.00),
    -> ('Vehiculos pesados', 81.09),
    -> ('Tractores', 81.09);

/*   Tabla para almacenar los defectos de cada inspeccion  */
CREATE TABLE inspeccion_defectos (
    -> id INT AUTO_INCREMENT PRIMARY KEY,
    -> id_inspeccion INT NOT NULL,       
    -> unidad VARCHAR(50),               
    -> descripcion TEXT,                 
    -> calificacion VARCHAR(20));   

/* Relacion de la tabla inspeccion_defectos con la tabla inspecciones */
ALTER TABLE inspeccion_defectos ADD CONSTRAINT fk_inspeccion_defectos FOREIGN KEY (id_inspeccion) REFERENCES inspecciones(id) ON DELETE CASCADE;