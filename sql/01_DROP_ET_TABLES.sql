/* 
   SCRIPT 1/4 : SUPPRESSION ET CREATION DES TABLES
   Ce fichier cree toutes les tables de la base de donnees de gestion locative.
   A executer en premier avant les autres scripts.
*/

/* Suppression des tables existantes dans l'ordre inverse des dependances */
DROP TABLE HISTORIQUE_ACTIVITE CASCADE CONSTRAINTS;
DROP TABLE HISTORIQUE_LOYERS CASCADE CONSTRAINTS;
DROP TABLE PREFERENCES CASCADE CONSTRAINTS;
DROP TABLE Ajoute CASCADE CONSTRAINTS;
DROP TABLE Signe CASCADE CONSTRAINTS;
DROP TABLE Mouvement_Caution CASCADE CONSTRAINTS;
DROP TABLE Echeance CASCADE CONSTRAINTS;
DROP TABLE Assurance CASCADE CONSTRAINTS;
DROP TABLE Garage CASCADE CONSTRAINTS;
DROP TABLE Diagnostique CASCADE CONSTRAINTS;
DROP TABLE Logement CASCADE CONSTRAINTS;
DROP TABLE Facture CASCADE CONSTRAINTS;
DROP TABLE Regularisation CASCADE CONSTRAINTS;
DROP TABLE Loyer CASCADE CONSTRAINTS;
DROP TABLE EtatDesLieux CASCADE CONSTRAINTS;
DROP TABLE Charges CASCADE CONSTRAINTS;
DROP TABLE DeclarationRevenusFoncier CASCADE CONSTRAINTS;
DROP TABLE Proprietaire CASCADE CONSTRAINTS;
DROP TABLE Batiment CASCADE CONSTRAINTS;
DROP TABLE Bail CASCADE CONSTRAINTS;
DROP TABLE Releve_Compteur CASCADE CONSTRAINTS;
DROP TABLE Garant CASCADE CONSTRAINTS;
DROP TABLE Locataire CASCADE CONSTRAINTS;
DROP TABLE Entreprise CASCADE CONSTRAINTS;

/* Suppression des sequences */
BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_HISTORIQUE_LOYERS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_HISTORIQUE_ACTIVITE';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

/* Suppression du type objet */
BEGIN
   EXECUTE IMMEDIATE 'DROP TYPE T_RECAP_FISCAL';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

/* ====================== TABLES DE BASE (SANS DEPENDANCES) ====================== */

/* Table des entreprises prestataires (plombiers, electriciens, etc.) */
CREATE TABLE Entreprise (
   Siret NUMBER(14) PRIMARY KEY,
   Nom_entreprise VARCHAR2(50),
   Adresse_Entreprise VARCHAR2(50), 
   Specialite VARCHAR2(50),
   ADRESSE VARCHAR2(255),
   TELEPHONE VARCHAR2(20),
   EMAIL VARCHAR2(100)
);

/* Table des locataires - contient les informations personnelles */
CREATE TABLE Locataire (
   id_locataire NUMBER(22) PRIMARY KEY,
   Nom_loc VARCHAR2(50) NOT NULL,
   Prenom_loc VARCHAR2(50) NOT NULL,
   Tel_Loc VARCHAR2(20),
   Mail_loc VARCHAR2(50),
   DATE_NAISSANCE DATE
);

/* Table des garants - personnes qui se portent caution pour un locataire */
CREATE TABLE Garant (
   id_Garant VARCHAR2(50) PRIMARY KEY,
   Nom_garant VARCHAR2(50) NOT NULL,
   Prenom_garant VARCHAR2(50),
   Adresse_garant VARCHAR2(50),
   Tel_garant VARCHAR2(20),
   Mail_garant VARCHAR2(50)
);

