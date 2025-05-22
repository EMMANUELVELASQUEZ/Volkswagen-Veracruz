
CREATE DATABASE volkswagen_veracruz;
USE volkswagen_veracruz;

-- Tabla de departamentos
CREATE TABLE departamentos (
    id_departamento INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(100),
    descripcion TEXT
);

-- Tabla de empleados
CREATE TABLE empleados (
    id_empleado INT PRIMARY KEY AUTO_INCREMENT,
    nombre_completo VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    puesto VARCHAR(50),
    fecha_ingreso DATE,
    id_departamento INT,
    FOREIGN KEY (id_departamento) REFERENCES departamentos(id_departamento)
);

-- Tabla de clientes
CREATE TABLE clientes (
    id_cliente INT PRIMARY KEY AUTO_INCREMENT,
    nombre_completo VARCHAR(100) NOT NULL,
    correo VARCHAR(100),
    telefono VARCHAR(20),
    direccion TEXT,
    fecha_registro DATE
);

-- Tabla de vehículos
CREATE TABLE vehiculos (
    id_vehiculo INT PRIMARY KEY AUTO_INCREMENT,
    vin VARCHAR(50) UNIQUE NOT NULL,
    modelo VARCHAR(100),
    año INT,
    color VARCHAR(50),
    tipo VARCHAR(50),
    id_cliente INT,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

-- Tabla de servicios
CREATE TABLE servicios (
    id_servicio INT PRIMARY KEY AUTO_INCREMENT,
    nombre_servicio VARCHAR(100),
    descripcion TEXT,
    costo DECIMAL(10,2)
);

-- Tabla de órdenes de servicio
CREATE TABLE ordenes_servicio (
    id_orden INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE NOT NULL,
    estado ENUM('Pendiente', 'En proceso', 'Finalizado') DEFAULT 'Pendiente',
    id_cliente INT,
    id_vehiculo INT,
    id_empleado INT,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo),
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado)
);

-- Relación muchos a muchos: ordenes_servicio <-> servicios
CREATE TABLE detalle_servicio (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_orden INT,
    id_servicio INT,
    cantidad INT DEFAULT 1,
    FOREIGN KEY (id_orden) REFERENCES ordenes_servicio(id_orden),
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio)
);

-- Tabla de documentos cargados (estilo iLovePDF)
CREATE TABLE documentos (
    id_documento INT PRIMARY KEY AUTO_INCREMENT,
    nombre_archivo VARCHAR(255),
    tipo ENUM('PDF', 'WORD', 'EXCEL'),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_empleado INT,
    id_cliente INT,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado),
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

-- Tabla de conversiones de documentos
CREATE TABLE conversiones_documento (
    id_conversion INT PRIMARY KEY AUTO_INCREMENT,
    formato_origen VARCHAR(20),
    formato_destino VARCHAR(20),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_documento INT,
    FOREIGN KEY (id_documento) REFERENCES documentos(id_documento)
);

-- Tabla de facturas
CREATE TABLE facturas (
    id_factura INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE,
    total DECIMAL(10,2),
    metodo_pago ENUM('Efectivo', 'Tarjeta', 'Transferencia'),
    id_cliente INT,
    id_orden INT,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_orden) REFERENCES ordenes_servicio(id_orden)
);

-- Tabla de historial de acciones del sistema
CREATE TABLE logs (
    id_log INT PRIMARY KEY AUTO_INCREMENT,
    accion TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_empleado INT,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado)
);

