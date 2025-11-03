create database banque_prog_2;
\c banque_prog_2;

-- =====================
-- Utilisateurs
-- =====================
CREATE TABLE Utilisateurs (
   id_utilisateur SERIAL PRIMARY KEY,
   nom VARCHAR(50),
   mot_de_passe VARCHAR(50),
   identifiant VARCHAR(50) UNIQUE NOT NULL
);

-- =====================
-- Type de comptes
-- =====================
CREATE TABLE type_comptes (
    type_compte VARCHAR(50) PRIMARY KEY
);

-- =====================
-- Comptes
-- =====================
CREATE TABLE Comptes (
    id_compte SERIAL PRIMARY KEY,
    identifiant VARCHAR(50) NOT NULL REFERENCES Utilisateurs(identifiant),
    type_compte VARCHAR(50) NOT NULL REFERENCES type_comptes(type_compte),
    libelle VARCHAR(50),
    solde NUMERIC(15,2) DEFAULT 0,
    niveau INT
);

CREATE TABLE type_operation(
    type_operation VARCHAR(50) PRIMARY KEY
);

-- =====================
-- Operations
-- =====================
CREATE TABLE Operations (
   id_operation SERIAL PRIMARY KEY,
   identifiant VARCHAR(50) NOT NULL REFERENCES Utilisateurs(identifiant),
   compte VARCHAR(50) NOT NULL REFERENCES type_comptes(type_compte),
   type_operation VARCHAR(50) NOT NULL REFERENCES type_operation(type_operation),
   montant NUMERIC(15,2) NOT NULL,
   date_operation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   details VARCHAR(255)
);
-- =====================
-- Table Pourcentage
-- =====================
CREATE TABLE Pourcentage (
    id_pourcentage SERIAL PRIMARY KEY,
    type_compte VARCHAR(50) NOT NULL REFERENCES type_comptes(type_compte),
    pourcentage DECIMAL(5,2) NOT NULL,
    periode INT NOT NULL
);

--- =====================
-- Table Direction
CREATE TABLE Direction (
    id_direction SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    niveau INTEGER NOT NULL UNIQUE
);

-- Table Utilisateur_Banquier
CREATE TABLE Utilisateur_Banquier (
    id_banquier SERIAL PRIMARY KEY,
    identifiant VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    id_direction INTEGER NOT NULL REFERENCES Direction(id_direction),
    role INTEGER NOT NULL DEFAULT 1
);

-- Table ActionRole
CREATE TABLE ActionRole (
    id_action_role SERIAL PRIMARY KEY,
    nom_table VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    role_necessaire INTEGER NOT NULL,
    UNIQUE(nom_table, action)
);


-- virement
-- Ajouter à votre script existant
CREATE TABLE Virements (
    id_virement SERIAL PRIMARY KEY,
    identifiant_source VARCHAR(50) NOT NULL REFERENCES Utilisateurs(identifiant),
    identifiant_destination VARCHAR(50) NOT NULL REFERENCES Utilisateurs(identifiant),
    montant NUMERIC(15,2) NOT NULL,
    devise VARCHAR(10) DEFAULT 'MGA',
    details VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_execution TIMESTAMP NULL,
    statut INTEGER DEFAULT 1, -- 1:EN_ATTENTE, 11:VALIDE, 21:EXECUTE, 0:ANNULE, -1:REFUSE
    motif_refus VARCHAR(255) NULL,
    created_by VARCHAR(50) NOT NULL -- Banquier qui a créé le virement
);

-- Ajouter un type d'opération pour les virements validés
INSERT INTO type_operation (type_operation) VALUES 
('virement_valide');

-- Mettre à jour les permissions pour les virements
INSERT INTO ActionRole (nom_table, action, role_necessaire) VALUES 
('virement', 'creer', 3),
('virement', 'valider', 2),
('virement', 'executer', 2),
('virement', 'annuler', 2),
('virement', 'refuser', 2);