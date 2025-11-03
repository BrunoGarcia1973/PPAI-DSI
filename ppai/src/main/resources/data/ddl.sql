-- =============================================================================
-- Esquema Red Sísmica – Núcleo de Mantenimiento/Inspecciones (CU 37)
-- PostgreSQL 14+
-- =============================================================================

-- Tipos
CREATE TYPE ambito_estado AS ENUM ('SISMOGRAFO', 'ORDEN_INSPECCION');

-- Tablas maestras
CREATE TABLE rol (
                     id              BIGSERIAL PRIMARY KEY,
                     nombre          TEXT NOT NULL UNIQUE,
                     descripcion_rol TEXT
);

CREATE TABLE empleado (
                          id        BIGSERIAL PRIMARY KEY,
                          nombre    TEXT NOT NULL,
                          apellido  TEXT NOT NULL,
                          mail      TEXT NOT NULL UNIQUE,
                          telefono  TEXT
);

CREATE TABLE usuario (
                         id              BIGSERIAL PRIMARY KEY,
                         nombre_usuario  TEXT NOT NULL UNIQUE,
                         contrasenia     TEXT NOT NULL,          -- almacenar hash
                         empleado_id     BIGINT NOT NULL REFERENCES empleado(id) ON UPDATE CASCADE
);

CREATE TABLE usuario_rol (
                             usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
                             rol_id     BIGINT NOT NULL REFERENCES rol(id) ON DELETE RESTRICT,
                             PRIMARY KEY (usuario_id, rol_id)
);

-- Estaciones y sismógrafos
CREATE TABLE estacion_sismologica (
                                      id                             BIGSERIAL PRIMARY KEY,
                                      codigo_estacion                TEXT NOT NULL UNIQUE,
                                      nombre                         TEXT NOT NULL,
                                      latitud                        NUMERIC(9,6) NOT NULL,
                                      longitud                       NUMERIC(9,6) NOT NULL,
                                      nro_certificacion_adquisicion  TEXT,
                                      documento_certificacion_adq    TEXT,
                                      fecha_solicitud_certificado    DATE
);

CREATE TABLE sismografo (
                            id                       BIGSERIAL PRIMARY KEY,
                            identificador_sismografo TEXT NOT NULL UNIQUE,
                            nro_serie                TEXT NOT NULL,
                            fecha_adquisicion        DATE NOT NULL,
                            estacion_id              BIGINT NOT NULL REFERENCES estacion_sismologica(id) ON UPDATE CASCADE,
                            estado_actual_id         BIGINT                                -- FK a estado, se crea luego
);

-- Estados y cambio de estados
CREATE TABLE estado (
                        id            BIGSERIAL PRIMARY KEY,
                        nombre_estado TEXT NOT NULL,
                        ambito        ambito_estado NOT NULL,
                        UNIQUE (ambito, nombre_estado)
);

-- FK diferida para permitir poblar estados primero
ALTER TABLE sismografo
    ADD CONSTRAINT fk_sismografo_estado_actual
        FOREIGN KEY (estado_actual_id)
            REFERENCES estado(id)
            DEFERRABLE INITIALLY DEFERRED;

-- Orden de Inspección
CREATE TABLE orden_de_inspeccion (
                                     id                       BIGSERIAL PRIMARY KEY,
                                     numero_orden             TEXT NOT NULL UNIQUE,
                                     estacion_id              BIGINT NOT NULL REFERENCES estacion_sismologica(id) ON UPDATE CASCADE,
                                     responsable_inspeccion_id BIGINT NOT NULL REFERENCES empleado(id) ON UPDATE CASCADE,
                                     fecha_hora_inicio        TIMESTAMPTZ,
                                     fecha_hora_finalizacion  TIMESTAMPTZ,
                                     fecha_hora_cierre        TIMESTAMPTZ,
                                     observacion_cierre       TEXT,
                                     estado_actual_id         BIGINT REFERENCES estado(id) DEFERRABLE INITIALLY DEFERRED
);

