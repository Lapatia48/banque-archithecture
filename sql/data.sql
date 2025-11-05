-- -- Utilisateur
-- INSERT INTO Utilisateurs (nom, mot_de_passe)
-- VALUES ('Lapatia', '1234');

-- INSERT INTO type_comptes (type_compte) VALUES 
-- ('Courant'),
-- ('Epargne'),
-- ('Depot'),
-- ('Pret');

-- INSERT INTO Comptes (id_utilisateur, type_compte, libelle, solde) VALUES
-- (1, 'Courant', 'Compte Courant Lapatia', 500000.00),
-- (1, 'Epargne', 'Compte Epargne Lapatia', 200000.00),
-- (1, 'Depot',   'Compte Depot Lapatia',   100000.00),
-- (1, 'Pret',    'Compte Pret Lapatia',   -150000.00);

-- =====================
-- Utilisateur
-- =====================
INSERT INTO Utilisateurs (nom, mot_de_passe, identifiant)
VALUES ('Lapatia', '1234', 'laplap');

INSERT INTO Utilisateurs (nom, mot_de_passe, identifiant)
VALUES ('Erica', '1234', 'rixrix');

-- =====================
-- Types de comptes
-- =====================
INSERT INTO type_comptes (type_compte) VALUES
('courant'),
('depot'),
('pret');

INSERT INTO type_operation (type_operation) VALUES
('depot'),
('retrait'),
('pret'),
('remboursement'),
('virement');

-- Ajouter un type d'opération pour les virements validés
INSERT INTO type_operation (type_operation) VALUES 
('virement_valide');
-- rollback
INSERT INTO type_operation (type_operation) VALUES 
('rollback_virement');

-- =====================
-- Comptes de Lapatia
-- =====================
INSERT INTO Comptes (identifiant, type_compte, libelle, solde, niveau) VALUES
('laplap', 'courant', 'Compte Courant de Lapatia', 0, 20),
('laplap', 'depot',   'Compte Depot de Lapatia', 0, 30),
('laplap', 'pret',    'Compte Pret de Lapatia', 0, 40);

INSERT INTO Comptes (identifiant, type_compte, libelle, solde, niveau) VALUES
('rixrix', 'courant', 'Compte Courant de Erica', 0, 20),
('rixrix', 'depot',   'Compte Depot de Erica', 0, 30),
('rixrix', 'pret',    'Compte Pret de Erica', 0, 40);


-- Insertion des données initiales pour les prêts
INSERT INTO Pourcentage (type_compte, pourcentage, periode) VALUES
('pret', 10.00, 1),
('depot', 2.00, 1);


INSERT INTO Pourcentage (type_compte, pourcentage, periode) VALUES
('pret', 3.2, 6),   -- 3% tous les 6 mois
('pret', 8.5, 12);  -- 8% tous les 12 mois


-- =====================
-- Insertion des directions
INSERT INTO Direction (libelle, niveau) VALUES 
('Direction Générale', 30),
('Direction Régionale', 25),
('Agence Locale', 10);

-- Insertion d'un administrateur
INSERT INTO Utilisateur_Banquier (identifiant, nom, mot_de_passe, id_direction, role) 
VALUES ('admin', 'Administrateur Principal', 'admin123', 1, 4),
('manager', 'Manager Region 1', 'mdp123', 2, 2),
('agent', 'Agent Agence 1', 'mdp123', 3, 1);

-- Définition des permissions pour le compte courant
INSERT INTO ActionRole (nom_table, action, role_necessaire) VALUES 
('courant', 'depot', 3),
('courant', 'retrait', 3),
('courant', 'virement', 3),
('courant', 'consultation', 3);




-- Mettre à jour les permissions pour les virements
INSERT INTO ActionRole (nom_table, action, role_necessaire) VALUES 
('virement', 'creer', 3),
('virement', 'valider', 2),
('virement', 'executer', 2),
('virement', 'annuler', 2),
('virement', 'refuser', 2);

-- Données d'exemple pour le compte courant
INSERT INTO conf_frais (type_compte, montant_inf, montant_sup, frais_forf, frais_pourc) VALUES 
('courant', 0, 1000, 100, 0),
('courant', 1001, 5000, 200, 1.0),
('courant', 5001, 100000, 300, 2.0),
('courant', 10001, 999999, 1000, 3.0);