/* 
   Table des releves de compteur (eau, electricite, gaz)
   Modifiee pour permettre plusieurs releves par logement/garage
   Les quotites permettent de repartir les charges entre plusieurs locataires
*/
CREATE TABLE Releve_Compteur (
   id_releve NUMBER(22) PRIMARY KEY,
   Unite VARCHAR2(50),
   Type VARCHAR2(50),
   Nouvelle_index NUMBER(22),
   Date_ DATE,
   Num_Compteur NUMBER(22),
   Ancien_Index NUMBER(22),
   ID_LOGEMENT NUMBER(22),
   ID_GARAGE NUMBER(22),
   QUOTITE_EAU NUMBER(5,2) DEFAULT 100,
   QUOTITE_ELECTRICITE NUMBER(5,2) DEFAULT 100,
   QUOTITE_OM NUMBER(5,2) DEFAULT 100,
   QUOTITE_ENTRETIEN NUMBER(5,2) DEFAULT 100
);

/* 
   Table des baux - contrat de location entre proprietaire et locataire
   DATE_DERNIERE_REVALORISATION permet de controler qu'on ne revalorise pas plus d'une fois par an
*/
CREATE TABLE Bail (
   Id_bail NUMBER(22) PRIMARY KEY,
   date_debut DATE NOT NULL,
   date_fin DATE,
   Loyer_Initial NUMBER(15,2) NOT NULL,
   provision_Initiales NUMBER(15,2) DEFAULT 0 NOT NULL,
   Solde_tout_compte NUMBER(15,2),
   Caution NUMBER(22),
   etat VARCHAR2(20) DEFAULT 'EN_COURS' NOT NULL,
   Jour_paiement NUMBER(2),
   DATE_DERNIERE_REVALORISATION DATE,
   CONSTRAINT CK_BAIL_ETAT CHECK (etat IN ('EN_COURS', 'RESILIE', 'CLOTURE')),
   CONSTRAINT CK_BAIL_DATE_FIN_COHERENCE CHECK (etat = 'EN_COURS' OR date_fin IS NOT NULL)
);

/* 
   Table des batiments - peut etre une maison ou un immeuble
   RUE represente la ville (nom mal choisi mais garde pour compatibilite)
*/
CREATE TABLE Batiment (
   id_Batiment NUMBER(22) PRIMARY KEY,
   Adresse VARCHAR2(50) NOT NULL,
   Nombre_d_etage NUMBER(22),
   Type_Batiment VARCHAR2(50),
   Rue VARCHAR2(50) NOT NULL,
   Code_postale NUMBER(5) NOT NULL
);

/* ====================== TABLES AVEC DEPENDANCES ====================== */

/* Table des proprietaires - lie a un batiment */
CREATE TABLE Proprietaire (
   Id_propietaire NUMBER(22) PRIMARY KEY,
   Nom_Prop VARCHAR2(50) NOT NULL,
   Prenom_Prop VARCHAR2(50) NOT NULL,
   Adresse_Prop VARCHAR2(50),
   Tel_Prop VARCHAR2(20),
   Mail_Prop VARCHAR2(50),
   id_Batiment NUMBER(22) NOT NULL,
   CONSTRAINT FK_PROPRIETAIRE_BATIMENT FOREIGN KEY (id_Batiment) REFERENCES Batiment(id_Batiment)
);

/* Table des declarations de revenus fonciers - pour la fiscalite */
CREATE TABLE DeclarationRevenusFoncier (
   id_declaration NUMBER(22) PRIMARY KEY,
   Annee NUMBER(4),
   Regime VARCHAR2(50),
   Total_recette NUMBER(15,2),
   Total_Charges_Deductible NUMBER(15,2),
   Resultat NUMBER(15,2),
   Id_proprietaire NUMBER(22),
   CONSTRAINT FK_DECLARATION_PROP FOREIGN KEY (Id_proprietaire) REFERENCES Proprietaire(Id_propietaire)
);

/* 
   Table des charges - eau, electricite, ordures menageres, entretien
   PCT_RECUPERABLE indique le pourcentage recuperable sur le locataire
*/
CREATE TABLE Charges (
   id_charge NUMBER(22) PRIMARY KEY,
   Nature VARCHAR2(50) NOT NULL,
   Montant NUMBER(15,2),
   Date_charge DATE,
   Type_Charges VARCHAR2(50) NOT NULL,
   Pct_recuperable NUMBER(5,2) DEFAULT 100 NOT NULL,
   id_Batiment NUMBER(22) NOT NULL,
   CONSTRAINT FK_CHARGE_BATIMENT FOREIGN KEY (id_Batiment) REFERENCES Batiment(id_Batiment)
);

