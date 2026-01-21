/* 
   SCRIPT 2/4 : FONCTIONS ET PROCEDURES PL/SQL
   Ce fichier contient toutes les fonctions et procedures de la gestion locative.
   A executer apres 01_DROP_ET_TABLES.sql
*/

/* Type objet pour retourner le recap fiscal complet en une seule requete */
CREATE OR REPLACE TYPE T_RECAP_FISCAL AS OBJECT (
    loyers_encaisses    NUMBER,
    charges_deductibles NUMBER,
    travaux             NUMBER,
    assurances          NUMBER,
    taxe_fonciere       NUMBER,
    resultat_net        NUMBER
);
/

/* ====================== FONCTIONS UTILITAIRES DE BASE ====================== */

/* Genere le prochain ID pour une table - evite les conflits de cles primaires */
CREATE OR REPLACE FUNCTION getNextId(
    p_table_name  IN VARCHAR2,
    p_column_name IN VARCHAR2
) RETURN NUMBER
AS
    v_next_id NUMBER;
    v_sql VARCHAR2(500);
BEGIN
    v_sql := 'SELECT NVL(MAX(' || p_column_name || '), 0) + 1 FROM ' || p_table_name;
    EXECUTE IMMEDIATE v_sql INTO v_next_id;
    RETURN v_next_id;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 1;
END getNextId;
/

/* Compte les factures impayees d'une entreprise - utile pour le suivi fournisseurs */
CREATE OR REPLACE FUNCTION countFacturesNonPayeesEntreprise(
    p_siret IN NUMBER
) RETURN NUMBER
AS
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM FACTURE
    WHERE SIRET = p_siret
    AND (STATUT_PAIEMENT IS NULL OR STATUT_PAIEMENT = 'A payer');
    RETURN v_count;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END countFacturesNonPayeesEntreprise;
/

/* Verifie si un batiment peut accueillir un nouveau logement
   Une maison ne peut avoir qu'un logement, un immeuble plusieurs */
CREATE OR REPLACE FUNCTION isBatimentDisponiblePourLogement(
    p_id_batiment IN NUMBER
) RETURN NUMBER
AS
    v_type_batiment VARCHAR2(50);
    v_nb_logements NUMBER := 0;
BEGIN
    SELECT TYPE_BATIMENT INTO v_type_batiment
    FROM BATIMENT
    WHERE ID_BATIMENT = p_id_batiment;
    
    IF UPPER(v_type_batiment) = 'IMMEUBLE' THEN
        RETURN 1;
    END IF;
    
    IF UPPER(v_type_batiment) = 'MAISON' THEN
        SELECT COUNT(*) INTO v_nb_logements
        FROM LOGEMENT
        WHERE ID_BATIMENT = p_id_batiment;
        
        IF v_nb_logements = 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END IF;
    
    IF UPPER(v_type_batiment) = 'GARAGE' THEN
        RETURN 0;
    END IF;
    
    RETURN 0;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END isBatimentDisponiblePourLogement;
/

/* ====================== FONCTIONS BAIL/LOCATAIRE/LOGEMENT ====================== */

/* Retourne l'ID du bail actif d'un locataire - NULL si aucun bail en cours */
CREATE OR REPLACE FUNCTION getBailActifLocataire(
    p_id_locataire IN NUMBER
) RETURN NUMBER
AS
    v_id_bail NUMBER;
BEGIN
    SELECT b.id_bail INTO v_id_bail
    FROM SIGNE s
    JOIN BAIL b ON s.id_bail = b.id_bail
    WHERE s.id_locataire = p_id_locataire
    AND b.etat = 'EN_COURS'
    AND ROWNUM = 1;
    RETURN v_id_bail;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END getBailActifLocataire;
/

/* Retourne le logement occupe par un locataire via son bail actif */
CREATE OR REPLACE FUNCTION getLogementByLocataire(
    p_id_locataire IN NUMBER
) RETURN NUMBER
AS
    v_id_logement NUMBER;
BEGIN
    SELECT lg.id_logement INTO v_id_logement
    FROM SIGNE s
    JOIN BAIL b ON s.id_bail = b.id_bail
    JOIN LOGEMENT lg ON lg.id_bail = b.id_bail
    WHERE s.id_locataire = p_id_locataire
    AND b.etat = 'EN_COURS'
    AND ROWNUM = 1;
    RETURN v_id_logement;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END getLogementByLocataire;
/

/* Retourne le bail actif d'un logement - NULL si logement libre */
CREATE OR REPLACE FUNCTION getBailActifLogement(
    p_id_logement IN NUMBER
) RETURN NUMBER
AS
    v_id_bail NUMBER;
BEGIN
    SELECT id_bail INTO v_id_bail
    FROM LOGEMENT
    WHERE id_logement = p_id_logement
    AND id_bail IS NOT NULL;
    RETURN v_id_bail;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END getBailActifLogement;
/

/* Retourne le bail actif d'un garage - NULL si garage libre */
CREATE OR REPLACE FUNCTION getBailActifGarage(
    p_id_garage IN NUMBER
) RETURN NUMBER
AS
    v_id_bail NUMBER;
BEGIN
    SELECT id_bail INTO v_id_bail
    FROM GARAGE
    WHERE id_garage = p_id_garage
    AND id_bail IS NOT NULL;
    RETURN v_id_bail;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END getBailActifGarage;
/

/* Compte les loyers impayes d'un bail - pour alertes et relances */
CREATE OR REPLACE FUNCTION countLoyersImpayes(
    p_id_bail IN NUMBER
) RETURN NUMBER
AS
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM LOYER
    WHERE ID_BAIL = p_id_bail
    AND (STATUT IS NULL OR UPPER(TRIM(STATUT)) != 'PAYE');
    RETURN v_count;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END countLoyersImpayes;
/

/* Calcule le montant total des loyers impayes d'un bail */
CREATE OR REPLACE FUNCTION getTotalLoyersImpayes(
    p_id_bail IN NUMBER
) RETURN NUMBER
AS
    v_total NUMBER := 0;
BEGIN
    SELECT NVL(SUM(MONTANT_LOYER + NVL(MONTANT_PROVISION, 0)), 0)
    INTO v_total
    FROM LOYER
    WHERE ID_BAIL = p_id_bail
    AND (STATUT IS NULL OR UPPER(TRIM(STATUT)) != 'PAYE');
    RETURN v_total;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END getTotalLoyersImpayes;
/

/* Determine le statut d'un locataire: ACTIF (bail en cours), ANCIEN (bail termine), PROSPECT (jamais loue) */
CREATE OR REPLACE FUNCTION getStatutLocataire(p_id_locataire IN NUMBER)
RETURN VARCHAR2
IS
    v_count_actif NUMBER;
    v_count_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count_actif 
    FROM SIGNE s 
    JOIN BAIL b ON s.ID_BAIL = b.ID_BAIL
    WHERE s.ID_LOCATAIRE = p_id_locataire 
    AND b.ETAT = 'EN_COURS';
    
    IF v_count_actif > 0 THEN 
        RETURN 'ACTIF'; 
    END IF;
    
    SELECT COUNT(*) INTO v_count_total 
    FROM SIGNE 
    WHERE ID_LOCATAIRE = p_id_locataire;
    
    IF v_count_total > 0 THEN 
        RETURN 'ANCIEN'; 
    ELSE 
        RETURN 'PROSPECT';
    END IF;
END getStatutLocataire;
/

/* Retourne le statut d'un diagnostic: Valide, Expire ou Inconnu */
CREATE OR REPLACE FUNCTION getStatutDiagnostic(
    p_date_expiration IN DATE
) RETURN VARCHAR2
AS
BEGIN
    IF p_date_expiration IS NULL THEN
        RETURN 'Inconnu';
    ELSIF p_date_expiration < SYSDATE THEN
        RETURN 'Expire';
    ELSE
        RETURN 'Valide';
    END IF;
END getStatutDiagnostic;
/

/* ====================== FONCTIONS CALCUL LOYERS ET PROVISIONS ====================== */

/* Calcule le nombre de mois d'occupation d'un bail pour une annee donnee
   Utilise pour le calcul au prorata des charges (regle 10) */
CREATE OR REPLACE FUNCTION getNbMoisOccupation(
    p_id_bail IN NUMBER,
    p_annee   IN NUMBER
) RETURN NUMBER
AS
    v_date_debut DATE;
    v_date_fin   DATE;
    v_debut_annee DATE;
    v_fin_annee   DATE;
    v_mois_debut  NUMBER;
    v_mois_fin    NUMBER;
