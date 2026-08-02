INSERT INTO Usuario (id, email, password, rol, activo, nombre, oro, expedicionActual, mazmorraActual, inventario_id)
VALUES
  (1, 'test@unlam.edu.ar', 'test', 'ADMIN', TRUE,  'Héroe',      1000, 1, 3, null),
  (2, 'otro@ejemplo.com',  '1234', 'ADMIN', TRUE,  'Aventurero',  500, 1, 2, null),
  (3, 'jugador3@game.com', '123',  'USER',  TRUE,  'Usuario 3', 1000, 3, 1, null),
  (4, 'jugador4@game.com', '123',  'USER',  TRUE,  'Usuario 4', 3000, 2, 3, null);


INSERT INTO game_session (usuario_id, turno, nivel, active, finished, started_at, ended_at)
VALUES
  (1, 1, 3, 1, 0, '2025-06-24 13:00:00', NULL),
  (2, 1, 2, 1, 0, '2025-06-24 13:05:00', NULL),
  (3, 1, 1, 1, 0, '2025-06-24 13:10:00', NULL),
  (4, 1, 3, 1, 0, '2025-06-24 13:15:00', NULL);

INSERT INTO expedition (session_id, number, completed)
VALUES (1, 2, FALSE), (2, 3, FALSE), (3, 3, FALSE), (4, 1, FALSE);


INSERT INTO heroe
  (id, nombre, nivel, precio, url_imagen, max_vida, atk, defensa_base)
VALUES
  (1, 'Cruzado', 1, 300, '/imagenes/cruzado.webp', 300, 35, 5),
  (2, 'Vestal',   1, 200, '/imagenes/Vestal.webp',   200, 25, 8),
  (3, 'Cazador', 1, 200 ,'/imagenes/Brigand_Hunter.webp', 250, 30, 10),
  (4, 'HeroeDios', 1, 200 ,'/imagenes/heroeDios.webp', 800, 1000, 1000);

INSERT INTO monster (id, nombre, vida, atk, imagen) VALUES
  (5, 'Orco',       60, 10, '&#x1F9DD;&#x200D;&#x2642;&#xFE0F;'),
  (6, 'Mago',       35, 12, '&#x1F9D9;&#x200D;&#x2642;&#xFE0F;'),
  (7, 'Vampiro',    55, 14, '&#x1F9DB;&#x200D;&#x2642;&#xFE0F;'),
  (8, 'Golem',      80,  7, '&#x1F9D9;&#x200D;&#x2640;&#xFE0F;'),
  (9, 'Dragón',     100,20, '&#x1F409;'),
  (10,'Demonio',    70, 18, '&#x1F47F;');


INSERT INTO Item (id, nombre, tipo, precio, imagenUrl) VALUES
(1, 'Poción de Vida', 'POCION', 100, '/imagenes/posion2.webp'),
(2, 'Espada Corta', 'ARMA', 150, '/imagenes/espada_equipamiento.webp');



-- estado de monstruos en la mazmorra 1 de la expedición 1
INSERT INTO session_monster
  (session_id, monster_id, vida_actual, expedition_number, dungeon_number, orden)
VALUES
  (1, 5,  60, 1, 1, 1),  -- Orco
  (1, 6,  35, 1, 1, 2),  -- Mago
  (1, 7,  55, 1, 1, 3);  -- Vampiro

