# 🏢 Gestion Immobilière

Une application complète de gestion de biens immobiliers (non meublés) développée en Java. Ce projet a été réalisé dans le cadre d'une SAE à l'IUT de Toulouse.

L'objectif est de fournir aux propriétaires privés un outil pour gérer leurs biens, locataires, baux et la complexité administrative liée à la régularisation des charges et aux impôts.

---

## 🛠️ Stack Technique

* **Langage :** Java (JDK 17+)
* **Interface Graphique (GUI) :** Swing (conçu avec **WindowBuilder**)
* **Base de données :** Oracle Database
* **Backend Logic :** PL/SQL (Procédures stockées, Triggers, Fonctions)
* **Architecture :** MVC (Modèle-Vue-Contrôleur)
* **Accès aux données :** JDBC avec le pattern **DAO** (Data Access Object)

---

## 🚀 Fonctionnalités Principales

L'application couvre l'ensemble du cycle de vie d'une location en monopropriété :

### 🏠 Gestion des Biens & Locataires
* **Hiérarchie des biens :** Gestion des Bâtiments, Logements (appartements) et Annexes (garages).
* **Locataires :** Suivi des locataires actuels et archivage des anciens pour l'historique et les statistiques.
* **Baux :** Gestion des entrées/sorties, état des lieux et archivage des documents numérisés (baux, diagnostics).

### 💰 Gestion Comptable & Charges (PL/SQL)
* **Régularisation des charges :** Calcul automatique (via SQL/PLSQL) de la différence entre provisions et charges réelles (Eau, Ordures ménagères, Entretien).
* **Loyer & Indexation :** Révision des loyers basée sur l'indice IRL et gestion des provisions.
* **Solde de tout compte :** Calcul final lors du départ d'un locataire incluant dégradations, caution et régularisations.
* **Import de données :** Capacité d'importer les paiements de loyers via fichiers CSV.

### 🛠️ Travaux & Fiscalité
* **Suivi des travaux :** Gestion des devis, factures et association aux entreprises (SIRET, corps de métier).
* **Fiscalité :** Identification fiscale des locaux et préparation des données pour la déclaration des revenus fonciers (2044 ou Microfoncier).

---

## 🏗️ Architecture du Projet

Ce projet respecte une séparation stricte des responsabilités :

1.  **Vue (Java/Swing) :** Interface utilisateur riche permettant la saisie et la visualisation (Tableaux de bord, Formulaires).
2.  **Contrôleur (Java) :** Orchestre les actions utilisateur et manipule les modèles.
3.  **Modèle (Java + DAO) :** Les classes métier reflètent la structure de la base de données. Le pattern DAO isole les requêtes SQL du code métier.
4.  **Base de Données (Oracle + PL/SQL) :**
    * Contrairement à une approche classique où tout le calcul est fait en Java, ici **la logique métier complexe réside dans la base de données**.
    * Utilisation intensive de **Triggers** pour garantir l'intégrité des données (ex: cohérence des dates, règles de gestion).
    * **Procédures stockées** pour les calculs lourds (Régularisation annuelle des charges).

---

## 💾 Modèle de Données (MCD)

Le projet gère les contraintes suivantes :
* Distinction entre bien physique et bien fiscal.
* Gestion des compteurs (eau, électricité) et de leurs relevés.
* Association des factures aux devis et aux entreprises.

---

## 📦 Installation et Lancement

1.  **Pré-requis :** Avoir Java et une instance Oracle Database installés.
2.  **Base de données :** Exécuter les scripts SQL fournis dans le dossier `/sql` pour créer les tables, triggers et procédures.
3.  **Configuration :** Modifier le fichier `modele/dao/UtOracleDataSource.java` avec vos identifiants Oracle.
4.  **Exécution :** Lancer la classe `vue/Principal/page_principale.java`.

---
