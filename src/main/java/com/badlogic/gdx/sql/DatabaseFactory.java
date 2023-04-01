/**<p>*********************************************************************************************************************
 * <h1>DatabaseFactory</h1>
 * @since 20150926
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20150926  Original author       Rafay Aleem
 * 20230330  Added ConstantClassPath
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 * E505, E506, E507
 **********************************************************************************************************************</p>*/
package com.badlogic.gdx.sql;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** A factory class that creates new database objects and returns references to them. See
 * {@link DatabaseFactory#getNewDatabase(String, int, String, String)} for more details.
 * @author M Rafay Aleem */
public class DatabaseFactory {
	public static final String ERROR_TAG = "DATABASE";
    private static final String androidClassname = ConstantClassPath.sqlitePackageAndroid+".AndroidDatabaseManager";
    private static final String desktopClassname = ConstantClassPath.sqlitePackageDesktop+".DesktopDatabaseManager";
	private static final String robovmClassname = ConstantClassPath.sqlitePackageRoboVm+".RobovmDatabaseManager";

    /* ERROR CODES */
    private static final String E505 = "Error getting database: %s: %s";
    private static final String E506 = "SQLite is currently not supported in Applets by this libgdx extension.";
    private static final String E507 = "SQLite is currently not supported in WebGL by this libgdx extension.";


    String template = "Firstname: %s, Lastname: %s, Id: %s, Company: %s, Role: %s, Department: %s, Address: %s ...";

	private static DatabaseManager databaseManager = null;

	/** This is a factory method that will return a reference to an existing or a not-yet-created database. You will need to
	 * manually call methods on the {@link Database} object to setup, open/create or close the database. See {@link Database} for
	 * more details. <b> Note: </b> dbOnUpgradeQuery will only work on an Android device. It will be executed when you increment
	 * your database version number. First, dbOnUpgradeQuery will be executed (Where you will generally perform activities such as
	 * dropping the tables, etc.). Then dbOnCreateQuery will be executed. However, dbOnUpgradeQuery won't be executed on
	 * downgrading the database version.
	 * @param dbName The name of the database.
	 * @param dbVersion number of the database (starting at 1); if the database is older, dbOnUpgradeQuery will be used to upgrade
	 *           the database (on Android only)
	 * @param dbOnCreateQuery The query that should be executed on the creation of the database. This query would usually create
	 *           the necessary tables in the database.
	 * @param dbOnUpgradeQuery The query that should be executed on upgrading the database from an old version to a new one.
	 * @return Returns a {@link Database} object pointing to an existing or not-yet-created database. */
	public static Database getNewDatabase (String dbName, int dbVersion, String dbOnCreateQuery, String dbOnUpgradeQuery) {
		if (databaseManager == null) {
			switch (Gdx.app.getType()) {
			case Android:
				try {
                    databaseManager = (DatabaseManager)Class.forName(androidClassname).getDeclaredConstructor().newInstance();
				} catch (Throwable ex) {
                    String errorMsg = String.format(E505, androidClassname, ex);
					throw new GdxRuntimeException(errorMsg);
				}
				break;
			case Desktop:
				try {
                    databaseManager = (DatabaseManager)Class.forName(desktopClassname).getDeclaredConstructor().newInstance();
				} catch (Throwable ex) {
                    String errorMsg = String.format(E505, desktopClassname, ex);
                    throw new GdxRuntimeException(errorMsg);
				}
				break;
			case Applet:
				throw new GdxRuntimeException(E506);
			case WebGL:
				throw new GdxRuntimeException(E507);
			case iOS:
				try {
                    databaseManager = (DatabaseManager)Class.forName(robovmClassname).getDeclaredConstructor().newInstance();
				} catch (Throwable ex) {
                    String errorMsg = String.format(E505, robovmClassname, ex);
                    throw new GdxRuntimeException(errorMsg);
				}
				break;
			}
		}
		return databaseManager.getNewDatabase(dbName, dbVersion, dbOnCreateQuery, dbOnUpgradeQuery);
	}

	private DatabaseFactory() {}

}
