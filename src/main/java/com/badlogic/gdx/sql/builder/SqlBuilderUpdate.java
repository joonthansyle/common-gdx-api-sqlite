/**<p>*********************************************************************************************************************
 * <h1>SqlBuilderUpdate</h1>
 * @since 20201014
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20201014  Original author       Evan White
 *           replacing DeleteBuilder
 *           changed to abstract class
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 *
 **********************************************************************************************************************</p>*/
package com.badlogic.gdx.sql.builder;

import com.badlogic.gdx.sql.SQLiteGdxException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.badlogic.gdx.sql.builder.Keywords.*;
public abstract class SqlBuilderUpdate {
    protected String table;
    protected final Map<Column, Object> values = new LinkedHashMap<>();
    protected final Map<Column, Object> clauses = new LinkedHashMap<>();

    /**
     * @param table name of the table to update data on
     * @return the builder this was invoked on
     */
    public SqlBuilderUpdate table(final String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds a column to be updated in the db.
     *
     * @param column a {@link Column} representing a column of the table in the db
     * @param value the desired value of the column. Can be null
     * @return the builder this was invoked on
     */
    public SqlBuilderUpdate value(final Column column, final Object value) {
        this.values.put(column, value);
        return this;
    }

    /**
     * Adds a clause to the update statement to update only certain rows.
     *
     * @param column a {@link Column} representing a column of the table in the db
     * @param value the desired value of the column. Can be null
     * @return the builder this was invoked on
     */
    public SqlBuilderUpdate where(final Column column, final Object value) {
        clauses.put(column, value);
        return this;
    }

    /**
     * Builds a UPDATE statement for the select columns in the table and where clauses supplied to this builder.
     * Only be visible for testing.
     *
     * @return DELETE statement
     */
    protected String createStatement() {
        final StringJoiner sj = new StringJoiner(" ", UPDATE, ";")
            .add(table)
            .add(SET);
        sj.add(values.keySet().stream()
            .map(s -> s.getName() + " = ?")
            .collect(Collectors.joining(", ")));
        if (!clauses.isEmpty()) {
            sj.add(WHERE)
                .add(clauses.entrySet()
                    .stream()
                    .map(s -> s.getKey().getName() + (s.getValue() == null ? " IS NULL" : " = ?"))
                    .collect(Collectors.joining(" AND ")));
        }
        return sj.toString();
    }

    /**
     * Builds the PreparedStatement and sets the necessary values for any where clauses
     *
     * @param connection connection to the db to perform this statement on
     * @return a prepared statement that can be executed
     * @throws SQLException if the table name was empty or an error occurred performing the query
     */
    public abstract OptionalInt update(final Connection connection) throws SQLException, SQLiteGdxException;
    public abstract OptionalInt update(final Object androidDatabase) throws SQLiteGdxException;
}
