/* 
   SCRIPT 4/4 : TRIGGERS ET CONTRAINTES (REGLES METIER)
   Ce fichier implemente les 15 regles metier du sujet + les triggers d'historique.
   A executer en dernier apres 03_VUES.sql
*/

/* ====================== REGLE 1 : Revalorisation loyers/provisions ====================== */
/* Geree par l'application Java - au moins un des deux montants doit etre modifie */

/* ====================== REGLE 2 : Coherence taxes foncieres ====================== */
/* Le montant total doit etre superieur a la taxe ordures menageres */

CREATE OR REPLACE TRIGGER TRG_COHERENCE_TAXE_FONCIERE
BEFORE INSERT OR UPDATE ON FACTURE
FOR EACH ROW
WHEN (NEW.Montant_TEOM IS NOT NULL AND NEW.Montant_TTC IS NOT NULL)
BEGIN
    IF :NEW.Montant_TTC <= :NEW.Montant_TEOM THEN
        RAISE_APPLICATION_ERROR(-20102, 
            'Le montant total de la taxe fonciere (' || 
            :NEW.Montant_TTC || ' EUR) doit etre strictement superieur ' ||
            'a la taxe des ordures menageres (' || :NEW.Montant_TEOM || ' EUR).');
    END IF;
END;
/

/* ====================== REGLE 3 : Type assurance ====================== */
/* Contrainte CHECK dans 01_DROP_ET_TABLES.sql - PROPRIETAIRE ou AIDE JURIDIQUE */

/* ====================== REGLE 4 : Exclusivite recuperable/deductible ====================== */
/* Une facture ne peut pas etre les deux a la fois (XOR) */

CREATE OR REPLACE TRIGGER TRG_FACTURE_RECUP_EXCLUSIF
BEFORE INSERT OR UPDATE ON FACTURE
FOR EACH ROW
BEGIN
    IF :NEW.Recuperable_locataire = 1 AND :NEW.Deductible_Impot = 1 THEN
        RAISE_APPLICATION_ERROR(-20104, 
            'Une facture ne peut pas etre a la fois ' ||
            'recuperable sur le locataire ET deductible des impots. ' ||
            'Choisissez l''une ou l''autre option.');
    END IF;
END;
/

/* ====================== REGLES 5-6 : Heritage Bien/Louable ====================== */
/* Gerees par la structure des tables - un batiment peut avoir logements OU garages */

/* ====================== REGLE 7 : Revalorisation selon IRL ====================== */
/* Geree par la procedure revaloriserLoyer dans 02_FONCTIONS_PROCEDURES.sql */

/* ====================== REGLE 8 : Solde de tout compte ====================== */
/* Geree par la procedure cloturerBail dans 02_FONCTIONS_PROCEDURES.sql */

/* ====================== REGLES 9-10 : Regularisation prorata ====================== */
/* Gerees par les fonctions de calcul dans 02_FONCTIONS_PROCEDURES.sql */

/* ====================== REGLE 11 : Index compteur croissant ====================== */
/* Le nouvel index doit etre >= ancien index */

CREATE OR REPLACE TRIGGER TRG_VERIF_ANCIEN_INDEX
BEFORE INSERT OR UPDATE ON RELEVE_COMPTEUR
FOR EACH ROW
BEGIN
    IF :NEW.Ancien_Index IS NOT NULL AND :NEW.Nouvelle_index IS NOT NULL THEN
        IF :NEW.Nouvelle_index < :NEW.Ancien_Index THEN
            RAISE_APPLICATION_ERROR(-20111, 
                'Le nouvel index (' || :NEW.Nouvelle_index || 
                ') doit etre superieur ou egal a l''ancien index (' || 
                :NEW.Ancien_Index || ').');
        END IF;
    END IF;
END;
/

/* ====================== REGLE 12 : Entreprise immutable sur facture travaux ====================== */
/* Une fois le devis lie, on ne peut plus changer l'entreprise */

CREATE OR REPLACE TRIGGER TRG_FACTURE_ENTREPRISE_IMMUTABLE
BEFORE UPDATE ON FACTURE
FOR EACH ROW
BEGIN
    IF :OLD.Travaux IS NOT NULL AND :OLD.MontantDevis IS NOT NULL THEN
        IF :NEW.Siret != :OLD.Siret THEN
            RAISE_APPLICATION_ERROR(-20112, 
                'L''entreprise d''une facture de travaux ' ||
                'avec devis ne peut pas etre modifiee. ' ||
                'Creez une nouvelle facture si necessaire.');
        END IF;
    END IF;