BEGIN
    SELECT DATE_DEBUT, NVL(DATE_FIN, SYSDATE) 
    INTO v_date_debut, v_date_fin
    FROM BAIL 
    WHERE ID_BAIL = p_id_bail;
    
    v_debut_annee := TO_DATE('01/01/' || p_annee, 'DD/MM/YYYY');
    v_fin_annee := TO_DATE('31/12/' || p_annee, 'DD/MM/YYYY');
    
    IF v_date_debut > v_fin_annee THEN
        RETURN 0;
    END IF;
    
    IF v_date_fin < v_debut_annee THEN
        RETURN 0;
    END IF;
    
    IF v_date_debut < v_debut_annee THEN
        v_mois_debut := 1;
    ELSE
        v_mois_debut := EXTRACT(MONTH FROM v_date_debut);
    END IF;
    
    IF v_date_fin > v_fin_annee THEN
        v_mois_fin := 12;
    ELSE
        v_mois_fin := EXTRACT(MONTH FROM v_date_fin);
    END IF;
    
    RETURN GREATEST(v_mois_fin - v_mois_debut + 1, 0);
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END getNbMoisOccupation;
/

/* Calcule le total des loyers payes pour un bail et une annee
   Combine les loyers courants (LOYER) et archives (HISTORIQUE_LOYERS) */
CREATE OR REPLACE FUNCTION getTotalLoyersBail(
    p_id_bail IN NUMBER,
    p_annee   IN NUMBER
) RETURN NUMBER
AS
    v_total_loyer NUMBER := 0;
    v_total_historique NUMBER := 0;
BEGIN
    BEGIN
        SELECT NVL(SUM(NVL(MONTANT_LOYER, 0)), 0)
        INTO v_total_loyer
        FROM LOYER
        WHERE ID_BAIL = p_id_bail
        AND UPPER(TRIM(NVL(STATUT, ''))) = 'PAYE'
        AND (
            TRIM(MOIS) LIKE '%/' || p_annee
            OR TRIM(MOIS) LIKE '%/' || MOD(p_annee, 100)
        );
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_total_loyer := 0;
    END;
    
    BEGIN
        SELECT NVL(SUM(NVL(MONTANT_LOYER, 0)), 0)
        INTO v_total_historique
        FROM HISTORIQUE_LOYERS
        WHERE ID_BAIL = p_id_bail
        AND ANNEE = p_annee;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_total_historique := 0;
    END;
    
    RETURN v_total_loyer + v_total_historique;
END getTotalLoyersBail;
/

/* Calcule le total des provisions payees pour un bail et une annee */
CREATE OR REPLACE FUNCTION getTotalProvisionsBail(
    p_id_bail IN NUMBER,
    p_annee   IN NUMBER
) RETURN NUMBER
AS
    v_total_prov NUMBER := 0;
    v_total_historique NUMBER := 0;
BEGIN
    BEGIN
        SELECT NVL(SUM(NVL(MONTANT_PROVISION, 0)), 0)
        INTO v_total_prov
        FROM LOYER
        WHERE ID_BAIL = p_id_bail
        AND UPPER(TRIM(NVL(STATUT, ''))) = 'PAYE'
        AND (
            TRIM(MOIS) LIKE '%/' || p_annee
            OR TRIM(MOIS) LIKE '%/' || MOD(p_annee, 100)
        );
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_total_prov := 0;
    END;
    
    BEGIN
        SELECT NVL(SUM(NVL(MONTANT_PROVISION, 0)), 0)
        INTO v_total_historique
        FROM HISTORIQUE_LOYERS
        WHERE ID_BAIL = p_id_bail
        AND ANNEE = p_annee;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_total_historique := 0;
    END;
    
    RETURN v_total_prov + v_total_historique;
END getTotalProvisionsBail;
/

/* Calcule les provisions avec fallback sur la provision initiale si aucun paiement
   Utile en debut de bail quand il n'y a pas encore d'historique */
CREATE OR REPLACE FUNCTION getTotalProvisionsBailAvecFallback(
    p_id_bail IN NUMBER,
    p_annee   IN NUMBER
) RETURN NUMBER
AS
    v_total NUMBER := 0;
    v_provision_mensuelle NUMBER := 0;
    v_nb_mois NUMBER := 0;
BEGIN
    v_total := getTotalProvisionsBail(p_id_bail, p_annee);
    
    IF v_total = 0 THEN
        SELECT NVL(PROVISION_INITIALES, 0)
        INTO v_provision_mensuelle
        FROM BAIL
        WHERE ID_BAIL = p_id_bail;
        
        v_nb_mois := getNbMoisOccupation(p_id_bail, p_annee);
        v_total := v_provision_mensuelle * v_nb_mois;
    END IF;
    
    RETURN v_total;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END getTotalProvisionsBailAvecFallback;
/

/* ====================== FONCTIONS CALCUL DES CHARGES ====================== */

/* Calcule les charges d'un batiment pour un type donne (EAU, OM, ELEC, ENTRETIEN)
   Combine les charges saisies et les factures recuperables */
CREATE OR REPLACE FUNCTION getChargesBatimentByType(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER,
    p_type        IN VARCHAR2
) RETURN NUMBER
AS
    v_total_charges NUMBER := 0;
    v_total_factures NUMBER := 0;
BEGIN
    SELECT NVL(SUM(MONTANT * NVL(PCT_RECUPERABLE, 100) / 100), 0)
    INTO v_total_charges
    FROM CHARGES
    WHERE ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM DATE_CHARGE) = p_annee
    AND UPPER(TYPE_CHARGES) = UPPER(p_type);

    SELECT NVL(SUM(MONTANT_TTC), 0)
    INTO v_total_factures
    FROM FACTURE
    WHERE ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM DATE_EMISSION) = p_annee
    AND RECUPERABLE_LOCATAIRE = 1
    AND ID_CHARGE IS NULL
    AND (STATUT_PAIEMENT IS NULL OR UPPER(STATUT_PAIEMENT) = 'PAYE')
    AND UPPER(NATURE) LIKE '%' || UPPER(p_type) || '%';

    RETURN v_total_charges + v_total_factures;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END getChargesBatimentByType;
/

