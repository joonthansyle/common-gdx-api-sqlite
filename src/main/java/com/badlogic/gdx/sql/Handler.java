//@formatter:off
/**<p>*********************************************************************************************************************
 * <h1>Handler</h1>
 * @since 20221218
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20221218  @version 01           @author ORIGINAL AUTHOR
 *           Initial version
 *           Implementing SQLite
 * 20230103  startConnection called separately
 * 20230115  As per Android, only 1 database is allowed
 *           Unlike Preference, this should be created once
 *           getInstance to synchronized
 * 20230122  if instance not null return instance
 * 20230131  Added Method delegation getCursor, insert, update and delete
 * 20230305  ANDROID LOCATION: /data/data/com.galaxy.red.hat.JLSystem/databases/<name>
 * 20230507  TODO: ERROR CODES for Other Exceptions for identification
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 * E520: Handler Singleton already constructed.
 * E521: Cannot create folder: %s for database: %s, default location is used
 * E522: Unable to Open or Create Database
 * E523: Unable to Start Connection
 * E524: Error executing raw query that returns database cursor
 * E525: Error executing exec sql
 **********************************************************************************************************************</p>*/
//@formatter:on
package com.badlogic.gdx.sql;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.builder.SqlBuilderDelete;
import com.badlogic.gdx.sql.builder.SqlBuilderInsert;
import com.badlogic.gdx.sql.builder.SqlBuilderSelect;
import com.badlogic.gdx.sql.builder.SqlBuilderUpdate;

import java.io.File;
import java.sql.SQLException;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class Handler {
    private static final String TAG = Handler.class.getCanonicalName();
    public static final String NAME = TAG;
    private static Handler instance;
    private Database db;
    private String path;
    private String dbName;
    private int dbVersion;

    private String dbOnCreateQuery;
    private String dbOnUpgradeQuery;

    /** ERRORS */
    public final String E520 = "Handler Singleton already constructed.";
    private final String E521 = "Cannot create folder: %s for database: %s, default location is used";
    private final String E522 = "Unable to Open or Create Database";
    private final String E523 = "Unable to Start Connection";
    private final String E524 = "Error executing raw query that returns database cursor";
    private final String E525 = "Error executing exec sql";

    /**
     * @param path             Windows path
     * @param dbName           name of the Database
     * @param dbVersion        Version
     * @param dbOnCreateQuery  use "null" if not required
     * @param dbOnUpgradeQuery use "null" if not required
     */
    public Handler(String path, String dbName, int dbVersion, String dbOnCreateQuery, String dbOnUpgradeQuery) {
        if(instance != null) throw new Error(E520);
        this.path = path;
        this.dbName = dbName;
        this.dbVersion = dbVersion;
        this.dbOnCreateQuery = dbOnCreateQuery;
        this.dbOnUpgradeQuery = dbOnUpgradeQuery;
        instance = this;
    }

    /**
     * Use for Windows
     * @param path
     * @param dbName
     * @param dbVersion
     */
    public Handler(String path, String dbName, int dbVersion) {
        if(instance != null) throw new Error(E520);
        this.path = path;
        this.dbName = dbName;
        this.dbVersion = dbVersion;
        instance = this;
    }

    /**
     * Use for Android, or default Windows Location which is the same sa Project Location
     * @param dbName
     * @param dbVersion
     */
    public Handler(String dbName, int dbVersion) {
        if(instance != null) return;
        this.dbName = dbName;
        this.dbVersion = dbVersion;
        instance = this;
    }

    /**
     * This method to get and instance of Handler that is already created;
     */
    public static synchronized Handler getInstance(){
        if(instance == null ) return null;
        return instance;
    }

    public void startConnection(){
        try {
            String createdDBName = createDBName(path, dbName);
            db = DatabaseFactory.getNewDatabase(createdDBName, dbVersion, dbOnCreateQuery,dbOnUpgradeQuery);
            db.setupDatabase();
            db.openOrCreateDatabase();
        } catch (SQLiteGdxException e) {
            Gdx.app.error(TAG, E523+" : "+e);
        }
    }
    public void openConnection(){
        try {
            db.openOrCreateDatabase();
        } catch (SQLiteGdxException e) {
            Gdx.app.error(TAG, E522+" : "+e);
        }
    }
    private String createDBName(String path, String dbFileName){
        if(path==null || path.equals("")){return dbFileName;}
        switch (Gdx.app.getType()) {
            case Desktop:
                return configureWindowDBFileName(path, dbFileName);
            default:
                return dbFileName;
        }
    }
    private String configureWindowDBFileName(String path, String dbFileName){
        String newPath = path;
        /** Remove trailing slash from the path */
        if (path.endsWith("/") || path.endsWith("\\")) {
            newPath = path.substring(0, path.length() - 1);
        }
        /** Create path if not exists, display error if any */
        File dir = new File(newPath);
        boolean isExist = dir.exists();
        if(!isExist){
            isExist = new File(newPath).mkdirs();
        }
        if(!isExist){
            String error = String.format(E521, path, dbFileName);
            Gdx.app.error(TAG, error);
            return dbFileName;
        }
        /** copy slash or backslash from existing */
        return path.contains("/") ? newPath+"/"+ dbFileName : newPath+"\\"+ dbFileName;
    }

    @Deprecated
    public DatabaseCursor rawQuery(String s){
        DatabaseCursor cursor = null;
        try {
            cursor = db.rawQuery(s);
        } catch (SQLiteGdxException e) {
            Gdx.app.error(TAG, E524+" : "+e);
        }
        return cursor;
    }

    @Deprecated
    public DatabaseCursor rawQuery(DatabaseCursor cursor, String s){
        DatabaseCursor nextCursor = null;
        try {
            nextCursor = db.rawQuery(cursor, s);
        } catch (SQLiteGdxException e) {
            Gdx.app.error(TAG, E524+" : "+e);
        }
        return nextCursor;
    }

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

    @Deprecated
    public void execSQL(String sql){
        try {
            db.execSQL(sql);
        } catch (SQLiteGdxException e) {
            Gdx.app.error(TAG, E525+" : "+e);
        }
    }

    public Database db(){
        return db;
    }

    public void close(){
        try {
            db.closeDatabase();
        } catch (SQLiteGdxException ignored) {}
    }

    public String name(){return dbName;}
    public int version(){return dbVersion;}
    public String path(){return path;}

}