/* Table des etats des lieux - entree et sortie */
CREATE TABLE EtatDesLieux (
   id_edl NUMBER(22) PRIMARY KEY,
   type VARCHAR2(50),
   date_edl DATE,
   observations VARCHAR2(500),
   Id_bail NUMBER(22) NOT NULL,
   CONSTRAINT FK_EDL_BAIL FOREIGN KEY (Id_bail) REFERENCES Bail(Id_bail)
);

/* 
   Table des loyers mensuels
   Un loyer est cree pour chaque mois, avec un statut de paiement
*/
CREATE TABLE Loyer (
   Id_loyer NUMBER(22) PRIMARY KEY,
   Quittance VARCHAR2(50),
   Montant_loyer NUMBER(15,2) NOT NULL,
   Mois CHAR(15) NOT NULL,
   Montant_provision NUMBER(15,2),
   Statut VARCHAR2(50),
   Indice_IRL NUMBER(22),
   ancien_IRL NUMBER(22),
   Date_paiement DATE,
   Id_bail NUMBER(22) NOT NULL,
   CONSTRAINT FK_LOYER_BAIL FOREIGN KEY (Id_bail) REFERENCES Bail(Id_bail)
);

/* Table des regularisations de charges - calcul annuel charges reelles vs provisions */
CREATE TABLE Regularisation (
   Id_Regularisation NUMBER(22) PRIMARY KEY,
   Type VARCHAR2(50),
   Total_charge_reelles NUMBER(15,2),
   Total_provisions NUMBER(15,2),
   Date_ DATE,
   Delta NUMBER(15,2),
   mode_compensation VARCHAR2(50),
   Reste_a_charge NUMBER(15,2),
   Nouvelle_provision NUMBER(15,2),
   Annee_concernee NUMBER(4),
   id_charge NUMBER(22) NOT NULL,
   Id_bail NUMBER(22) NOT NULL,
   CONSTRAINT FK_REGULARISATION_CHARGE FOREIGN KEY (id_charge) REFERENCES Charges(id_charge),
   CONSTRAINT FK_REGULARISATION_BAIL FOREIGN KEY (Id_bail) REFERENCES Bail(Id_bail)
);

/* 
   Table des logements - appartements ou maisons
   NUM_FISCAL est UNIQUE pour eviter les doublons
*/
CREATE TABLE Logement (
   Id_Logement NUMBER(22) PRIMARY KEY,
   Type_Logement VARCHAR2(50),
   Adresse_Logement VARCHAR2(50),
   Complement_Adresse VARCHAR(50),
   Num_Fiscal NUMBER(22) NOT NULL UNIQUE,
   Surface NUMBER(10,2) NOT NULL,
   Nb_Pieces NUMBER(22) NOT NULL,
   id_releve NUMBER(22),
   id_Batiment NUMBER(22) NOT NULL,
   Id_bail NUMBER(22),
   CONSTRAINT FK_LOGEMENT_RELEVE FOREIGN KEY (id_releve) REFERENCES Releve_Compteur(id_releve),
   CONSTRAINT FK_LOGEMENT_BATIMENT FOREIGN KEY (id_Batiment) REFERENCES Batiment(id_Batiment),
   CONSTRAINT FK_LOGEMENT_BAIL FOREIGN KEY (Id_bail) REFERENCES Bail(Id_bail)
);

