-- INSERT INTO users (username, password, enabled) VALUES ('user', '$2a$10$wT0XkQY4Z7p2H8q9Y4Z7p2H8q9Y4Z7p2H8q9Y4Z7p2H8q9Y4Z7p2H8q9Y', TRUE); -- Password: password
-- INSERT INTO users (username, password, enabled) VALUES ('admin', '$2a$10$r.2y0yG.Jq5.Z5p.2y0yG.Jq5.Z5p.2y0yG.Jq5.Z5p.2y0yG.Jq5.', TRUE); -- Password: adminpass

INSERT INTO users (username, password, enabled) VALUES ('user', '$2a$10$eWCsj60sW/0GToigKXa42.1isOl.kpI9Kx6heIX3Mdi13719cpCIS', TRUE); -- Password: password
INSERT INTO users (username, password, enabled) VALUES ('admin', '$2a$10$u6xD73xkMJorEkzJUdrvI.5tY71.pkkhg63Q13WUnU2.HNbnn4.fO', TRUE); -- Password: adminpass


INSERT INTO authorities (username, authority) VALUES ('user', 'ROLE_USER');
INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_USER');


--adminpass=$2a$10$u6xD73xkMJorEkzJUdrvI.5tY71.pkkhg63Q13WUnU2.HNbnn4.fO
--userpass=$2a$10$eWCsj60sW/0GToigKXa42.1isOl.kpI9Kx6heIX3Mdi13719cpCIS

