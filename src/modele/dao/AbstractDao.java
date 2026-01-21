package modele.dao;

import java.sql.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Classe DAO abstraite générique
 */
public abstract class AbstractDao<T, ID> implements AutoCloseable {

    protected Connection cn;
    protected final String tableName;
    protected final String primaryKeyColumn;
    
    /**
     * Pattern pour valider les noms de table et colonnes SQL
     */
    private static final Pattern VALID_SQL_IDENTIFIER = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    /**
     * Constructeur avec nom de table et colonne PK
     */
    protected AbstractDao(String tableName, String primaryKeyColumn) throws SQLException {
        validateSqlIdentifier(tableName, "tableName");
        validateSqlIdentifier(primaryKeyColumn, "primaryKeyColumn");
        
        this.tableName = tableName;
        this.primaryKeyColumn = primaryKeyColumn;
        initConnection();
    }

    /**
     * Valide qu'un identifiant SQL est sécurisé
     */
    private void validateSqlIdentifier(String identifier, String paramName) throws SQLException {
        if (identifier == null || identifier.isEmpty()) {
            throw new SQLException("Le paramètre " + paramName + " ne peut pas être null ou vide");
        }
        if (!VALID_SQL_IDENTIFIER.matcher(identifier).matches()) {
            throw new SQLException("Identifiant SQL invalide pour " + paramName + ": " + identifier + 
                ". Seuls les caractères alphanumériques et underscore sont autorisés.");
        }
        // Vérification supplémentaire: longueur maximale
        if (identifier.length() > 128) {
            throw new SQLException("Identifiant SQL trop long pour " + paramName + ": max 128 caractères");
        }
    }

    /**
     * Initialise ou réinitialise la connexion
     */
    protected void initConnection() throws SQLException {
        cn = UtOracleDataSource.getConnection();
        if (cn == null || cn.isClosed()) {
            UtOracleDataSource.creerAcces();
            cn = UtOracleDataSource.getConnection();
        }
    }

    /**
     * Définit une connexion externe 
     */
    public void setConnection(Connection connection) {
        this.cn = connection;
    }

    /**
     * Retourne la connexion actuelle
     */
    public Connection getConnection() {
        return cn;
    }


    // MÉTHODES ABSTRAITES


    /**
     * Crée une nouvelle entité en BDD
     */
    public abstract void create(T entity) throws SQLException;

    /**
     * Met à jour une entité existante
     */
    public abstract void update(T entity) throws SQLException;

    /**
     * Supprime une entité
     */
    public abstract void delete(T entity) throws SQLException;

    /**
     * Recherche une entité par son identifiant
     */
    public abstract T findById(ID id) throws SQLException;

    /**
     * Retourne toutes les entités
     */
    public abstract List<T> findAll() throws SQLException;

    /**
     * Mappe un ResultSet vers une entité
     */
    protected abstract T mapResultSet(ResultSet rs) throws SQLException;


    // MÉTHODES COMMUNES - Implémentées ici


    /**
     * Vérifie si une entité existe par son ID
     */
    public boolean exists(ID id) throws SQLException {
        // SECURITY: tableName et primaryKeyColumn ont été validés dans le constructeur
        String sql = "SELECT 1 FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            setIdParameter(ps, 1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Compte le nombre total d'entités
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Supprime une entité par son ID
     */
    public void deleteById(ID id) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            setIdParameter(ps, 1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Génère le prochain ID via la fonction PL/SQL getNextId
     */
    public long getNextId() throws SQLException {
        String sql = "{? = call getNextId(?, ?)}";
        try (CallableStatement cs = cn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, tableName);
            cs.setString(3, primaryKeyColumn);
            cs.execute();
            return cs.getLong(1);
        }
    }


    // UTILITAIRES DE PARAMÉTRAGE


    /**
     * Configure le paramètre ID selon son type
     */

    protected void setIdParameter(PreparedStatement ps, int index, ID id) throws SQLException {
        if (id instanceof Long) {
            ps.setLong(index, (Long) id);
        } else if (id instanceof Integer) {
            ps.setInt(index, (Integer) id);
        } else if (id instanceof String) {
            ps.setString(index, (String) id);
        } else {
            ps.setObject(index, id);
        }
    }

    /**
     * Configure un paramètre Long nullable
     */
    protected void setLongOrNull(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value != null) {
            ps.setLong(index, value);
        } else {
            ps.setNull(index, Types.NUMERIC);
        }
    }

    /**
     * Configure un paramètre Double nullable
     */
    protected void setDoubleOrNull(PreparedStatement ps, int index, Double value) throws SQLException {
        if (value != null) {
            ps.setDouble(index, value);
        } else {
            ps.setNull(index, Types.NUMERIC);
        }
    }

    /**
     * Configure un paramètre Integer nullable
     */
    protected void setIntOrNull(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null) {
            ps.setInt(index, value);
        } else {
            ps.setNull(index, Types.INTEGER);
        }
    }

    /**
     * Configure un paramètre Date nullable
     */
    protected void setDateOrNull(PreparedStatement ps, int index, java.util.Date value) throws SQLException {
        if (value != null) {
            ps.setDate(index, new java.sql.Date(value.getTime()));
        } else {
            ps.setNull(index, Types.DATE);
        }
    }

    /**
     * Configure un paramètre Boolean comme Integer (1/0)
     */
    protected void setBooleanAsInt(PreparedStatement ps, int index, Boolean value) throws SQLException {
        if (value != null) {
            ps.setInt(index, value ? 1 : 0);
        } else {
            ps.setNull(index, Types.INTEGER);
        }
    }


    // UTILITAIRES DE LECTURE


    /**
     * Lit un Long nullable depuis le ResultSet
     */
    protected Long getLongOrNull(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    /**
     * Lit un Double nullable depuis le ResultSet
     */
    protected Double getDoubleOrNull(ResultSet rs, String columnName) throws SQLException {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? null : value;
    }

    /**
     * Lit un Integer nullable depuis le ResultSet
     */
    protected Integer getIntOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    /**
     * Lit un Boolean depuis un Integer (1/0)
     */
    protected Boolean getBooleanFromInt(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : (value == 1);
    }


    // GESTION DE LA CONNEXION


    /**
     * Vérifie si la connexion est valide
     */
    public boolean isConnectionValid() throws SQLException {
        return cn != null && !cn.isClosed() && cn.isValid(5);
    }

    /**
     * Ferme la connexion
     */
    @Override
    public void close() throws SQLException {
        if (cn != null && !cn.isClosed()) {
            cn.close();
        }
    }


    // MÉTHODES TRANSACTIONNELLES


    /**
     * Démarre une transaction
     */
    public void beginTransaction() throws SQLException {
        cn.setAutoCommit(false);
    }

    /**
     * Valide la transaction
     */
    public void commit() throws SQLException {
        cn.commit();
        cn.setAutoCommit(true);
    }

    /**
     * Annule la transaction
     */
    public void rollback() throws SQLException {
        cn.rollback();
        cn.setAutoCommit(true);
    }
}
