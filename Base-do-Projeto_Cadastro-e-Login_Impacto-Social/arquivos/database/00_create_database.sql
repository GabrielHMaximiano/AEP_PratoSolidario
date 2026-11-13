-- =====================================================================
-- PRATO SOLIDÁRIO — Criação do banco de dados
-- Execute UMA vez, conectado ao banco padrão "postgres":
--   psql -U postgres -f database/00_create_database.sql
-- (Se usar o docker-compose do projeto, este passo NÃO é necessário:
--  o contêiner já cria o banco "prato_solidario".)
-- =====================================================================
CREATE DATABASE prato_solidario
    WITH ENCODING 'UTF8'
    TEMPLATE template0;