/* Fonctions specifiques par type de charge - simplifient les appels */
CREATE OR REPLACE FUNCTION getChargesEauBatiment(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER AS
BEGIN
    RETURN getChargesBatimentByType(p_id_batiment, p_annee, 'EAU');
END getChargesEauBatiment;
/

CREATE OR REPLACE FUNCTION getChargesOMBatiment(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER AS
BEGIN
    RETURN getChargesBatimentByType(p_id_batiment, p_annee, 'OM');
END getChargesOMBatiment;
/

CREATE OR REPLACE FUNCTION getChargesElecBatiment(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER AS
BEGIN
    RETURN getChargesBatimentByType(p_id_batiment, p_annee, 'ELEC');
END getChargesElecBatiment;
/

CREATE OR REPLACE FUNCTION getChargesEntretienBatiment(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER AS
BEGIN
    RETURN getChargesBatimentByType(p_id_batiment, p_annee, 'ENTRETIEN');
END getChargesEntretienBatiment;
/

/* Total de toutes les charges recuperables du batiment */
CREATE OR REPLACE FUNCTION getTotalChargesBatiment(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER
AS
BEGIN
    RETURN getChargesEauBatiment(p_id_batiment, p_annee)
         + getChargesOMBatiment(p_id_batiment, p_annee)
         + getChargesElecBatiment(p_id_batiment, p_annee)
         + getChargesEntretienBatiment(p_id_batiment, p_annee);
END getTotalChargesBatiment;
/

/* Calcule le total des mois d'occupation du batiment - denominateur pour la repartition */
CREATE OR REPLACE FUNCTION getTotalMoisOccupationBatiment(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER
AS
    v_total NUMBER := 0;
    v_mois NUMBER;
BEGIN
    FOR rec IN (
        SELECT DISTINCT ID_BAIL 
        FROM LOGEMENT 
        WHERE ID_BATIMENT = p_id_batiment 
        AND ID_BAIL IS NOT NULL
        UNION
        SELECT DISTINCT ID_BAIL 
        FROM GARAGE 
        WHERE ID_BATIMENT = p_id_batiment 
        AND ID_BAIL IS NOT NULL
    ) LOOP
        v_mois := getNbMoisOccupation(rec.ID_BAIL, p_annee);
        v_total := v_total + v_mois;
    END LOOP;
    
    RETURN GREATEST(v_total, 1);
END getTotalMoisOccupationBatiment;
/

/* Calcule la quote-part de charges d'un locataire au prorata de l'occupation (regle 10) */
CREATE OR REPLACE FUNCTION getChargesLocataire(
    p_id_bail     IN NUMBER,
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER,
    p_type        IN VARCHAR2
) RETURN NUMBER
AS
    v_nb_mois_locataire NUMBER;
    v_nb_mois_total NUMBER;
    v_charges_immeuble NUMBER;
    v_quote_part NUMBER;
BEGIN
    v_nb_mois_locataire := getNbMoisOccupation(p_id_bail, p_annee);
    
    IF v_nb_mois_locataire = 0 THEN
        RETURN 0;
    END IF;
    
    v_nb_mois_total := getTotalMoisOccupationBatiment(p_id_batiment, p_annee);
    v_charges_immeuble := getChargesBatimentByType(p_id_batiment, p_annee, p_type);
    v_quote_part := (v_nb_mois_locataire / v_nb_mois_total) * v_charges_immeuble;
    
    RETURN ROUND(v_quote_part, 2);
END getChargesLocataire;
/

/* Calcule la regularisation annuelle: charges reelles - provisions versees (regle 9) */
CREATE OR REPLACE FUNCTION getRegularisationLocataire(
    p_id_bail     IN NUMBER,
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER
AS
    v_total_charges NUMBER := 0;
    v_total_provisions NUMBER := 0;
BEGIN
    v_total_charges := getChargesLocataire(p_id_bail, p_id_batiment, p_annee, 'EAU')
                     + getChargesLocataire(p_id_bail, p_id_batiment, p_annee, 'OM')
                     + getChargesLocataire(p_id_bail, p_id_batiment, p_annee, 'ELEC')
                     + getChargesLocataire(p_id_bail, p_id_batiment, p_annee, 'ENTRETIEN');
    
    v_total_provisions := getTotalProvisionsBailAvecFallback(p_id_bail, p_annee);
    
    RETURN ROUND(v_total_charges - v_total_provisions, 2);
END getRegularisationLocataire;
/

/* Total des travaux du batiment pour la fiscalite */
CREATE OR REPLACE FUNCTION getTotalTravauxBatiment(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER
AS
    v_total NUMBER := 0;
BEGIN
    SELECT NVL(SUM(MONTANT_TTC), 0)
    INTO v_total
    FROM FACTURE
    WHERE ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM DATE_EMISSION) = p_annee
    AND (DEDUCTIBLE_IMPOT = 1 OR TRAVAUX IS NOT NULL);
    
    RETURN v_total;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END getTotalTravauxBatiment;
/

/* ====================== FONCTIONS FISCALITE ====================== */

/* Calcule le resultat fiscal net d'un batiment pour une annee */
CREATE OR REPLACE FUNCTION getRecapFiscal(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN NUMBER
AS
    v_loyers_encaisses    NUMBER := 0;
    v_charges_deductibles NUMBER := 0;
    v_travaux             NUMBER := 0;
    v_assurances          NUMBER := 0;
    v_taxe_fonciere       NUMBER := 0;
    v_resultat            NUMBER := 0;
BEGIN
    SELECT NVL(SUM(l.MONTANT_LOYER), 0)
    INTO v_loyers_encaisses
    FROM LOYER l
    JOIN LOGEMENT lg ON l.ID_BAIL = lg.ID_BAIL
    WHERE lg.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM l.DATE_PAIEMENT) = p_annee
    AND UPPER(TRIM(l.STATUT)) = 'PAYE';
    
    SELECT v_loyers_encaisses + NVL(SUM(l.MONTANT_LOYER), 0)
    INTO v_loyers_encaisses
    FROM LOYER l
    JOIN GARAGE g ON l.ID_BAIL = g.ID_BAIL
    WHERE g.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM l.DATE_PAIEMENT) = p_annee
    AND UPPER(TRIM(l.STATUT)) = 'PAYE';

    SELECT NVL(SUM(c.MONTANT), 0)
    INTO v_charges_deductibles
    FROM CHARGES c
    WHERE c.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM c.DATE_CHARGE) = p_annee
    AND c.TYPE_CHARGES IN ('COPROPRIETE', 'ENTRETIEN', 'GESTION', 'REPARATION');

    SELECT NVL(SUM(f.MONTANT_TTC), 0)
    INTO v_travaux
    FROM FACTURE f
    WHERE f.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM f.DATE_EMISSION) = p_annee
    AND f.TRAVAUX IS NOT NULL
    AND NVL(f.DEDUCTIBLE_IMPOT, 0) = 1
    AND UPPER(TRIM(NVL(f.STATUT_PAIEMENT, 'A payer'))) = 'PAYE';
    
    SELECT v_travaux + NVL(SUM(f.MONTANT_TTC), 0)
    INTO v_travaux
    FROM FACTURE f
    JOIN LOGEMENT lg ON f.ID_LOGEMENT = lg.ID_LOGEMENT
    WHERE lg.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM f.DATE_EMISSION) = p_annee
    AND f.TRAVAUX IS NOT NULL
    AND NVL(f.DEDUCTIBLE_IMPOT, 0) = 1
    AND UPPER(TRIM(NVL(f.STATUT_PAIEMENT, 'A payer'))) = 'PAYE';

    SELECT NVL(SUM(a.PRIMEBASE), 0)
    INTO v_assurances
    FROM ASSURANCE a
    WHERE a.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM a.DATE_EFFET) = p_annee;
    
    SELECT v_assurances + NVL(SUM(a.PRIMEBASE), 0)
    INTO v_assurances
    FROM ASSURANCE a
    JOIN LOGEMENT lg ON a.ID_LOGEMENT = lg.ID_LOGEMENT
    WHERE lg.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM a.DATE_EFFET) = p_annee;
    
    SELECT v_assurances + NVL(SUM(a.PRIMEBASE), 0)
    INTO v_assurances
    FROM ASSURANCE a
    JOIN GARAGE g ON a.ID_GARAGE = g.ID_GARAGE
    WHERE g.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM a.DATE_EFFET) = p_annee;

    SELECT NVL(SUM(c.MONTANT), 0)
    INTO v_taxe_fonciere
    FROM CHARGES c
    WHERE c.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM c.DATE_CHARGE) = p_annee
    AND UPPER(c.TYPE_CHARGES) = 'TAXE_FONCIERE';

    v_resultat := v_loyers_encaisses 
                - v_charges_deductibles 
                - v_travaux 
                - v_assurances 
                - v_taxe_fonciere;
    
    RETURN v_resultat;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
    WHEN OTHERS THEN
        RETURN 0;
END getRecapFiscal;
/

/* Retourne le detail complet du recap fiscal dans un objet */
CREATE OR REPLACE FUNCTION getDetailRecapFiscal(
    p_id_batiment IN NUMBER,
    p_annee       IN NUMBER
) RETURN T_RECAP_FISCAL
AS
    v_loyers_encaisses    NUMBER := 0;
    v_charges_deductibles NUMBER := 0;
    v_travaux             NUMBER := 0;
    v_assurances          NUMBER := 0;
    v_taxe_fonciere       NUMBER := 0;
