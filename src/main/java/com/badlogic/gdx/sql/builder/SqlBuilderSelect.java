/**<p>*********************************************************************************************************************
 * <h1>SqlBuilderSelect</h1>
 * @since 20201014
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20201014  Original author       Evan White
 *           replacing SelectBuilder
 *           changed to abstract class
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 *
 **********************************************************************************************************************</p>*/

package com.badlogic.gdx.sql.builder;

import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.SQLiteGdxException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.badlogic.gdx.sql.builder.Keywords.*;
public abstract class SqlBuilderSelect<T> {
    protected String table;
    protected final List<String> columns = new ArrayList<>();
    protected final Map<Column, Object> clauses = new LinkedHashMap<>();
    protected final Map<String, OrderType> orders = new HashMap<>();
    protected final List<String> groupings = new ArrayList<>();
    protected final ResultMapper<T> resultMapper;

    /**
     * Create a SelectBuilder with a custom {@link ResultMapper}
     *
     * @param resultMapper maps the expected output to a desired return type
     */
    public SqlBuilderSelect(final ResultMapper<T> resultMapper) {
        this.resultMapper = resultMapper;
    }

    /**
     * Create a SelectBuilder that will try to map the expected result to the fields in a class.
     * This uses reflection to find class fields that match column names. Columns that have no
     * matching field are skipped.
     *
     * @param tClass the class of the desired output objects
     */
    public SqlBuilderSelect(final Class<T> tClass) {
        this.resultMapper = new ClassResultMapper<>(tClass);
    }

    /**
     * @param table name of the table to pull data from
     * @return the builder this was invoked on
     */
    public SqlBuilderSelect<T> table(final String table) {
        this.table = table;
        return this;
    }

    /**
     * Adds a column to be selected from the db. Never calling this results in all columns being selected.
     *
     * @param column a {@link Column} representing a column of the table in the db
     * @return the builder this was invoked on
     */
    public SqlBuilderSelect<T> select(final Column column) {
        this.columns.add(column.getName());
        return this;
    }

    /**
     * Adds a clause to the select statement to filter results.
     *
     * @param column a {@link Column} representing a column of the table in the db
     * @param value the desired value of the column. Can be null
     * @return the builder this was invoked on
     */
    public SqlBuilderSelect<T> where(final Column column, final Object value) {
        clauses.put(column, value);
        return this;
    }

    /**
     * Adds a column to group the query results by.
     *
     * @param column a {@link Column} representing a column of the table in the db
     * @return the builder this was invoked on
     */
    public SqlBuilderSelect<T> groupBy(final Column column) {
        groupings.add(column.getName());
        return this;
    }

    /**
     * Adds a column to order the query results by.
     *
     * @param column a {@link Column} representing a column of the table in the db
     * @param orderType how to order the column. Can be null
     * @return the builder this was invoked on
     */
    public SqlBuilderSelect<T> orderBy(final Column column, final OrderType orderType) {
        orders.put(column.getName(), orderType);
        return this;
    }

    /**
     * Builds a SELECT statement for the table, selected columns, and where clauses supplied to this builder.
     * Only be visible for testing.
     *
     * @return SELECT statement
     */
    public String createStatement() {
        final StringJoiner sj = new StringJoiner(" ", SELECT, ";");
        if (columns.isEmpty()) {
            sj.add("*");
        } else {
            sj.add(String.join(", ", columns));
        }
        sj.add(FROM).add(table);
        if (!clauses.isEmpty()) {
            sj.add(WHERE)
                .add(clauses.entrySet().stream()
                    .map(e -> e.getKey().getName() + (e.getValue() == null ? " IS NULL" : " = ?"))
                    .collect(Collectors.joining(" AND ")));
        }
        if (!groupings.isEmpty()) {
            sj.add(GROUP_BY)
                .add(String.join(", ", groupings));
        }
        if (!orders.isEmpty()) {
            sj.add(ORDER_BY)
                .add(orders.entrySet().stream()
                    .map(e -> e.getKey() + (e.getValue() == null ? "" : " " + e.getValue().name()))
                    .collect(Collectors.joining(", ")));
        }
        return sj.toString();
    }

    protected abstract Object preparedStatementAndroid(Object androidDatabase) throws SQLiteGdxException;
    protected abstract Object preparedStatementWin(Connection connection) throws SQLiteGdxException, SQLException;

    /** Runs on Windows */
    public abstract DatabaseCursor getCursor(final Connection connection) throws SQLiteGdxException, SQLException;

    /** Runs on Windows */
    public abstract DatabaseCursor getCursor(final DatabaseCursor cursor, final Connection connection) throws SQLiteGdxException, SQLException;

    /** Runs on Android */
    public abstract DatabaseCursor getCursor(Object androidDatabase) throws SQLiteGdxException;

    /** Runs on Android */
    public abstract DatabaseCursor getCursor(DatabaseCursor cursor, Object androidDatabase) throws SQLiteGdxException;

}