END;
/

/* ====================== REGLE 13 : Numero fiscal obligatoire ====================== */
/* Contraintes NOT NULL dans 01_DROP_ET_TABLES.sql */

/* ====================== REGLE 14 : Association batiment obligatoire ====================== */
/* Un logement ou garage doit etre lie a un batiment */

CREATE OR REPLACE TRIGGER TRG_LOGEMENT_BATIMENT_OBLIGATOIRE
BEFORE INSERT OR UPDATE ON LOGEMENT
FOR EACH ROW
BEGIN
    IF :NEW.ID_BATIMENT IS NULL THEN
        RAISE_APPLICATION_ERROR(-20114, 
            'Un logement doit obligatoirement etre ' ||
            'associe a un batiment.');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_GARAGE_BATIMENT_OBLIGATOIRE
BEFORE INSERT OR UPDATE ON GARAGE
FOR EACH ROW
BEGIN
    IF :NEW.ID_BATIMENT IS NULL THEN
        RAISE_APPLICATION_ERROR(-20114, 
            'Un garage doit obligatoirement etre ' ||
            'associe a un batiment.');
    END IF;
END;
/

/* ====================== REGLE 15 : Date fin obligatoire si bail termine ====================== */
/* Un bail resilie ou cloture doit avoir une date de fin */

CREATE OR REPLACE TRIGGER TRG_BAIL_DATE_FIN_OBLIGATOIRE
BEFORE UPDATE ON BAIL
FOR EACH ROW
BEGIN
    IF :NEW.ETAT IN ('RESILIE', 'CLOTURE') AND :NEW.DATE_FIN IS NULL THEN
        RAISE_APPLICATION_ERROR(-20115, 
            'Un bail resilie ou cloture doit avoir ' ||
            'une date de fin renseignee.');
    END IF;
END;
/

/* ====================== TRIGGERS DE DISPONIBILITE ====================== */

/* Empeche d'affecter un bail a un logement deja loue */
CREATE OR REPLACE TRIGGER TRG_LOGEMENT_DISPONIBILITE
BEFORE UPDATE ON LOGEMENT
FOR EACH ROW
BEGIN
    IF :OLD.ID_BAIL IS NOT NULL AND :NEW.ID_BAIL IS NOT NULL 
       AND :OLD.ID_BAIL != :NEW.ID_BAIL THEN
        RAISE_APPLICATION_ERROR(-20201, 
            'Ce logement est deja loue (bail #' || :OLD.ID_BAIL || '). ' ||
            'Terminez le bail existant avant d''en creer un nouveau.');
    END IF;
END;
/

/* Empeche d'affecter un bail a un garage deja loue */
CREATE OR REPLACE TRIGGER TRG_GARAGE_DISPONIBILITE
BEFORE UPDATE ON GARAGE
FOR EACH ROW
BEGIN
    IF :OLD.ID_BAIL IS NOT NULL AND :NEW.ID_BAIL IS NOT NULL 
       AND :OLD.ID_BAIL != :NEW.ID_BAIL THEN
        RAISE_APPLICATION_ERROR(-20202, 
            'Ce garage est deja loue (bail #' || :OLD.ID_BAIL || '). ' ||
            'Terminez le bail existant avant d''en creer un nouveau.');
    END IF;
END;
/

/* ====================== TRIGGER VERIFICATION COORDONNEES LOCATAIRE ====================== */
/* Verifie que le locataire a toutes ses coordonnees avant de signer un bail */

CREATE OR REPLACE TRIGGER TRG_VERIF_COORDONNEES_LOCATAIRE
BEFORE INSERT ON SIGNE
FOR EACH ROW
DECLARE
    v_nom            LOCATAIRE.NOM_LOC%TYPE;
    v_prenom         LOCATAIRE.PRENOM_LOC%TYPE;
    v_tel            LOCATAIRE.TEL_LOC%TYPE;
    v_mail           LOCATAIRE.MAIL_LOC%TYPE;
    v_date_naissance LOCATAIRE.DATE_NAISSANCE%TYPE;