BEGIN
    SELECT NVL(SUM(l.MONTANT_LOYER), 0)
    INTO v_loyers_encaisses
    FROM LOYER l
    JOIN LOGEMENT lg ON l.ID_BAIL = lg.ID_BAIL
    WHERE lg.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM l.DATE_PAIEMENT) = p_annee
    AND UPPER(TRIM(l.STATUT)) = 'PAYE';
    
    SELECT v_loyers_encaisses + NVL(SUM(l.MONTANT_LOYER), 0)
    INTO v_loyers_encaisses
    FROM LOYER l
    JOIN GARAGE g ON l.ID_BAIL = g.ID_BAIL
    WHERE g.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM l.DATE_PAIEMENT) = p_annee
    AND UPPER(TRIM(l.STATUT)) = 'PAYE';

    SELECT NVL(SUM(c.MONTANT), 0)
    INTO v_charges_deductibles
    FROM CHARGES c
    WHERE c.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM c.DATE_CHARGE) = p_annee
    AND c.TYPE_CHARGES IN ('COPROPRIETE', 'ENTRETIEN', 'GESTION', 'REPARATION');

    SELECT NVL(SUM(f.MONTANT_TTC), 0)
    INTO v_travaux
    FROM FACTURE f
    WHERE f.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM f.DATE_EMISSION) = p_annee
    AND f.TRAVAUX IS NOT NULL
    AND NVL(f.DEDUCTIBLE_IMPOT, 0) = 1
    AND UPPER(TRIM(NVL(f.STATUT_PAIEMENT, 'A payer'))) = 'PAYE';
    
    SELECT v_travaux + NVL(SUM(f.MONTANT_TTC), 0)
    INTO v_travaux
    FROM FACTURE f
    JOIN LOGEMENT lg ON f.ID_LOGEMENT = lg.ID_LOGEMENT
    WHERE lg.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM f.DATE_EMISSION) = p_annee
    AND f.TRAVAUX IS NOT NULL
    AND NVL(f.DEDUCTIBLE_IMPOT, 0) = 1
    AND UPPER(TRIM(NVL(f.STATUT_PAIEMENT, 'A payer'))) = 'PAYE';

    SELECT NVL(SUM(a.PRIMEBASE), 0)
    INTO v_assurances
    FROM ASSURANCE a
    WHERE a.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM a.DATE_EFFET) = p_annee;
    
    SELECT v_assurances + NVL(SUM(a.PRIMEBASE), 0)
    INTO v_assurances
    FROM ASSURANCE a
    JOIN LOGEMENT lg ON a.ID_LOGEMENT = lg.ID_LOGEMENT
    WHERE lg.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM a.DATE_EFFET) = p_annee;
    
    SELECT v_assurances + NVL(SUM(a.PRIMEBASE), 0)
    INTO v_assurances
    FROM ASSURANCE a
    JOIN GARAGE g ON a.ID_GARAGE = g.ID_GARAGE
    WHERE g.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM a.DATE_EFFET) = p_annee;

    SELECT NVL(SUM(c.MONTANT), 0)
    INTO v_taxe_fonciere
    FROM CHARGES c
    WHERE c.ID_BATIMENT = p_id_batiment
    AND EXTRACT(YEAR FROM c.DATE_CHARGE) = p_annee
    AND UPPER(c.TYPE_CHARGES) = 'TAXE_FONCIERE';

    RETURN T_RECAP_FISCAL(
        v_loyers_encaisses,
        v_charges_deductibles,
        v_travaux,
        v_assurances,
        v_taxe_fonciere,
        v_loyers_encaisses - v_charges_deductibles - v_travaux - v_assurances - v_taxe_fonciere
    );
EXCEPTION
    WHEN OTHERS THEN
        RETURN T_RECAP_FISCAL(0, 0, 0, 0, 0, 0);
END getDetailRecapFiscal;
/

/* ====================== FONCTIONS STATISTIQUES (TABLEAU DE BORD) ====================== */

/* FIX: Retourne les loyers REELLEMENT payes ce mois-ci (pas les loyers theoriques)
   Ancienne version retournait la somme des loyers_initial des baux actifs */
CREATE OR REPLACE FUNCTION getRevenuMensuel RETURN NUMBER
AS
    v_total NUMBER := 0;
BEGIN
    SELECT NVL(SUM(MONTANT_LOYER), 0)
    INTO v_total
    FROM LOYER
    WHERE UPPER(TRIM(NVL(STATUT, ''))) = 'PAYE'
    AND (
        TO_CHAR(DATE_PAIEMENT, 'MM/YYYY') = TO_CHAR(SYSDATE, 'MM/YYYY')
        OR
        (DATE_PAIEMENT IS NULL AND (
            TRIM(MOIS) LIKE TO_CHAR(SYSDATE, 'MM') || '/' || TO_CHAR(SYSDATE, 'YYYY')
            OR TRIM(MOIS) LIKE TO_CHAR(SYSDATE, 'MM') || '/' || TO_CHAR(SYSDATE, 'YY')
        ))
    );
    
    RETURN v_total;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END getRevenuMensuel;
/

/* Compte le nombre total de proprietes (logements + garages) */
CREATE OR REPLACE FUNCTION getNbProprietes RETURN NUMBER
AS
    v_count NUMBER := 0;
BEGIN
    SELECT (SELECT COUNT(*) FROM LOGEMENT) + (SELECT COUNT(*) FROM GARAGE)
    INTO v_count
    FROM DUAL;
    RETURN v_count;
END getNbProprietes;
/

/* Compte le nombre de locataires avec un bail actif */
CREATE OR REPLACE FUNCTION getNbLocatairesActifs RETURN NUMBER
AS
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(DISTINCT s.ID_LOCATAIRE)
    INTO v_count
    FROM SIGNE s
    JOIN BAIL b ON s.ID_BAIL = b.ID_BAIL
    WHERE b.ETAT = 'EN_COURS';
    RETURN v_count;
END getNbLocatairesActifs;
/

/* Compte le nombre de baux en cours */
CREATE OR REPLACE FUNCTION getNbContratsEnCours RETURN NUMBER
AS
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM BAIL
    WHERE ETAT = 'EN_COURS';
    RETURN v_count;
END getNbContratsEnCours;
/

/* ====================== FONCTIONS REVALORISATION ====================== */

/* Verifie si un bail peut etre revalorise (>12 mois et pas de reval depuis 12 mois) */
CREATE OR REPLACE FUNCTION peutRevaloriser(
    p_id_bail IN NUMBER
) RETURN NUMBER
AS
    v_date_debut DATE;
    v_date_derniere_reval DATE;
    v_mois_depuis_debut NUMBER;
    v_mois_depuis_reval NUMBER;
BEGIN
    SELECT DATE_DEBUT, DATE_DERNIERE_REVALORISATION 
    INTO v_date_debut, v_date_derniere_reval
    FROM BAIL
    WHERE ID_BAIL = p_id_bail;
    
    v_mois_depuis_debut := MONTHS_BETWEEN(SYSDATE, v_date_debut);
    
    IF v_mois_depuis_debut < 12 THEN
        RETURN 0;
    END IF;
    
    IF v_date_derniere_reval IS NOT NULL THEN
        v_mois_depuis_reval := MONTHS_BETWEEN(SYSDATE, v_date_derniere_reval);
        IF v_mois_depuis_reval < 12 THEN
            RETURN 0;
        END IF;
    END IF;
    
    RETURN 1;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END peutRevaloriser;
/

/* Retourne un message explicatif si la revalorisation n'est pas possible */
CREATE OR REPLACE FUNCTION getMessageErreurRevalorisation(
    p_id_bail IN NUMBER
) RETURN VARCHAR2
AS
    v_date_debut DATE;
    v_date_derniere_reval DATE;
    v_mois_depuis_debut NUMBER;
    v_mois_depuis_reval NUMBER;
BEGIN
    SELECT DATE_DEBUT, DATE_DERNIERE_REVALORISATION 
    INTO v_date_debut, v_date_derniere_reval
    FROM BAIL
    WHERE ID_BAIL = p_id_bail;
    
    v_mois_depuis_debut := MONTHS_BETWEEN(SYSDATE, v_date_debut);
    
    IF v_mois_depuis_debut < 12 THEN
        RETURN 'Le bail doit avoir au moins 12 mois pour etre revalorise. ' ||
               'Actuellement : ' || ROUND(v_mois_depuis_debut, 1) || ' mois. ' ||
               'Revalorisation possible a partir du ' || 
               TO_CHAR(ADD_MONTHS(v_date_debut, 12), 'DD/MM/YYYY') || '.';
    END IF;
    
    IF v_date_derniere_reval IS NOT NULL THEN
        v_mois_depuis_reval := MONTHS_BETWEEN(SYSDATE, v_date_derniere_reval);
        IF v_mois_depuis_reval < 12 THEN
            RETURN 'Une revalorisation a deja ete effectuee le ' || 
                   TO_CHAR(v_date_derniere_reval, 'DD/MM/YYYY') || '. ' ||
                   'Prochaine revalorisation possible a partir du ' ||
                   TO_CHAR(ADD_MONTHS(v_date_derniere_reval, 12), 'DD/MM/YYYY') || '.';
        END IF;
    END IF;
    
    RETURN NULL;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'Bail introuvable.';
END getMessageErreurRevalorisation;
/

/* Calcule le solde de tout compte d'un bail (loyers impayes + regul - caution) */
CREATE OR REPLACE FUNCTION calculerSoldeBail(
    p_id_bail IN NUMBER
) RETURN NUMBER
AS
    v_loyers_impayes NUMBER := 0;
    v_regularisation NUMBER := 0;
    v_caution NUMBER := 0;
    v_id_batiment NUMBER;
    v_annee NUMBER;
BEGIN
    v_annee := EXTRACT(YEAR FROM SYSDATE);
    
    SELECT NVL(SUM(MONTANT_LOYER + NVL(MONTANT_PROVISION, 0)), 0)
    INTO v_loyers_impayes
    FROM LOYER
    WHERE ID_BAIL = p_id_bail
    AND UPPER(STATUT) != 'PAYE';
    
    BEGIN
        SELECT ID_BATIMENT INTO v_id_batiment
        FROM LOGEMENT WHERE ID_BAIL = p_id_bail AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            BEGIN
                SELECT ID_BATIMENT INTO v_id_batiment
                FROM GARAGE WHERE ID_BAIL = p_id_bail AND ROWNUM = 1;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    v_id_batiment := NULL;
            END;
    END;
    
    IF v_id_batiment IS NOT NULL THEN
        v_regularisation := getRegularisationLocataire(p_id_bail, v_id_batiment, v_annee);
    END IF;
    
    SELECT NVL(CAUTION, 0) INTO v_caution
    FROM BAIL WHERE ID_BAIL = p_id_bail;
    
    RETURN v_loyers_impayes + v_regularisation - v_caution;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END calculerSoldeBail;