-- Historial de estados (sismógrafo y OI)
CREATE TABLE cambio_estado (
                               id                 BIGSERIAL PRIMARY KEY,
                               ambito             ambito_estado NOT NULL,
                               sismografo_id      BIGINT REFERENCES sismografo(id) ON DELETE CASCADE,
                               orden_inspeccion_id BIGINT REFERENCES orden_de_inspeccion(id) ON DELETE CASCADE,
                               estado_id          BIGINT NOT NULL REFERENCES estado(id),
                               fecha_hora_inicio  TIMESTAMPTZ NOT NULL,
                               fecha_hora_fin     TIMESTAMPTZ,
                               CHECK (
                                   (ambito = 'SISMOGRAFO' AND sismografo_id IS NOT NULL AND orden_inspeccion_id IS NULL)
                                       OR
                                   (ambito = 'ORDEN_INSPECCION' AND orden_inspeccion_id IS NOT NULL AND sismografo_id IS NULL)
                                   )
);

-- Motivos de fuera de servicio
CREATE TABLE motivo_tipo (
                             id          BIGSERIAL PRIMARY KEY,
                             descripcion TEXT NOT NULL UNIQUE
);

-- Selección de motivos al poner fuera de servicio
CREATE TABLE motivo_fuera_servicio (
                                       id               BIGSERIAL PRIMARY KEY,
                                       cambio_estado_id BIGINT NOT NULL REFERENCES cambio_estado(id) ON DELETE CASCADE,
                                       motivo_tipo_id   BIGINT NOT NULL REFERENCES motivo_tipo(id) ON DELETE RESTRICT,
                                       comentario       TEXT,
                                       UNIQUE (cambio_estado_id, motivo_tipo_id)
);

-- Índices útiles
CREATE INDEX idx_sismografo_estacion ON sismografo(estacion_id);
CREATE INDEX idx_oi_estacion ON orden_de_inspeccion(estacion_id);
CREATE INDEX idx_oi_responsable ON orden_de_inspeccion(responsable_inspeccion_id);
CREATE INDEX idx_cambio_estado_sismo ON cambio_estado(sismografo_id);
CREATE INDEX idx_cambio_estado_oi ON cambio_estado(orden_inspeccion_id);
CREATE INDEX idx_cambio_estado_estado ON cambio_estado(estado_id);

-- =============================================================================
-- DATOS INICIALES (DATOS MAESTROS)
-- =============================================================================

-- Estados para Sismógrafos
INSERT INTO estado (nombre_estado, ambito) VALUES
  ('EN_LINEA', 'SISMOGRAFO'),
  ('INHABILITADO_POR_INSPECCION', 'SISMOGRAFO'),
  ('FUERA_DE_SERVICIO', 'SISMOGRAFO'),
  ('EN_MANTENIMIENTO', 'SISMOGRAFO'),
  ('ABIERTA', 'SISMOGRAFO')
ON CONFLICT (ambito, nombre_estado) DO NOTHING;

-- Estados para Órdenes de Inspección
INSERT INTO estado (nombre_estado, ambito) VALUES
  ('PENDIENTE_DE_REALIZACION', 'ORDEN_INSPECCION'),
  ('PARCIALMENTE_REALIZADA', 'ORDEN_INSPECCION'),
  ('COMPLETAMENTE_REALIZADA', 'ORDEN_INSPECCION'),
  ('ABIERTA', 'ORDEN_INSPECCION'),
  ('CERRADA', 'ORDEN_INSPECCION')
ON CONFLICT (ambito, nombre_estado) DO NOTHING;

-- Roles del sistema
INSERT INTO rol (nombre, descripcion_rol) VALUES
  ('RESPONSABLE_DE_INSPECCION', 'Responsable de las inspecciones de sismógrafos'),
  ('TECNICO', 'Técnico encargado del mantenimiento'),
  ('ADMINISTRADOR', 'Administrador del sistema'),
  ('SISTEMA', 'Usuario para operaciones del sistema')
ON CONFLICT (nombre) DO NOTHING;

-- Motivos de tipo para fuera de servicio
INSERT INTO motivo_tipo (descripcion) VALUES
  ('Mantenimiento'),
  ('Calibracion'),
  ('Falla de Sensor'),
  ('Falta de presión'),
  ('Daño mecánico'),
  ('Problema de software'),
  ('Falla de hardware'),
  ('Acto de vandalismo')
ON CONFLICT (descripcion) DO NOTHING;