BEGIN
    SELECT NOM_LOC, PRENOM_LOC, TEL_LOC, MAIL_LOC, DATE_NAISSANCE
    INTO v_nom, v_prenom, v_tel, v_mail, v_date_naissance
    FROM LOCATAIRE
    WHERE ID_LOCATAIRE = :NEW.ID_LOCATAIRE;
    
    IF v_nom IS NULL OR TRIM(v_nom) = '' THEN
        RAISE_APPLICATION_ERROR(-20301, 
            'Le nom du locataire est obligatoire pour signer un bail.');
    END IF;
    
    IF v_prenom IS NULL OR TRIM(v_prenom) = '' THEN
        RAISE_APPLICATION_ERROR(-20302, 
            'Le prenom du locataire est obligatoire pour signer un bail.');
    END IF;
    
    IF v_date_naissance IS NULL THEN
        RAISE_APPLICATION_ERROR(-20303, 
            'La date de naissance du locataire est obligatoire pour signer un bail.');
    END IF;
    
    IF v_tel IS NULL OR TRIM(v_tel) = '' THEN
        RAISE_APPLICATION_ERROR(-20304, 
            'Le telephone du locataire est obligatoire pour signer un bail.');
    END IF;
    
    IF v_mail IS NULL OR TRIM(v_mail) = '' THEN
        RAISE_APPLICATION_ERROR(-20305, 
            'L''email du locataire est obligatoire pour signer un bail.');
    END IF;
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20306, 
            'Locataire non trouve (ID: ' || :NEW.ID_LOCATAIRE || ').');
END;
/

/* ====================== TRIGGER BAIL CLOTURE IMMUTABLE ====================== */
/* Empeche la modification des informations d'un bail cloture */

CREATE OR REPLACE TRIGGER TRG_BAIL_CLOTURE_IMMUTABLE
BEFORE UPDATE ON BAIL
FOR EACH ROW
DECLARE
    v_ancien_etat VARCHAR2(50);
BEGIN
    v_ancien_etat := :OLD.ETAT;
    
    IF UPPER(v_ancien_etat) = 'CLOTURE' THEN
        IF :NEW.LOYER_INITIAL != :OLD.LOYER_INITIAL 
           OR :NEW.PROVISION_INITIALES != :OLD.PROVISION_INITIALES
           OR :NEW.CAUTION != :OLD.CAUTION
           OR :NEW.DATE_DEBUT != :OLD.DATE_DEBUT THEN
            RAISE_APPLICATION_ERROR(-20300, 
                'Impossible de modifier un bail cloture. ' ||
                'Les informations d''un bail termine ne peuvent plus etre changees.');
        END IF;
    END IF;
END TRG_BAIL_CLOTURE_IMMUTABLE;
/

/* ====================== TRIGGERS HISTORIQUE_ACTIVITE (NOUVEAU) ====================== */
/* Ces triggers alimentent automatiquement la table HISTORIQUE_ACTIVITE pour le dashboard */

/* Enregistre les paiements de loyers recus */
CREATE OR REPLACE TRIGGER TRG_HISTORIQUE_PAIEMENT
AFTER UPDATE ON LOYER
FOR EACH ROW
WHEN (UPPER(NEW.STATUT) = 'PAYE' AND (OLD.STATUT IS NULL OR UPPER(OLD.STATUT) != 'PAYE'))
DECLARE
    v_adresse VARCHAR2(200);
    v_locataire VARCHAR2(200);
BEGIN
    BEGIN
        SELECT l.ADRESSE_LOGEMENT INTO v_adresse
        FROM LOGEMENT l
        WHERE l.ID_BAIL = :NEW.ID_BAIL AND ROWNUM = 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_adresse := 'Bien non identifie';
    END;
    
    BEGIN
        SELECT loc.NOM_LOC || ' ' || loc.PRENOM_LOC INTO v_locataire
        FROM SIGNE s JOIN LOCATAIRE loc ON s.ID_LOCATAIRE = loc.ID_LOCATAIRE
        WHERE s.ID_BAIL = :NEW.ID_BAIL AND ROWNUM = 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_locataire := 'Locataire';
    END;
    
    INSERT INTO HISTORIQUE_ACTIVITE 
        (ID_ACTIVITE, TYPE_ACTIVITE, DESCRIPTION_ACTIVITE, DATE_ACTIVITE, 
         ENTITE_TYPE, ENTITE_ID, MONTANT, ADRESSE)
    VALUES 
        (SEQ_HISTORIQUE_ACTIVITE.NEXTVAL, 'PAIEMENT_RECU',
         'Loyer recu de ' || v_locataire || ' (' || :NEW.MOIS || ')',
         SYSTIMESTAMP, 'LOYER', :NEW.ID_LOYER, 
         :NEW.MONTANT_LOYER + NVL(:NEW.MONTANT_PROVISION, 0), v_adresse);
