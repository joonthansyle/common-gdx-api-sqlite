/**<p>*********************************************************************************************************************
 * <h1>SqlBuilderInsert</h1>
 * @since 20201014
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20201014  Original author       Evan White
 *           replacing InsertBuilder
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
import java.util.OptionalLong;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.badlogic.gdx.sql.builder.Keywords.*;

public abstract class SqlBuilderInsert {
    protected String table;
    protected final Map<Column, Object> values = new LinkedHashMap<>();

    /**
     * @param table name of the table to insert data into
     * @return the builder this was invoked on
     */
    public SqlBuilderInsert table(final String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds a column to be inserted in the db.
     *
     * @param column a {@link Column} representing a column of the table in the db
     * @param value the desired value of the column. Can be null
     * @return the builder this was invoked on
     */
    public SqlBuilderInsert value(final Column column, final Object value) {
        this.values.put(column, value);
        return this;
    }

    /**
     * Builds a INSERT statement for the select columns in the table supplied to this builder.
     * Only be visible for testing.
     *
     * @return INSERT statement
     */
    public String createStatement() {
        final StringJoiner sj = new StringJoiner(" ", INSERT, ";")
            .add(INTO)
            .add(table);
        if (!values.isEmpty()) {
            sj.add(values.keySet()
                    .stream()
                    .map(Column::getName)
                    .collect(Collectors.joining(", ", "(", ")")))
                .add(VALUES)
                .add(values.values()
                    .stream()
                    .map(p -> "?")
                    .collect(Collectors.joining(", ", "(", ")")));
        }
        return sj.toString();
    }

    public abstract OptionalLong insert(final Connection connection) throws SQLiteGdxException, SQLException;
    public abstract OptionalLong insert(final Object androidDatabase) throws SQLiteGdxException;

}
