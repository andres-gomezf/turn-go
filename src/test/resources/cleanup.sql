-- Script para limpiar la base de datos después de cada test
-- Se ejecuta en orden inverso para respetar las foreign keys

DELETE FROM turnos;
DELETE FROM horarios;
DELETE FROM canchas;
DELETE FROM clientes;

-- Resetear las secuencias de auto-increment
ALTER SEQUENCE IF EXISTS turnos_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS horarios_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS canchas_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS clientes_id_seq RESTART WITH 1;