END TRG_HISTORIQUE_PAIEMENT;
/

/* Enregistre les nouveaux baux signes */
CREATE OR REPLACE TRIGGER TRG_HISTORIQUE_NOUVEAU_BAIL
AFTER INSERT ON BAIL
FOR EACH ROW
BEGIN
    INSERT INTO HISTORIQUE_ACTIVITE 
        (ID_ACTIVITE, TYPE_ACTIVITE, DESCRIPTION_ACTIVITE, DATE_ACTIVITE, 
         ENTITE_TYPE, ENTITE_ID, MONTANT, ADRESSE)
    VALUES 
        (SEQ_HISTORIQUE_ACTIVITE.NEXTVAL, 'CONTRAT_SIGNE',
         'Nouveau bail signe - Loyer ' || :NEW.LOYER_INITIAL || ' EUR',
         SYSTIMESTAMP, 'BAIL', :NEW.ID_BAIL, :NEW.LOYER_INITIAL, NULL);
END TRG_HISTORIQUE_NOUVEAU_BAIL;
/

/* Enregistre les baux clotures */
CREATE OR REPLACE TRIGGER TRG_HISTORIQUE_BAIL_CLOTURE
AFTER UPDATE ON BAIL
FOR EACH ROW
WHEN (UPPER(NEW.ETAT) = 'CLOTURE' AND UPPER(OLD.ETAT) != 'CLOTURE')
BEGIN
    INSERT INTO HISTORIQUE_ACTIVITE 
        (ID_ACTIVITE, TYPE_ACTIVITE, DESCRIPTION_ACTIVITE, DATE_ACTIVITE, 
         ENTITE_TYPE, ENTITE_ID, MONTANT, ADRESSE)
    VALUES 
        (SEQ_HISTORIQUE_ACTIVITE.NEXTVAL, 'CONTRAT_CLOTURE',
         'Bail cloture',
         SYSTIMESTAMP, 'BAIL', :NEW.ID_BAIL, :NEW.SOLDE_TOUT_COMPTE, NULL);
END TRG_HISTORIQUE_BAIL_CLOTURE;
/

/* Enregistre les nouveaux batiments ajoutes */
CREATE OR REPLACE TRIGGER TRG_HISTORIQUE_NOUVEAU_BATIMENT
AFTER INSERT ON BATIMENT
FOR EACH ROW
BEGIN
    INSERT INTO HISTORIQUE_ACTIVITE 
        (ID_ACTIVITE, TYPE_ACTIVITE, DESCRIPTION_ACTIVITE, DATE_ACTIVITE, 
         ENTITE_TYPE, ENTITE_ID, MONTANT, ADRESSE)
    VALUES 
        (SEQ_HISTORIQUE_ACTIVITE.NEXTVAL, 'NOUVEAU_BATIMENT',
         'Nouveau batiment ajoute: ' || :NEW.ADRESSE,
         SYSTIMESTAMP, 'BATIMENT', :NEW.ID_BATIMENT, NULL, :NEW.ADRESSE);
END TRG_HISTORIQUE_NOUVEAU_BATIMENT;
/

/* Enregistre les nouveaux logements ajoutes */
CREATE OR REPLACE TRIGGER TRG_HISTORIQUE_NOUVEAU_LOGEMENT
AFTER INSERT ON LOGEMENT
FOR EACH ROW
BEGIN
    INSERT INTO HISTORIQUE_ACTIVITE 
        (ID_ACTIVITE, TYPE_ACTIVITE, DESCRIPTION_ACTIVITE, DATE_ACTIVITE, 
         ENTITE_TYPE, ENTITE_ID, MONTANT, ADRESSE)
    VALUES 
        (SEQ_HISTORIQUE_ACTIVITE.NEXTVAL, 'NOUVEAU_LOGEMENT',
         'Nouveau logement ajoute: ' || :NEW.ADRESSE_LOGEMENT,
         SYSTIMESTAMP, 'LOGEMENT', :NEW.ID_LOGEMENT, NULL, :NEW.ADRESSE_LOGEMENT);