/

/* ====================== PROCEDURES LOYERS ET PAIEMENTS ====================== */

/* Marque un loyer comme paye et genere la reference quittance */
CREATE OR REPLACE PROCEDURE marquerLoyerPaye(
    p_id_loyer      IN NUMBER,
    p_date_paiement IN DATE
)
AS
    v_quittance VARCHAR2(100);
BEGIN
    v_quittance := 'QUI-' || TO_CHAR(p_date_paiement, 'YYYYMMDD') || '-' || p_id_loyer;
    
    UPDATE LOYER
    SET STATUT = 'Paye',
        DATE_PAIEMENT = p_date_paiement,
        QUITTANCE = v_quittance
    WHERE ID_LOYER = p_id_loyer;
    
    COMMIT;
END marquerLoyerPaye;
/

/* Annule un paiement - utile en cas d'erreur de saisie */
CREATE OR REPLACE PROCEDURE annulerPaiementLoyer(
    p_id_loyer IN NUMBER
)
AS
BEGIN
    UPDATE LOYER
    SET STATUT = 'En attente',
        DATE_PAIEMENT = NULL,
        QUITTANCE = NULL
    WHERE ID_LOYER = p_id_loyer;
    
    COMMIT;
END annulerPaiementLoyer;
/

/* Genere tous les loyers d'une annee pour un bail */
CREATE OR REPLACE PROCEDURE genererLoyersAnnuels(
    p_id_bail IN NUMBER,
    p_annee   IN NUMBER
)
AS
    v_loyer NUMBER;
    v_provision NUMBER;
    v_id_loyer NUMBER;
    v_mois VARCHAR2(15);
BEGIN
    SELECT LOYER_INITIAL, PROVISION_INITIALES
    INTO v_loyer, v_provision
    FROM BAIL WHERE ID_BAIL = p_id_bail;
    
    FOR i IN 1..12 LOOP
        v_mois := LPAD(i, 2, '0') || '/' || p_annee;
        
        BEGIN
            SELECT ID_LOYER INTO v_id_loyer
            FROM LOYER
            WHERE ID_BAIL = p_id_bail AND TRIM(MOIS) = v_mois;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                SELECT NVL(MAX(ID_LOYER), 0) + 1 INTO v_id_loyer FROM LOYER;
                
                INSERT INTO LOYER (ID_LOYER, ID_BAIL, MOIS, MONTANT_LOYER, 
                                   MONTANT_PROVISION, STATUT)
                VALUES (v_id_loyer, p_id_bail, v_mois, v_loyer, 
                        v_provision, 'EN_ATTENTE');
        END;
    END LOOP;
    
    COMMIT;
END genererLoyersAnnuels;
/

/* Importe un paiement depuis un fichier CSV - genere la quittance automatiquement */
CREATE OR REPLACE PROCEDURE IMPORTER_PAIEMENT_CSV(
    p_id_logement IN NUMBER,
    p_id_locataire IN NUMBER,
    p_mois_csv IN VARCHAR2,
    p_montant_loyer IN NUMBER,
    p_provision IN NUMBER
) AS
    v_id_bail NUMBER;
    v_id_loyer NUMBER;
    v_mois_recherche_1 VARCHAR2(15);
    v_mois_recherche_2 VARCHAR2(15);
    v_partie_mois VARCHAR2(2);
    v_partie_annee VARCHAR2(4);
    v_quittance VARCHAR2(100);
BEGIN
    v_partie_mois := SUBSTR(p_mois_csv, 1, 2);
    v_partie_annee := SUBSTR(p_mois_csv, 4); 
    
    IF LENGTH(v_partie_annee) = 2 THEN
        v_mois_recherche_1 := p_mois_csv;
        v_mois_recherche_2 := v_partie_mois || '/20' || v_partie_annee;
    ELSE
        v_mois_recherche_1 := p_mois_csv;
        v_mois_recherche_2 := v_partie_mois || '/' || SUBSTR(v_partie_annee, 3, 2);
    END IF;

    BEGIN
        SELECT l.ID_BAIL INTO v_id_bail
        FROM LOGEMENT l
        JOIN SIGNE s ON l.ID_BAIL = s.ID_BAIL
        WHERE l.ID_LOGEMENT = p_id_logement
        AND s.ID_LOCATAIRE = p_id_locataire;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            BEGIN
                SELECT g.ID_BAIL INTO v_id_bail
                FROM GARAGE g
                JOIN SIGNE s ON g.ID_BAIL = s.ID_BAIL
                WHERE g.ID_GARAGE = p_id_logement
                AND s.ID_LOCATAIRE = p_id_locataire;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    RAISE_APPLICATION_ERROR(-20010, 'ERREUR: Aucun bail actif trouve pour Logement ' || p_id_logement || ' / Locataire ' || p_id_locataire);
            END;
    END;

    BEGIN
        SELECT ID_LOYER INTO v_id_loyer
        FROM LOYER
        WHERE ID_BAIL = v_id_bail
        AND (TRIM(MOIS) = v_mois_recherche_1 OR TRIM(MOIS) = v_mois_recherche_2);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
             RAISE_APPLICATION_ERROR(-20010, 'ERREUR: Bail trouve ('||v_id_bail||') mais pas de loyer pour ' || v_mois_recherche_1 || ' ou ' || v_mois_recherche_2);
    END;

    v_quittance := 'QUI-' || TO_CHAR(SYSDATE, 'YYYYMMDD') || '-' || v_id_loyer;

    UPDATE LOYER
    SET MONTANT_LOYER = p_montant_loyer,
        MONTANT_PROVISION = p_provision,
        STATUT = 'PAYE',
        DATE_PAIEMENT = SYSDATE,
        QUITTANCE = v_quittance
    WHERE ID_LOYER = v_id_loyer;
    
    COMMIT;
END;
/

/* ====================== PROCEDURES GESTION BAUX ====================== */

/* Cree un bail complet avec logement et locataire en une seule operation */
CREATE OR REPLACE PROCEDURE creerBailComplet(
    p_id_bail       IN NUMBER,
    p_date_debut    IN DATE,
    p_loyer         IN NUMBER,
    p_provisions    IN NUMBER,
    p_caution       IN NUMBER,
    p_jour_paiement IN NUMBER,
    p_id_logement   IN NUMBER,
    p_id_locataire  IN NUMBER
)
AS
BEGIN
    INSERT INTO BAIL (ID_BAIL, DATE_DEBUT, LOYER_INITIAL, PROVISION_INITIALES, 
                      CAUTION, JOUR_PAIEMENT, ETAT)
    VALUES (p_id_bail, p_date_debut, p_loyer, p_provisions, 
            p_caution, p_jour_paiement, 'EN_COURS');
    
    INSERT INTO SIGNE (ID_BAIL, ID_LOCATAIRE) VALUES (p_id_bail, p_id_locataire);
    UPDATE LOGEMENT SET ID_BAIL = p_id_bail WHERE ID_LOGEMENT = p_id_logement;
    
    COMMIT;
END creerBailComplet;
/

/* Cree un bail pour un garage seul */
CREATE OR REPLACE PROCEDURE creerBailGarage(
    p_id_bail       IN NUMBER,
    p_date_debut    IN DATE,
    p_loyer         IN NUMBER,
    p_provisions    IN NUMBER,
    p_caution       IN NUMBER,
    p_jour_paiement IN NUMBER,
    p_id_garage     IN NUMBER,
    p_id_locataire  IN NUMBER
)
AS
BEGIN
    INSERT INTO BAIL (ID_BAIL, DATE_DEBUT, LOYER_INITIAL, PROVISION_INITIALES, 
                      CAUTION, JOUR_PAIEMENT, ETAT)
    VALUES (p_id_bail, p_date_debut, p_loyer, p_provisions, 
            p_caution, p_jour_paiement, 'EN_COURS');
    
    INSERT INTO SIGNE (ID_BAIL, ID_LOCATAIRE) VALUES (p_id_bail, p_id_locataire);
    UPDATE GARAGE SET ID_BAIL = p_id_bail WHERE ID_GARAGE = p_id_garage;
    
    COMMIT;