/* 
   Table des factures - travaux, charges, etc.
   RECUPERABLE_LOCATAIRE et DEDUCTIBLE_IMPOT sont mutuellement exclusifs
*/
CREATE TABLE Facture (
   Id_facture NUMBER(22) PRIMARY KEY,
   Accompte NUMBER(15,2),
   Nature VARCHAR2(50),
   Periode_Deb VARCHAR2(50),
   Date_emission DATE NOT NULL,
   Montant_HT NUMBER(15,2),
   Montant_TTC NUMBER(15,2) NOT NULL,
   Montant_TEOM NUMBER(15,2),
   Recuperable_locataire NUMBER(1),
   Deductible_Impot NUMBER(1),
   Periode_Fin DATE,
   Travaux VARCHAR2(50),
   DateDevis DATE,
   MontantDevis NUMBER(15,2),
   Siret NUMBER(14) NOT NULL,
   id_charge NUMBER(22),
   id_Batiment NUMBER(22),
   STATUT_PAIEMENT VARCHAR2(20) DEFAULT 'A payer',
   Id_Bail NUMBER(22),
   ID_LOGEMENT NUMBER(22),
   CONSTRAINT FK_FACTURE_ENTREPRISE FOREIGN KEY (Siret) REFERENCES Entreprise(Siret),
   CONSTRAINT FK_FACTURE_CHARGE FOREIGN KEY (id_charge) REFERENCES Charges(id_charge),
   CONSTRAINT FK_FACTURE_BATIMENT FOREIGN KEY (id_Batiment) REFERENCES Batiment(id_Batiment),
   CONSTRAINT FK_FACTURE_BAIL FOREIGN KEY (Id_Bail) REFERENCES Bail(Id_bail),
   CONSTRAINT FK_FACTURE_LOGEMENT FOREIGN KEY (ID_LOGEMENT) REFERENCES Logement(Id_Logement),
   CONSTRAINT CK_FACTURE_TRAVAUX_DEVIS CHECK (Travaux IS NULL OR MontantDevis IS NOT NULL)
);

/* Table des diagnostics obligatoires - DPE, electricite, gaz, etc. */
CREATE TABLE Diagnostique (
   id_diagnostique NUMBER(22) PRIMARY KEY,
   Type_Diag VARCHAR2(50),
   Date_Emission DATE,
   Date_Expiration DATE,
   Reference VARCHAR2(50),
   Id_Logement NUMBER(22),
   CONSTRAINT FK_DIAG_LOGEMENT FOREIGN KEY (Id_Logement) REFERENCES Logement(Id_Logement)
);

/* 
   Table des garages - peut etre lie a un logement ou loue separement
   NUM_FISCAL est UNIQUE pour eviter les doublons
*/
CREATE TABLE Garage (
   Id_Garage NUMBER(22) PRIMARY KEY,
   Surface_Garage NUMBER(22),
   Num_Fiscal NUMBER(22) NOT NULL UNIQUE,
   Adresse_Garage VARCHAR2(50),
   Complement_Adresse VARCHAR(50),
   id_Batiment NUMBER(22) NOT NULL,
   Id_Logement NUMBER(22),
   Id_bail NUMBER(22),
   id_releve NUMBER(22),
   CONSTRAINT FK_GARAGE_BATIMENT FOREIGN KEY (id_Batiment) REFERENCES Batiment(id_Batiment),
   CONSTRAINT FK_GARAGE_LOGEMENT FOREIGN KEY (Id_Logement) REFERENCES Logement(Id_Logement),
   CONSTRAINT FK_GARAGE_BAIL FOREIGN KEY (Id_bail) REFERENCES Bail(Id_bail),
   CONSTRAINT FK_GARAGE_RELEVE FOREIGN KEY (id_releve) REFERENCES Releve_Compteur(id_releve)
);

/* Ajout des FK sur RELEVE_COMPTEUR maintenant que LOGEMENT et GARAGE existent */
ALTER TABLE Releve_Compteur ADD CONSTRAINT FK_RELEVE_LOGEMENT 
    FOREIGN KEY (ID_LOGEMENT) REFERENCES Logement(Id_Logement);
ALTER TABLE Releve_Compteur ADD CONSTRAINT FK_RELEVE_GARAGE 
    FOREIGN KEY (ID_GARAGE) REFERENCES Garage(Id_Garage);

