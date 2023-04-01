/**<p>*********************************************************************************************************************
 * <h1>SqlBuilderDelete</h1>
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

public abstract class SqlBuilderDelete {
    protected String table;
    protected final Map<Column, Object> clauses = new LinkedHashMap<>();

    /**
     * @param table name of the table to delete data from
     * @return the builder this was invoked on
     */
    public SqlBuilderDelete table(final String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds a clause to the delete statement to delete only certain rows.
     *
     * @param column a {@link Column} representing a column of the table in the db
     * @param value the desired value of the column. Can be null
     * @return the builder this was invoked on
     */
    public SqlBuilderDelete where(final Column column, final Object value) {
        this.clauses.put(column, value);
        return this;
    }

    /**
     * Builds a DELETE statement for the table and where clauses supplied to this builder.
     * Only be visible for testing.
     *
     * @return DELETE statement
     */
    protected String createStatement() {
        return new StringJoiner(" ", DELETE, ";")
            .add(FROM)
            .add(table)
            .add(WHERE)
            .add(clauses.entrySet()
                .stream()
                .map(e -> e.getKey().getName() + (e.getValue() == null ? " IS NULL" : " = ?"))
                .collect(Collectors.joining(" AND ")))
            .toString();
    }

    /**
     * Builds the PreparedStatement and sets the necessary values for any where clauses
     *
     * @param connection connection to the db to perform this statement on
     * @return a prepared statement that can be executed
     * @throws SQLiteGdxException if the table name was empty or an error occurred performing the query
     */
    public abstract OptionalInt delete(final Connection connection) throws SQLiteGdxException, SQLException;

    /**
     * Builds the SqliteStatement and sets the necessary values for any where clauses
     *
     * @param  androidDatabase Android Sqlite Database
     * @return a prepared statement that can be executed
     * @throws SQLiteGdxException if the table name was empty or an error occurred performing the query
     */
    public abstract OptionalInt delete(final Object androidDatabase) throws SQLiteGdxException;

}