END creerBailGarage;
/

/* Ajoute un locataire supplementaire a un bail existant (colocation) */
CREATE OR REPLACE PROCEDURE ajouterLocataireAuBail(
    p_id_bail      IN NUMBER,
    p_id_locataire IN NUMBER
)
AS
BEGIN
    INSERT INTO SIGNE (ID_BAIL, ID_LOCATAIRE) VALUES (p_id_bail, p_id_locataire);
    COMMIT;
END ajouterLocataireAuBail;
/

/* Ajoute un garage a un bail existant */
CREATE OR REPLACE PROCEDURE lierGarageAuBail(
    p_id_bail   IN NUMBER,
    p_id_garage IN NUMBER
)
AS
BEGIN
    UPDATE GARAGE SET ID_BAIL = p_id_bail WHERE ID_GARAGE = p_id_garage;
    COMMIT;
END lierGarageAuBail;
/

/* Resilie un bail - le met en etat RESILIE avec date de fin */
CREATE OR REPLACE PROCEDURE resilierBail(
    p_id_bail  IN NUMBER,
    p_date_fin IN DATE
)
AS
BEGIN
    UPDATE BAIL 
    SET ETAT = 'RESILIE', DATE_FIN = p_date_fin
    WHERE ID_BAIL = p_id_bail;
    COMMIT;
END resilierBail;
/

/* ====================== PROCEDURE ARCHIVAGE LOYERS ====================== */

/* Archive les loyers payes d'un bail dans HISTORIQUE_LOYERS avant cloture
   Utilise COMPLEMENT_ADRESSE (corrige depuis ADRESSE_COMPLEMENTAIRE) */
CREATE OR REPLACE PROCEDURE archiverLoyersBail(p_id_bail IN NUMBER)
AS
    v_nom_bien VARCHAR2(200);
    v_nom_locataire VARCHAR2(200);
    v_id_logement NUMBER;
    v_id_garage NUMBER;
    v_id_batiment NUMBER;
    v_id_historique NUMBER;
    v_annee NUMBER;
BEGIN
    BEGIN
        SELECT l.ID_LOGEMENT, l.ID_BATIMENT,
               NVL(l.TYPE_LOGEMENT, 'Logement') || ' ' || NVL(l.COMPLEMENT_ADRESSE, l.ADRESSE_LOGEMENT)
        INTO v_id_logement, v_id_batiment, v_nom_bien
        FROM LOGEMENT l
        WHERE l.ID_BAIL = p_id_bail AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_id_logement := NULL;
            v_nom_bien := NULL;
    END;
    
    IF v_id_logement IS NULL THEN
        BEGIN
            SELECT g.ID_GARAGE, g.ID_BATIMENT, 'Garage ' || NVL(g.COMPLEMENT_ADRESSE, g.ADRESSE_GARAGE)
            INTO v_id_garage, v_id_batiment, v_nom_bien
            FROM GARAGE g
            WHERE g.ID_BAIL = p_id_bail AND ROWNUM = 1;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_id_garage := NULL;
                v_nom_bien := 'Bail #' || p_id_bail;
        END;
    END IF;
    
    BEGIN
        SELECT LISTAGG(loc.NOM_LOC || ' ' || loc.PRENOM_LOC, ', ') WITHIN GROUP (ORDER BY loc.NOM_LOC)
        INTO v_nom_locataire
        FROM SIGNE s
        JOIN LOCATAIRE loc ON s.ID_LOCATAIRE = loc.ID_LOCATAIRE
        WHERE s.ID_BAIL = p_id_bail;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_nom_locataire := 'Inconnu';
    END;
    
    FOR rec IN (
        SELECT ID_LOYER, MOIS, MONTANT_LOYER, MONTANT_PROVISION, DATE_PAIEMENT,
               CASE WHEN QUITTANCE IS NOT NULL THEN 'O' ELSE 'N' END AS QUITTANCE_OK
        FROM LOYER
        WHERE ID_BAIL = p_id_bail
        AND UPPER(STATUT) = 'PAYE'
    ) LOOP
        BEGIN
            v_annee := TO_NUMBER(SUBSTR(TRIM(rec.MOIS), 4, 4));
        EXCEPTION
            WHEN OTHERS THEN
                v_annee := EXTRACT(YEAR FROM SYSDATE);
        END;
        
        SELECT SEQ_HISTORIQUE_LOYERS.NEXTVAL INTO v_id_historique FROM DUAL;
        
        INSERT INTO HISTORIQUE_LOYERS (
            ID_HISTORIQUE, ID_BAIL, ID_LOGEMENT, ID_GARAGE, NOM_BIEN, NOM_LOCATAIRE,
            MOIS, ANNEE, MONTANT_LOYER, MONTANT_PROVISION, DATE_PAIEMENT,
            QUITTANCE_GENEREE, DATE_CLOTURE, ID_BATIMENT
        ) VALUES (
            v_id_historique,
            p_id_bail,
            v_id_logement,
            v_id_garage,
            v_nom_bien,
            v_nom_locataire,
            rec.MOIS,
            v_annee,
            rec.MONTANT_LOYER,
            rec.MONTANT_PROVISION,
            rec.DATE_PAIEMENT,
            rec.QUITTANCE_OK,
            SYSDATE,
            v_id_batiment
        );
    END LOOP;
    
    COMMIT;
END archiverLoyersBail;
/

/* ====================== PROCEDURE CLOTURE BAIL ====================== */

/* Cloture un bail: archive les loyers, supprime les impayes, libere les biens */
CREATE OR REPLACE PROCEDURE cloturerBail(
    p_id_bail           IN NUMBER,
    p_date_fin          IN DATE,
    p_solde_tout_compte IN NUMBER DEFAULT NULL
)
AS
BEGIN
    archiverLoyersBail(p_id_bail);
    
    DELETE FROM LOYER 
    WHERE ID_BAIL = p_id_bail 
    AND DATE_PAIEMENT IS NULL;
    
    UPDATE BAIL
    SET ETAT = 'CLOTURE',
        DATE_FIN = p_date_fin,
        SOLDE_TOUT_COMPTE = p_solde_tout_compte
    WHERE ID_BAIL = p_id_bail;
    
    UPDATE LOGEMENT SET ID_BAIL = NULL WHERE ID_BAIL = p_id_bail;
    UPDATE GARAGE SET ID_BAIL = NULL WHERE ID_BAIL = p_id_bail;
    
    COMMIT;
END cloturerBail;
/

/* ====================== PROCEDURES REVALORISATION ET REGULARISATION ====================== */

/* Revalorise un loyer en verifiant les regles (12 mois, plafond IRL) - regle 7 */
CREATE OR REPLACE PROCEDURE revaloriserLoyer(
    p_id_bail       IN NUMBER,
    p_nouveau_loyer IN NUMBER,
    p_nouvel_irl    IN NUMBER,
    p_ancien_irl    IN NUMBER
)
AS
    v_loyer_actuel NUMBER;
    v_augmentation_pct NUMBER;
    v_augmentation_irl NUMBER;
    v_augmentation_max NUMBER;
    v_message_erreur VARCHAR2(500);
    C_PLAFOND_MAX CONSTANT NUMBER := 10;
BEGIN
    IF peutRevaloriser(p_id_bail) = 0 THEN
        v_message_erreur := getMessageErreurRevalorisation(p_id_bail);
        IF v_message_erreur IS NULL THEN
            v_message_erreur := 'Revalorisation non autorisee.';
        END IF;
        RAISE_APPLICATION_ERROR(-20107, v_message_erreur);
    END IF;
    
    SELECT LOYER_INITIAL INTO v_loyer_actuel
    FROM BAIL
    WHERE ID_BAIL = p_id_bail;
    
    IF v_loyer_actuel > 0 THEN
        v_augmentation_pct := ((p_nouveau_loyer - v_loyer_actuel) / v_loyer_actuel) * 100;
        
        IF p_ancien_irl > 0 THEN
            v_augmentation_irl := ((p_nouvel_irl - p_ancien_irl) / p_ancien_irl) * 100;
        ELSE
            v_augmentation_irl := 0;
        END IF;
        
        v_augmentation_max := LEAST(v_augmentation_irl, C_PLAFOND_MAX);
        
        IF v_augmentation_pct > (v_augmentation_max + 0.01) THEN
            RAISE_APPLICATION_ERROR(-20108, 
                'L''augmentation demandee (' || 
                ROUND(v_augmentation_pct, 2) || '%) depasse l''augmentation ' ||
                'maximale autorisee (' || ROUND(v_augmentation_max, 2) || '%). ' ||
                'Loyer maximum autorise : ' || 
                ROUND(v_loyer_actuel * (1 + v_augmentation_max/100), 2) || ' EUR.');
        END IF;
    END IF;
    
    UPDATE BAIL
    SET LOYER_INITIAL = p_nouveau_loyer,
        DATE_DERNIERE_REVALORISATION = SYSDATE
    WHERE ID_BAIL = p_id_bail;
    
    UPDATE LOYER
    SET MONTANT_LOYER = p_nouveau_loyer,
        INDICE_IRL = p_nouvel_irl,
        ANCIEN_IRL = p_ancien_irl
    WHERE ID_BAIL = p_id_bail
    AND UPPER(STATUT) = 'EN_ATTENTE';
    
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20109, 'Bail introuvable.');
END revaloriserLoyer;
/