/* 
   Table des assurances - PNO, GLI, multirisque, etc.
   Peut etre liee a un logement, garage ou batiment
*/
CREATE TABLE Assurance (
   numAssurance NUMBER(22) PRIMARY KEY,
   Type VARCHAR2(50),
   primeBase NUMBER(15,2),
   Nom_Compagnie VARCHAR2(100),
   Date_Effet DATE,
   Id_Logement NUMBER(22),
   Id_Garage NUMBER(22),
   Siret NUMBER(14),
   ID_BATIMENT NUMBER(22),
   CONSTRAINT FK_ASSURANCE_LOGEMENT FOREIGN KEY (Id_Logement) REFERENCES Logement(Id_Logement),
   CONSTRAINT FK_ASSURANCE_GARAGE FOREIGN KEY (Id_Garage) REFERENCES Garage(Id_Garage),
   CONSTRAINT FK_ASSURANCE_ENTREPRISE FOREIGN KEY (Siret) REFERENCES Entreprise(Siret),
   CONSTRAINT FK_ASSURANCE_BATIMENT FOREIGN KEY (ID_BATIMENT) REFERENCES Batiment(id_Batiment),
   CONSTRAINT CK_ASSURANCE_TYPE CHECK (Type IS NULL OR UPPER(Type) IN (
       'PROPRIETAIRE', 
       'AIDE JURIDIQUE', 
       'AIDE_JURIDIQUE',
       'PNO',
       'MULTIRISQUE_IMMEUBLE',
       'GLI'
   ))
);

/* Table des echeances d'assurance - dates et montants a payer */
CREATE TABLE Echeance (
   numAssurance NUMBER(22),
   Date_ DATE,
   Montant NUMBER(22),
   PRIMARY KEY (numAssurance, Date_),
   CONSTRAINT FK_ECHEANCE_ASSURANCE FOREIGN KEY (numAssurance) REFERENCES Assurance(numAssurance)
);

/* 
   Table des mouvements de caution - versements, restitutions, retenues
   Permet de tracer l'historique complet de la caution
*/
CREATE TABLE Mouvement_Caution (
    id_mouvement NUMBER(22) PRIMARY KEY,
    date_mouvement DATE NOT NULL,
    type_mouvement VARCHAR2(50) NOT NULL,
    montant NUMBER(15,2) NOT NULL,
    moyen_paiement VARCHAR2(50),
    observations VARCHAR2(255),
    id_bail NUMBER(22) NOT NULL,
    CONSTRAINT FK_MVT_CAUTION_BAIL FOREIGN KEY (id_bail) REFERENCES Bail(Id_bail),
    CONSTRAINT CK_TYPE_MVT CHECK (type_mouvement IN ('Versement', 'Restitution', 'Retenue'))
);

/* ====================== TABLES D'ASSOCIATION ====================== */

/* Table de liaison bail-locataire - un bail peut avoir plusieurs locataires */
CREATE TABLE Signe (
   Id_bail NUMBER(22),
   id_locataire NUMBER(22),
   PRIMARY KEY (Id_bail, id_locataire),
   CONSTRAINT FK_SIGNE_BAIL FOREIGN KEY (Id_bail) REFERENCES Bail(Id_bail),
   CONSTRAINT FK_SIGNE_LOCATAIRE FOREIGN KEY (id_locataire) REFERENCES Locataire(id_locataire)
);

/* Table de liaison bail-garant - un bail peut avoir plusieurs garants */
CREATE TABLE Ajoute (
   Id_bail NUMBER(22),
   id_Garant VARCHAR2(50),
   PRIMARY KEY (Id_bail, id_Garant),
   CONSTRAINT FK_AJOUTE_BAIL FOREIGN KEY (Id_bail) REFERENCES Bail(Id_bail),
   CONSTRAINT FK_AJOUTE_GARANT FOREIGN KEY (id_Garant) REFERENCES Garant(id_Garant)
);

/* Table des preferences utilisateur - mode sombre, notifications, etc. */
CREATE TABLE PREFERENCES (
    ID_LOCATAIRE NUMBER(22) NOT NULL,
    MODE_SOMBRE NUMBER(1) DEFAULT 0,
    NOTIF_EMAIL NUMBER(1) DEFAULT 1,
    RAPPEL_LOYER NUMBER(1) DEFAULT 0,
    CONSTRAINT PK_PREFERENCES PRIMARY KEY (ID_LOCATAIRE),
    CONSTRAINT FK_PREFS_LOCATAIRE FOREIGN KEY (ID_LOCATAIRE) REFERENCES Locataire(id_locataire)
);