END TRG_HISTORIQUE_NOUVEAU_LOGEMENT;
/

/* Enregistre les nouveaux garages ajoutes */
CREATE OR REPLACE TRIGGER TRG_HISTORIQUE_NOUVEAU_GARAGE
AFTER INSERT ON GARAGE
FOR EACH ROW
BEGIN
    INSERT INTO HISTORIQUE_ACTIVITE 
        (ID_ACTIVITE, TYPE_ACTIVITE, DESCRIPTION_ACTIVITE, DATE_ACTIVITE, 
         ENTITE_TYPE, ENTITE_ID, MONTANT, ADRESSE)
    VALUES 
        (SEQ_HISTORIQUE_ACTIVITE.NEXTVAL, 'NOUVEAU_GARAGE',
         'Nouveau garage ajoute: ' || :NEW.ADRESSE_GARAGE,
         SYSTIMESTAMP, 'GARAGE', :NEW.ID_GARAGE, NULL, :NEW.ADRESSE_GARAGE);
END TRG_HISTORIQUE_NOUVEAU_GARAGE;
/

/* Enregistre les nouveaux locataires ajoutes */
CREATE OR REPLACE TRIGGER TRG_HISTORIQUE_NOUVEAU_LOCATAIRE
AFTER INSERT ON LOCATAIRE
FOR EACH ROW
BEGIN
    INSERT INTO HISTORIQUE_ACTIVITE 
        (ID_ACTIVITE, TYPE_ACTIVITE, DESCRIPTION_ACTIVITE, DATE_ACTIVITE, 
         ENTITE_TYPE, ENTITE_ID, MONTANT, ADRESSE)
    VALUES 
        (SEQ_HISTORIQUE_ACTIVITE.NEXTVAL, 'NOUVEAU_LOCATAIRE',
         'Nouveau locataire: ' || :NEW.NOM_LOC || ' ' || :NEW.PRENOM_LOC,
         SYSTIMESTAMP, 'LOCATAIRE', :NEW.ID_LOCATAIRE, NULL, NULL);
END TRG_HISTORIQUE_NOUVEAU_LOCATAIRE;
/

/* Enregistre les factures payees */
CREATE OR REPLACE TRIGGER TRG_HISTORIQUE_FACTURE_PAYEE
AFTER UPDATE ON FACTURE
FOR EACH ROW
WHEN (UPPER(NEW.STATUT_PAIEMENT) = 'PAYE' AND (OLD.STATUT_PAIEMENT IS NULL OR UPPER(OLD.STATUT_PAIEMENT) != 'PAYE'))
DECLARE
    v_adresse VARCHAR2(200);
BEGIN
    BEGIN
        SELECT b.ADRESSE INTO v_adresse
        FROM BATIMENT b WHERE b.ID_BATIMENT = :NEW.ID_BATIMENT;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        v_adresse := NULL;
    END;
    
    INSERT INTO HISTORIQUE_ACTIVITE 
        (ID_ACTIVITE, TYPE_ACTIVITE, DESCRIPTION_ACTIVITE, DATE_ACTIVITE, 
         ENTITE_TYPE, ENTITE_ID, MONTANT, ADRESSE)
    VALUES 
        (SEQ_HISTORIQUE_ACTIVITE.NEXTVAL, 'FACTURE_PAYEE',
         'Facture payee: ' || NVL(:NEW.NATURE, 'Facture'),
         SYSTIMESTAMP, 'FACTURE', :NEW.ID_FACTURE, :NEW.MONTANT_TTC, v_adresse);
END TRG_HISTORIQUE_FACTURE_PAYEE;
/

/* ====================== VERIFICATION ====================== */

/* Verifier que tous les triggers sont valides */
SELECT object_name, object_type, status 
FROM user_objects 
WHERE object_type = 'TRIGGER' 
AND status = 'INVALID';

/* Verifier que toutes les contraintes sont actives */
SELECT constraint_name, table_name, status 
FROM user_constraints 
WHERE status = 'DISABLED';

COMMIT;

/* FIN DU SCRIPT 04_TRIGGERS.sql */