/* Retourne le recap complet de regularisation pour un bail et une annee */
CREATE OR REPLACE PROCEDURE getRecapRegularisation(
    p_id_bail           IN NUMBER,
    p_annee             IN NUMBER,
    p_nom_locataire     OUT VARCHAR2,
    p_total_loyers      OUT NUMBER,
    p_total_provisions  OUT NUMBER,
    p_regularisation    OUT NUMBER,
    p_total             OUT NUMBER,
    p_nb_mois           OUT NUMBER,
    p_charges_eau       OUT NUMBER,
    p_charges_om        OUT NUMBER,
    p_charges_elec      OUT NUMBER,
    p_charges_entretien OUT NUMBER,
    p_total_charges     OUT NUMBER,
    p_nouvelle_provision OUT NUMBER
)
AS
    v_id_batiment NUMBER;
BEGIN
    BEGIN
        SELECT l.NOM_LOC || ' ' || NVL(l.PRENOM_LOC, '')
        INTO p_nom_locataire
        FROM LOCATAIRE l
        JOIN SIGNE s ON l.ID_LOCATAIRE = s.ID_LOCATAIRE
        WHERE s.ID_BAIL = p_id_bail
        AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_nom_locataire := 'Locataire inconnu';
    END;
    
    p_nb_mois := getNbMoisOccupation(p_id_bail, p_annee);
    p_total_loyers := getTotalLoyersBail(p_id_bail, p_annee);
    p_total_provisions := getTotalProvisionsBailAvecFallback(p_id_bail, p_annee);
    
    BEGIN
        SELECT ID_BATIMENT INTO v_id_batiment
        FROM LOGEMENT 
        WHERE ID_BAIL = p_id_bail 
        AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            BEGIN
                SELECT ID_BATIMENT INTO v_id_batiment
                FROM GARAGE 
                WHERE ID_BAIL = p_id_bail 
                AND ROWNUM = 1;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    v_id_batiment := NULL;
            END;
    END;
    
    IF v_id_batiment IS NOT NULL AND p_nb_mois > 0 THEN
        p_charges_eau := getChargesLocataire(p_id_bail, v_id_batiment, p_annee, 'EAU');
        p_charges_om := getChargesLocataire(p_id_bail, v_id_batiment, p_annee, 'OM');
        p_charges_elec := getChargesLocataire(p_id_bail, v_id_batiment, p_annee, 'ELEC');
        p_charges_entretien := getChargesLocataire(p_id_bail, v_id_batiment, p_annee, 'ENTRETIEN');
    ELSE
        p_charges_eau := 0;
        p_charges_om := 0;
        p_charges_elec := 0;
        p_charges_entretien := 0;
    END IF;
    
    p_total_charges := p_charges_eau + p_charges_om + p_charges_elec + p_charges_entretien;
    p_regularisation := ROUND(p_total_charges - p_total_provisions, 2);
    p_total := p_total_loyers + p_regularisation;
    
    IF p_nb_mois > 0 THEN
        p_nouvelle_provision := ROUND(p_total_charges / p_nb_mois, 2);
    ELSE
        p_nouvelle_provision := 0;
    END IF;
END getRecapRegularisation;
/

/* Retourne le recap des charges d'un batiment par type */
CREATE OR REPLACE PROCEDURE getRecapChargesBatiment(
    p_id_batiment       IN NUMBER,
    p_annee             IN NUMBER,
    p_adresse           OUT VARCHAR2,
    p_charges_eau       OUT NUMBER,
    p_charges_om        OUT NUMBER,
    p_charges_elec      OUT NUMBER,
    p_charges_entretien OUT NUMBER,
    p_total_charges     OUT NUMBER
)
AS
BEGIN
    BEGIN
        SELECT ADRESSE INTO p_adresse
        FROM BATIMENT
        WHERE ID_BATIMENT = p_id_batiment;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_adresse := 'Adresse non renseignee';
    END;
    
    p_charges_eau := getChargesEauBatiment(p_id_batiment, p_annee);
    p_charges_om := getChargesOMBatiment(p_id_batiment, p_annee);
    p_charges_elec := getChargesElecBatiment(p_id_batiment, p_annee);
    p_charges_entretien := getChargesEntretienBatiment(p_id_batiment, p_annee);
    p_total_charges := p_charges_eau + p_charges_om + p_charges_elec + p_charges_entretien;
END getRecapChargesBatiment;
/

/* Retourne le detail du solde pour affichage */
CREATE OR REPLACE PROCEDURE getDetailsSolde(
    p_id_bail IN NUMBER,
    p_loyers_impayes OUT NUMBER,
    p_caution OUT NUMBER,
    p_regul_montant OUT NUMBER
) AS
    v_id_batiment NUMBER;
    v_annee_courante NUMBER;
BEGIN
    SELECT NVL(SUM(MONTANT_LOYER + NVL(MONTANT_PROVISION, 0)), 0)
    INTO p_loyers_impayes
    FROM LOYER
    WHERE ID_BAIL = p_id_bail
    AND UPPER(STATUT) != 'PAYE';

    SELECT NVL(CAUTION, 0) 
    INTO p_caution 
    FROM BAIL 
    WHERE ID_BAIL = p_id_bail;

    v_annee_courante := EXTRACT(YEAR FROM SYSDATE);
    
    BEGIN
        SELECT ID_BATIMENT INTO v_id_batiment
        FROM LOGEMENT WHERE ID_BAIL = p_id_bail AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            BEGIN
                SELECT ID_BATIMENT INTO v_id_batiment
                FROM GARAGE WHERE ID_BAIL = p_id_bail AND ROWNUM = 1;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    v_id_batiment := NULL;
            END;
    END;

    IF v_id_batiment IS NOT NULL THEN
        p_regul_montant := getRegularisationLocataire(p_id_bail, v_id_batiment, v_annee_courante);
    ELSE
        p_regul_montant := 0;
    END IF;
END;
/

/* ====================== PROCEDURES SAISIE CHARGES ====================== */

/* Saisit ou met a jour une charge pour un batiment */
CREATE OR REPLACE PROCEDURE saisirChargeImmeuble(
    p_id_batiment   IN NUMBER,
    p_annee         IN NUMBER,
    p_type_charge   IN VARCHAR2,
    p_montant       IN NUMBER,
    p_pct_recup     IN NUMBER DEFAULT 100
)
AS
    v_id_charge NUMBER;
    v_date_charge DATE;
BEGIN
    v_date_charge := TO_DATE('31/12/' || p_annee, 'DD/MM/YYYY');
    
    BEGIN
        SELECT ID_CHARGE INTO v_id_charge
        FROM CHARGES
        WHERE ID_BATIMENT = p_id_batiment
        AND UPPER(TYPE_CHARGES) = UPPER(p_type_charge)
        AND EXTRACT(YEAR FROM DATE_CHARGE) = p_annee
        AND ROWNUM = 1;
        
        UPDATE CHARGES
        SET MONTANT = p_montant,
            PCT_RECUPERABLE = p_pct_recup
        WHERE ID_CHARGE = v_id_charge;
        
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT NVL(MAX(ID_CHARGE), 0) + 1 INTO v_id_charge FROM CHARGES;
            
            INSERT INTO CHARGES (
                ID_CHARGE, NATURE, MONTANT, DATE_CHARGE, 
                TYPE_CHARGES, PCT_RECUPERABLE, ID_BATIMENT
            ) VALUES (
                v_id_charge,
                'Charge ' || p_type_charge || ' ' || p_annee,
                p_montant,
                v_date_charge,
                UPPER(p_type_charge),
                p_pct_recup,
                p_id_batiment
            );
    END;
    
    COMMIT;
END saisirChargeImmeuble;
/

