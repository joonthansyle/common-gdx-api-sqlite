/**<p>*********************************************************************************************************************
 * <h1>SqlBuilderUpdateFactory</h1>
 * @since 20201014
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20201014  Original author       Evan White
 *           Initial version
 *           reference to {@link DatabaseManager.java}
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 * EQ08
 **********************************************************************************************************************</p>*/
package com.badlogic.gdx.sql.builder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.ConstantClassPath;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.lang.reflect.InvocationTargetException;

public class SqlBuilderUpdateFactory {
    private static final String desktopClassname = ConstantClassPath.sqlitePackageDesktop+".builder.BuildSqlUpdate";
    private static final String androidClassname = ConstantClassPath.sqlitePackageAndroid+".builder.BuildSqlUpdate";
    private SqlBuilderUpdate sqlBuilderUpdate = null;

    /* ERROR CODES */
    private static final String EQ08 = "Error getting BuildSql class: %s: %s";

    public SqlBuilderUpdate builderUpdate() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        switch (Gdx.app.getType()) {
            case Android:
                try {
                    sqlBuilderUpdate = (SqlBuilderUpdate) Class.forName(androidClassname).getDeclaredConstructor().newInstance();
                } catch (Throwable ex) {
                    String errorMsg = String.format(EQ08, androidClassname, ex);
                    throw new GdxRuntimeException(errorMsg);
                }
                break;
            case Desktop:
                try {
                    sqlBuilderUpdate = (SqlBuilderUpdate) Class.forName(desktopClassname).getDeclaredConstructor().newInstance();
                } catch (Throwable ex) {
                    String errorMsg = String.format(EQ08, desktopClassname, ex);
                    throw new GdxRuntimeException(errorMsg);
                }
                break;
            default:
                break;
        }
        return sqlBuilderUpdate;
    }
}
