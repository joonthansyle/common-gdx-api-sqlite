/**<p>*********************************************************************************************************************
 * <h1>LoadSqlite</h1>
 * @since 20230111
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20230111  @version 01           @author ORIGINAL AUTHOR
 *           Initial version
 *           Use Handler to Load Sql
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 *
 **********************************************************************************************************************</p>*/
package com.badlogic.gdx.sql;

import com.badlogic.gdx.sql.builder.SqlBuilderDelete;
import com.badlogic.gdx.sql.builder.SqlBuilderInsert;
import com.badlogic.gdx.sql.builder.SqlBuilderSelect;
import com.badlogic.gdx.sql.builder.SqlBuilderUpdate;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class LoadSqlite implements Closeable {
    private static final String TAG = LoadSqlite.class.getCanonicalName();
    public static final String NAME = TAG;

    private static Handler db;
    private static LoadSqlite instance;

    public LoadSqlite(Handler handler){
        if(db != null) return;
        db = handler;
        instance = this;
    }

    public static LoadSqlite getInstance(){
        if(instance == null ) return null;
        return instance;
    }

    @Override
    public void close() {
        db.close();
    }

    @Deprecated
    public DatabaseCursor rawQuery(String s){
        return db.rawQuery(s);
    }

    @Deprecated
    public DatabaseCursor rawQuery(DatabaseCursor cursor, String s){
        return db.rawQuery(cursor, s);
    }

    @Deprecated
    public void execSQL(String sql){db.execSQL(sql);}

    public DatabaseCursor getCursor(SqlBuilderSelect builder) throws SQLiteGdxException, SQLException {
        return db.getCursor(builder);
    }
    public DatabaseCursor getCursor(DatabaseCursor cursor, SqlBuilderSelect builder) throws SQLiteGdxException, SQLException{
        return db.getCursor(cursor, builder);
    }

    public OptionalLong insert(SqlBuilderInsert builder) throws SQLiteGdxException, SQLException{
        return db.insert(builder);
    }
    public OptionalInt delete(SqlBuilderDelete builder) throws SQLiteGdxException, SQLException{
        return db.delete(builder);
    }
    public OptionalInt update(SqlBuilderUpdate builder) throws SQLiteGdxException, SQLException{
        return db.update(builder);
    }

    public void startConnection(){
        db.startConnection();
    }
    public void openConnection(){
        db.openConnection();
    }

    public String path(){return db.path();}
    public int version(){return db.version();}
    public String name(String name){ return db.name();}

    public void setHandler(Handler handler){db = handler;}
    public Handler handler(){return db;}


}