/* ====================== TABLES D'HISTORIQUE ====================== */

/* 
   Table d'archivage des loyers des baux clotures
   Permet de conserver les statistiques meme apres cloture du bail
*/
CREATE TABLE HISTORIQUE_LOYERS (
    ID_HISTORIQUE       NUMBER(22) PRIMARY KEY,
    ID_BAIL             NUMBER(22) NOT NULL,
    ID_LOGEMENT         NUMBER(22),
    ID_GARAGE           NUMBER(22),
    NOM_BIEN            VARCHAR2(200),
    NOM_LOCATAIRE       VARCHAR2(200),
    MOIS                VARCHAR2(15),
    ANNEE               NUMBER(4),
    MONTANT_LOYER       NUMBER(10,2),
    MONTANT_PROVISION   NUMBER(10,2),
    DATE_PAIEMENT       DATE,
    QUITTANCE_GENEREE   CHAR(1) DEFAULT 'N',
    DATE_CLOTURE        DATE DEFAULT SYSDATE,
    ID_BATIMENT         NUMBER(22),
    CONSTRAINT CK_HIST_QUITTANCE CHECK (QUITTANCE_GENEREE IN ('O', 'N'))
);

CREATE SEQUENCE SEQ_HISTORIQUE_LOYERS START WITH 1 INCREMENT BY 1;

/* 
   Table d'historique des activites recentes
   Alimentee automatiquement par des triggers pour le tableau de bord
*/
CREATE TABLE HISTORIQUE_ACTIVITE (
    ID_ACTIVITE         NUMBER(22) PRIMARY KEY,
    TYPE_ACTIVITE       VARCHAR2(50) NOT NULL,
    DESCRIPTION_ACTIVITE VARCHAR2(500),
    DATE_ACTIVITE       TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    ENTITE_TYPE         VARCHAR2(50),
    ENTITE_ID           NUMBER(22),
    MONTANT             NUMBER(15,2),
    ADRESSE             VARCHAR2(200)
);

CREATE SEQUENCE SEQ_HISTORIQUE_ACTIVITE START WITH 1 INCREMENT BY 1;

/* ====================== INDEX POUR PERFORMANCES ====================== */

/* Index sur les cles etrangeres les plus utilisees */
CREATE INDEX IDX_LOGEMENT_BATIMENT ON LOGEMENT(ID_BATIMENT);
CREATE INDEX IDX_GARAGE_BATIMENT ON GARAGE(ID_BATIMENT);
CREATE INDEX IDX_SIGNE_LOCATAIRE ON SIGNE(ID_LOCATAIRE);
CREATE INDEX IDX_SIGNE_BAIL ON SIGNE(ID_BAIL);
CREATE INDEX IDX_LOYER_BAIL ON LOYER(ID_BAIL);
CREATE INDEX IDX_CHARGES_BATIMENT ON CHARGES(ID_BATIMENT);
CREATE INDEX IDX_FACTURE_LOGEMENT ON FACTURE(ID_LOGEMENT);
CREATE INDEX IDX_ASSURANCE_BATIMENT ON ASSURANCE(ID_BATIMENT);

/* Index sur l'historique pour les recherches par bail/annee/batiment */
CREATE INDEX IDX_HIST_BAIL ON HISTORIQUE_LOYERS(ID_BAIL);
CREATE INDEX IDX_HIST_ANNEE ON HISTORIQUE_LOYERS(ANNEE);
CREATE INDEX IDX_HIST_BATIMENT ON HISTORIQUE_LOYERS(ID_BATIMENT);

/* Index sur les releves compteur pour les nouvelles liaisons */
CREATE INDEX IDX_RELEVE_LOGEMENT ON RELEVE_COMPTEUR(ID_LOGEMENT);
CREATE INDEX IDX_RELEVE_GARAGE ON RELEVE_COMPTEUR(ID_GARAGE);

COMMIT;

/* FIN DU SCRIPT 01_DROP_ET_TABLES.sql */
