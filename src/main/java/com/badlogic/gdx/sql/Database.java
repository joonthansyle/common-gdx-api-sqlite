/**<p>*********************************************************************************************************************
 * <h1>Database</h1>
 * @since 20150926
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20150926  Original author       Rafay Aleem
 * 20230329  Deprecated methods execSQL and rawQuery
 *           Added getCursor method for select
 *           Added insert method
 *           Added update method
 *           Added delete method
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 *
 **********************************************************************************************************************</p>*/
package com.badlogic.gdx.sql;

import com.badlogic.gdx.sql.builder.SqlBuilderDelete;
import com.badlogic.gdx.sql.builder.SqlBuilderInsert;
import com.badlogic.gdx.sql.builder.SqlBuilderSelect;
import com.badlogic.gdx.sql.builder.SqlBuilderUpdate;

import java.sql.SQLException;
import java.util.OptionalInt;
import java.util.OptionalLong;
public interface Database {

    /** This method is needed to be called only once before any database related activity can be performed. The method performs the
     * necessary procedures for the database. However, a database will not be opened/created until
     * {@link Database#openOrCreateDatabase()} is called. */
    public void setupDatabase ();

    /** Opens an already existing database or creates a new database if it doesn't already exist.
     * @throws SQLiteGdxException */
    public void openOrCreateDatabase () throws SQLiteGdxException;

    /** Closes the opened database and releases all the resources related to this database.
     * @throws SQLiteGdxException */
    public void closeDatabase () throws SQLiteGdxException;

    /** Execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
     * @Deprecated
     * @param sql the SQL statement to be executed. Multiple statements separated by semicolons are not supported.
     * @throws SQLiteGdxException */
    @Deprecated
    public void execSQL (String sql) throws SQLiteGdxException;

    /** Runs the provided SQL and returns a {@link DatabaseCursor} over the result set.
     * @Deprecated
     * @param sql the SQL query. The SQL string must not be ; terminated
     * @return {@link DatabaseCursor}
     * @throws SQLiteGdxException */
    @Deprecated
    public DatabaseCursor rawQuery (String sql) throws SQLiteGdxException;

    /** Runs the provided SQL and returns the same {@link DatabaseCursor} that was passed to this method. Use this method when you
     * want to avoid reallocation of {@link DatabaseCursor} object. Note that you shall only pass the {@link DatabaseCursor} object
     * that was previously returned by a rawQuery method. Creating your own {@link DatabaseCursor} and then passing it as an object
     * will not work.
     * @Deprecated
     * @param cursor existing {@link DatabaseCursor} object
     * @param sql the SQL query. The SQL string must not be ; terminated
     * @return the passed {@link DatabaseCursor}.
     * @throws SQLiteGdxException */
    @Deprecated
    public DatabaseCursor rawQuery (DatabaseCursor cursor, String sql) throws SQLiteGdxException;
    public DatabaseCursor getCursor(SqlBuilderSelect<?> builder) throws SQLiteGdxException, SQLException;
    public DatabaseCursor getCursor(DatabaseCursor cursor, SqlBuilderSelect<?> builder) throws SQLiteGdxException, SQLException;
    public OptionalLong insert(SqlBuilderInsert builder) throws SQLiteGdxException, SQLException;
    public OptionalInt delete(SqlBuilderDelete builder) throws SQLiteGdxException, SQLException;
    public OptionalInt update(SqlBuilderUpdate builder) throws SQLiteGdxException, SQLException;
}

