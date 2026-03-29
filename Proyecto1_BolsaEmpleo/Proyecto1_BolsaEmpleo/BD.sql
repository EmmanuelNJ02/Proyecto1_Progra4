DROP DATABASE IF EXISTS bolsa_empleo;
CREATE DATABASE bolsa_empleo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bolsa_empleo;

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'EMPRESA', 'OFERENTE') NOT NULL,
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE administrador (
    usuario_id INT PRIMARY KEY,
    nombre_admin VARCHAR(150) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE empresa (
    usuario_id INT PRIMARY KEY,
    nombre_empresa VARCHAR(150) NOT NULL,
    localizacion TEXT NOT NULL,
    correo_electronico VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    descripcion TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE oferente (
    usuario_id INT PRIMARY KEY,
    identificacion VARCHAR(50) NOT NULL UNIQUE,
    nombre_oferente VARCHAR(100) NOT NULL,
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    nacionalidad VARCHAR(50) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    lugar_residencia TEXT NOT NULL,
    ruta_curriculum VARCHAR(500),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE caracteristica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_caracteristica VARCHAR(100) NOT NULL,
    caracteristica_padre_id INT NULL,
    FOREIGN KEY (caracteristica_padre_id) REFERENCES caracteristica(id) ON DELETE CASCADE
);

CREATE TABLE puesto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    empresa_id INT NOT NULL,
    descripcion_general TEXT NOT NULL,
    salario DECIMAL(10,2) NOT NULL,
    tipo_publicacion ENUM('PUBLICO', 'PRIVADO') NOT NULL DEFAULT 'PUBLICO',
    activo BOOLEAN DEFAULT TRUE,
    fecha_publicacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (empresa_id) REFERENCES empresa(usuario_id) ON DELETE CASCADE
);

CREATE TABLE puesto_caracteristica (
    puesto_id INT NOT NULL,
    caracteristica_id INT NOT NULL,
    nivel_deseado TINYINT NOT NULL,
    PRIMARY KEY (puesto_id, caracteristica_id),
    FOREIGN KEY (puesto_id) REFERENCES puesto(id) ON DELETE CASCADE,
    FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id) ON DELETE CASCADE
);

CREATE TABLE oferente_habilidad (
    oferente_id INT NOT NULL,
    caracteristica_id INT NOT NULL,
    nivel TINYINT NOT NULL,
    PRIMARY KEY (oferente_id, caracteristica_id),
    FOREIGN KEY (oferente_id) REFERENCES oferente(usuario_id) ON DELETE CASCADE,
    FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id) ON DELETE CASCADE
);

INSERT INTO usuario (correo_electronico, clave, rol, estado)
VALUES ('admin@bolsaempleo.com', 'admin123', 'ADMIN', 'APROBADO');

INSERT INTO administrador (usuario_id, nombre_admin)
VALUES (1, 'Administrador General');

INSERT INTO caracteristica (nombre_caracteristica, caracteristica_padre_id) VALUES
('Lenguajes de programación', NULL),
('Tecnologías Web', NULL),
('Testing', NULL),
('Bases de Datos', NULL);

INSERT INTO caracteristica (nombre_caracteristica, caracteristica_padre_id) VALUES
('Java', 1),
('C#', 1),
('Python', 1),
('HTML', 2),
('CSS', 2),
('JavaScript', 2),
('Thymeleaf', 2),
('JUnit', 3),
('Casos de prueba', 3),
('MySQL', 4),
('PostgreSQL', 4);

-- Datos de ejemplo para que la parte pública muestre contenido desde el inicio.
INSERT INTO usuario (correo_electronico, clave, rol, estado)
VALUES ('empresa.demo@bolsaempleo.com', 'demo123', 'EMPRESA', 'APROBADO');

INSERT INTO empresa (usuario_id, nombre_empresa, localizacion, correo_electronico, telefono, descripcion)
VALUES (2, 'Web Soft', 'San José, Costa Rica', 'empresa.demo@bolsaempleo.com', '2222-3333', 'Empresa demo para pruebas del sistema.');

INSERT INTO usuario (correo_electronico, clave, rol, estado)
VALUES ('oferente.demo@bolsaempleo.com', 'demo123', 'OFERENTE', 'APROBADO');

INSERT INTO oferente (usuario_id, identificacion, nombre_oferente, primer_apellido, segundo_apellido, nacionalidad, telefono, lugar_residencia, ruta_curriculum)
VALUES (3, '1-1111-1111', 'Ana', 'Sánchez', 'Rojas', 'Costarricense', '8888-9999', 'Heredia, Costa Rica', NULL);

INSERT INTO oferente_habilidad (oferente_id, caracteristica_id, nivel) VALUES
(3, 5, 4),   -- Java
(3, 8, 5),   -- HTML
(3, 9, 4),   -- CSS
(3, 11, 4),  -- Thymeleaf
(3, 14, 4);  -- MySQL

INSERT INTO puesto (empresa_id, descripcion_general, salario, tipo_publicacion, activo)
VALUES (2, 'Se busca desarrollador con experiencia en HTML.', 1200000, 'PUBLICO', TRUE);

INSERT INTO puesto_caracteristica (puesto_id, caracteristica_id, nivel_deseado) VALUES
(1, 8, 4);

INSERT INTO puesto (empresa_id, descripcion_general, salario, tipo_publicacion, activo)
VALUES (2, 'Se busca trabajador con experiencia en MySQL.', 1800000, 'PUBLICO', TRUE);

INSERT INTO puesto_caracteristica (puesto_id, caracteristica_id, nivel_deseado) VALUES
(2, 14, 3);