/* Saisit les 4 types de charges en une seule operation */
CREATE OR REPLACE PROCEDURE saisirToutesChargesImmeuble(
    p_id_batiment       IN NUMBER,
    p_annee             IN NUMBER,
    p_montant_eau       IN NUMBER,
    p_montant_om        IN NUMBER,
    p_montant_elec      IN NUMBER,
    p_montant_entretien IN NUMBER
)
AS
BEGIN
    saisirChargeImmeuble(p_id_batiment, p_annee, 'EAU', p_montant_eau, 100);
    saisirChargeImmeuble(p_id_batiment, p_annee, 'OM', p_montant_om, 100);
    saisirChargeImmeuble(p_id_batiment, p_annee, 'ELEC', p_montant_elec, 100);
    saisirChargeImmeuble(p_id_batiment, p_annee, 'ENTRETIEN', p_montant_entretien, 100);
    
    COMMIT;
END saisirToutesChargesImmeuble;
/

/* ====================== PROCEDURES GARANTS ET ASSURANCES ====================== */

/* Ajoute un garant a un bail existant */
CREATE OR REPLACE PROCEDURE ajouterGarantAuBail(
    p_id_bail       IN NUMBER,
    p_nom           IN VARCHAR2,
    p_prenom        IN VARCHAR2,
    p_adresse       IN VARCHAR2,
    p_email         IN VARCHAR2,
    p_telephone     IN VARCHAR2
)
AS
    v_id_garant VARCHAR2(50);
BEGIN
    v_id_garant := 'G-' || TO_CHAR(SYSTIMESTAMP, 'YYYYMMDDHH24MISSFF3');
    
    INSERT INTO GARANT (ID_GARANT, NOM_GARANT, PRENOM_GARANT, ADRESSE_GARANT, MAIL_GARANT, TEL_GARANT)
    VALUES (v_id_garant, p_nom, p_prenom, p_adresse, p_email, p_telephone);
    
    INSERT INTO AJOUTE (ID_BAIL, ID_GARANT)
    VALUES (p_id_bail, v_id_garant);
    
    COMMIT;
END ajouterGarantAuBail;
/

/* Supprime un garant et ses liaisons */
CREATE OR REPLACE PROCEDURE supprimerGarant(
    p_id_garant IN VARCHAR2
)
AS
BEGIN
    DELETE FROM AJOUTE WHERE ID_GARANT = p_id_garant;
    DELETE FROM GARANT WHERE ID_GARANT = p_id_garant;
    COMMIT;
END supprimerGarant;
/

/* Ajoute une assurance a un logement avec echeance */
CREATE OR REPLACE PROCEDURE ajouterAssuranceLogement(
    p_num_assurance  IN NUMBER,
    p_nom_compagnie  IN VARCHAR2,
    p_type           IN VARCHAR2,
    p_prime_base     IN NUMBER,
    p_date_effet     IN DATE,
    p_date_echeance  IN DATE,
    p_id_logement    IN NUMBER
)
AS
BEGIN
    INSERT INTO ASSURANCE (NUMASSURANCE, NOM_COMPAGNIE, TYPE, PRIMEBASE, DATE_EFFET, ID_LOGEMENT)
    VALUES (p_num_assurance, p_nom_compagnie, p_type, p_prime_base, p_date_effet, p_id_logement);
    
    INSERT INTO ECHEANCE (NUMASSURANCE, DATE_, MONTANT)
    VALUES (p_num_assurance, p_date_echeance, p_prime_base);
    
    COMMIT;
END ajouterAssuranceLogement;
/

/* Ajoute une assurance a un garage */
CREATE OR REPLACE PROCEDURE ajouterAssuranceGarage(
    p_num_assurance  IN NUMBER,
    p_nom_compagnie  IN VARCHAR2,
    p_type           IN VARCHAR2,
    p_prime_base     IN NUMBER,
    p_date_effet     IN DATE,
    p_date_echeance  IN DATE,
    p_id_garage      IN NUMBER
)
AS
BEGIN
    INSERT INTO ASSURANCE (NUMASSURANCE, NOM_COMPAGNIE, TYPE, PRIMEBASE, DATE_EFFET, ID_GARAGE)
    VALUES (p_num_assurance, p_nom_compagnie, p_type, p_prime_base, p_date_effet, p_id_garage);
    
    INSERT INTO ECHEANCE (NUMASSURANCE, DATE_, MONTANT)
    VALUES (p_num_assurance, p_date_echeance, p_prime_base);
    
    COMMIT;
END ajouterAssuranceGarage;
/

/* Ajoute une assurance a un batiment (PNO, multirisque immeuble) */
CREATE OR REPLACE PROCEDURE ajouterAssuranceBatiment(
    p_id_batiment     IN NUMBER,
    p_num_assurance   IN NUMBER,
    p_nom_compagnie   IN VARCHAR2,
    p_type            IN VARCHAR2,
    p_prime_base      IN NUMBER,
    p_date_effet      IN DATE,
    p_date_echeance   IN DATE DEFAULT NULL
)
AS
    v_exists NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_exists
    FROM BATIMENT WHERE ID_BATIMENT = p_id_batiment;
    
    IF v_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20201, 'Batiment inexistant: ' || p_id_batiment);
    END IF;
    
    INSERT INTO ASSURANCE (
        NUMASSURANCE, TYPE, PRIMEBASE, NOM_COMPAGNIE, 
        DATE_EFFET, ID_BATIMENT
    ) VALUES (
        p_num_assurance, p_type, p_prime_base, p_nom_compagnie,
        p_date_effet, p_id_batiment
    );
    
    IF p_date_echeance IS NOT NULL THEN
        INSERT INTO ECHEANCE (NUMASSURANCE, DATE_, MONTANT)
        VALUES (p_num_assurance, p_date_echeance, p_prime_base);
    END IF;
    
    COMMIT;
END ajouterAssuranceBatiment;
/

/* Supprime une assurance et ses echeances */
CREATE OR REPLACE PROCEDURE supprimerAssurance(
    p_num_assurance IN NUMBER
)
AS
BEGIN
    DELETE FROM ECHEANCE WHERE NUMASSURANCE = p_num_assurance;
    DELETE FROM ASSURANCE WHERE NUMASSURANCE = p_num_assurance;
    COMMIT;
END supprimerAssurance;
/

/* Ajoute un mouvement de caution (versement, restitution, retenue) */
CREATE OR REPLACE PROCEDURE ajouterMouvementCaution(
    p_id_bail         IN NUMBER,
    p_date_mouvement  IN DATE,
    p_type_mouvement  IN VARCHAR2,
    p_montant         IN NUMBER,
    p_moyen_paiement  IN VARCHAR2,
    p_observations    IN VARCHAR2
)
AS
    v_new_id NUMBER;
BEGIN
    SELECT NVL(MAX(ID_MOUVEMENT), 0) + 1 INTO v_new_id FROM MOUVEMENT_CAUTION;
    
    INSERT INTO MOUVEMENT_CAUTION (ID_MOUVEMENT, DATE_MOUVEMENT, TYPE_MOUVEMENT, MONTANT, MOYEN_PAIEMENT, OBSERVATIONS, ID_BAIL)
    VALUES (v_new_id, p_date_mouvement, p_type_mouvement, p_montant, p_moyen_paiement, p_observations, p_id_bail);
    
    COMMIT;
END ajouterMouvementCaution;
/

/* Ajoute une facture de travaux a un logement */
CREATE OR REPLACE PROCEDURE ajouterFactureLogement(
    p_id_facture        IN NUMBER,
    p_id_logement       IN NUMBER,
    p_siret             IN NUMBER,
    p_nature            IN VARCHAR2,
    p_montant_ttc       IN NUMBER,
    p_date_emission     IN DATE,
    p_travaux           IN VARCHAR2 DEFAULT NULL,
    p_deductible        IN NUMBER DEFAULT 1,
    p_montant_devis     IN NUMBER DEFAULT NULL
)
AS
    v_id_batiment NUMBER;
BEGIN
    SELECT ID_BATIMENT INTO v_id_batiment
    FROM LOGEMENT WHERE ID_LOGEMENT = p_id_logement;
    
    INSERT INTO FACTURE (
        ID_FACTURE, SIRET, NATURE, MONTANT_TTC, DATE_EMISSION,
        TRAVAUX, DEDUCTIBLE_IMPOT, ID_LOGEMENT, ID_BATIMENT,
        MONTANTDEVIS, STATUT_PAIEMENT
    ) VALUES (
        p_id_facture, p_siret, p_nature, p_montant_ttc, p_date_emission,
        p_travaux, p_deductible, p_id_logement, v_id_batiment,
        p_montant_devis, 'A payer'
    );
    
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20202, 'Logement inexistant: ' || p_id_logement);
END ajouterFactureLogement;
/

COMMIT;

/* FIN DU SCRIPT 02_FONCTIONS_PROCEDURES.sql */
