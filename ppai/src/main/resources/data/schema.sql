CREATE OR REPLACE FUNCTION random_datetime(start_date TIMESTAMP, end_date TIMESTAMP)
RETURNS TIMESTAMP AS $$
BEGIN
RETURN start_date + (end_date - start_date) * random();
END;
$$ LANGUAGE plpgsql;

-- INSERTAR REGISTROS EN SISMOGRAFO (Estado inicial: 3)
INSERT INTO public.sismografo (id, fecha_adquisicion, identificador_sismografo, nro_serie, estacion_id, estado_actual_id)
VALUES
    (10, NOW(), 'S-10', '500', 1, 3),
    (11, '2024-05-15', 'S-11', '501', 2, 3),
    (12, NOW(), 'S-12', '502', 1, 3),
    (13, '2025-10-01', 'S-13', '503', 2, 3),
    (14, NOW(), 'S-14', '504', 1, 3);