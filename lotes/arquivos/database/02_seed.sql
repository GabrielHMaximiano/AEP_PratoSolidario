-- =====================================================================
-- PRATO SOLIDÁRIO — Dados iniciais para demonstração (DML)
--
-- Idempotente: cada INSERT só acontece se o dado ainda não existir,
-- então o script pode rodar a cada inicialização da aplicação.
-- As datas são relativas a CURRENT_DATE para que os níveis de urgência
-- (crítico / atenção / normal) apareçam sempre, em qualquer dia.
--
-- Senha de TODOS os usuários de exemplo: 123456
-- (armazenada como hash SHA-256 com salt, formato "salt$hash",
--  o mesmo gerado pela classe br.com.pratosolidario.util.SenhaUtil)
-- =====================================================================

-- ---------- Usuários (superclasse) ----------
INSERT INTO usuarios (nome, email, senha_hash, tipo_usuario)
SELECT v.nome, v.email, v.senha_hash, v.tipo_usuario
FROM (VALUES
    (1, 'Padaria da Dona Marta', 'marta@padaria.com',
     'a1b2c3d4e5f60718293a4b5c6d7e8f90$457a2ff654a76ac5a19ce5a4e04e5c54ad717e94712db4dc28d54c03d13c452b', 'DOADOR'),
    (2, 'Hortifruti Bom Preço', 'contato@bompreco.com',
     '0f1e2d3c4b5a69788796a5b4c3d2e1f0$7ba2800631d72e3824c71f116f2f5b3850f1c6cab4030d514f464a01d98d8380', 'DOADOR'),
    (3, 'ONG Mesa Solidária', 'contato@mesasolidaria.org',
     '9a8b7c6d5e4f30211203f4e5d6c7b8a9$1224a5cade4f95051d39a40cdf0c69ef97a8b5be8a11986392b8a8109c0afe54', 'INSTITUICAO'),
    (4, 'Lar São Vicente', 'lar@saovicente.org',
     '5e5e5e5e1a1a1a1a2b2b2b2b3c3c3c3c$bbd14f8d98fad58500e6d0f8867152d3d75da6b76678c6bee564b994104c011d', 'INSTITUICAO')
) AS v (ordem, nome, email, senha_hash, tipo_usuario)
WHERE NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.email = v.email)
ORDER BY v.ordem;

-- ---------- Doadores (subclasse) ----------
INSERT INTO doadores (usuario_id, documento, tipo_estabelecimento, regiao)
SELECT u.id, v.documento, v.tipo_estabelecimento, v.regiao
FROM (VALUES
    ('marta@padaria.com',    '12345678000190', 'Padaria',    'Zona Norte'),
    ('contato@bompreco.com', '98765432000110', 'Hortifruti', 'Centro')
) AS v (email, documento, tipo_estabelecimento, regiao)
JOIN usuarios u ON u.email = v.email
WHERE NOT EXISTS (SELECT 1 FROM doadores d WHERE d.usuario_id = u.id);

-- ---------- Instituições (subclasse) ----------
INSERT INTO instituicoes (usuario_id, area_atuacao, capacidade_atendimento)
SELECT u.id, v.area_atuacao, v.capacidade
FROM (VALUES
    ('contato@mesasolidaria.org', 'Zona Norte', 80),
    ('lar@saovicente.org',        'Centro',     40)
) AS v (email, area_atuacao, capacidade)
JOIN usuarios u ON u.email = v.email
WHERE NOT EXISTS (SELECT 1 FROM instituicoes i WHERE i.usuario_id = u.id);

-- ---------- Lotes de doação (somente se a tabela estiver vazia) ----------
INSERT INTO lotes_doacao (doador_id, tipo_alimento, quantidade_kg, data_validade,
                          retirada_inicio, retirada_fim, status, data_publicacao)
SELECT u.id,
       v.tipo_alimento,
       v.quantidade_kg,
       CURRENT_DATE + v.dias_validade,
       CURRENT_DATE + v.dia_retirada + v.hora_inicio,
       CURRENT_DATE + v.dia_retirada + v.hora_fim,
       v.status,
       CURRENT_DATE + v.dia_publicacao + TIME '08:00'
FROM (VALUES
    -- n, email do doador,       alimento,                 kg,   validade, dia ret., início,       fim,          status,       publicado
    (1, 'marta@padaria.com',    'Pães franceses',          12.50,  1,  0, TIME '18:00', TIME '20:00', 'DISPONIVEL',  0),
    (2, 'marta@padaria.com',    'Bolos e doces',            6.00,  2,  1, TIME '17:00', TIME '19:00', 'DISPONIVEL',  0),
    (3, 'contato@bompreco.com', 'Frutas (banana e maçã)',  25.00,  3,  1, TIME '08:00', TIME '11:00', 'DISPONIVEL',  0),
    (4, 'contato@bompreco.com', 'Legumes variados',        30.00,  6,  2, TIME '08:00', TIME '12:00', 'DISPONIVEL',  0),
    (5, 'contato@bompreco.com', 'Tomates',                 10.00,  1,  0, TIME '14:00', TIME '17:00', 'RESERVADO',   0),
    (6, 'marta@padaria.com',    'Pão de forma',             8.00, -3, -4, TIME '18:00', TIME '20:00', 'RETIRADO',   -5),
    (7, 'contato@bompreco.com', 'Verduras folhosas',       15.00, -5, -6, TIME '16:00', TIME '18:00', 'RETIRADO',   -7)
) AS v (ordem, email, tipo_alimento, quantidade_kg, dias_validade, dia_retirada,
        hora_inicio, hora_fim, status, dia_publicacao)
JOIN usuarios u ON u.email = v.email
WHERE NOT EXISTS (SELECT 1 FROM lotes_doacao)
ORDER BY v.ordem;

-- ---------- Reservas (somente se a tabela estiver vazia) ----------
INSERT INTO reservas (lote_id, instituicao_id, data_reserva, data_retirada,
                      familias_atendidas, status)
SELECT l.id,
       u.id,
       CURRENT_DATE + v.dia_reserva + TIME '10:00',
       CURRENT_DATE + v.dia_retirada + v.hora_retirada,
       v.familias_atendidas,
       v.status
FROM (VALUES
    ('Pão de forma',      'contato@mesasolidaria.org', -5, -4,   TIME '19:00', 20,   'CONCLUIDA'),
    ('Verduras folhosas', 'lar@saovicente.org',        -7, -6,   TIME '17:30', 12,   'CONCLUIDA'),
    ('Tomates',           'lar@saovicente.org',         0, NULL, NULL,         NULL, 'ATIVA')
) AS v (tipo_alimento, email, dia_reserva, dia_retirada, hora_retirada,
        familias_atendidas, status)
JOIN lotes_doacao l ON l.tipo_alimento = v.tipo_alimento
JOIN usuarios u     ON u.email = v.email
WHERE NOT EXISTS (SELECT 1 FROM reservas)
ORDER BY v.dia_reserva;

-- ---------- Notificação de exemplo (RF06) ----------
INSERT INTO notificacoes (instituicao_id, lote_id, mensagem)
SELECT i.usuario_id, l.id,
       'Lote CRÍTICO na região Zona Norte: Pães franceses (12.5 kg) de Padaria da Dona Marta. Retire até amanhã.'
FROM instituicoes i
JOIN lotes_doacao l ON l.tipo_alimento = 'Pães franceses'
WHERE i.area_atuacao = 'Zona Norte'
  AND NOT EXISTS (SELECT 1 FROM notificacoes);
