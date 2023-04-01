/**<p>*********************************************************************************************************************
 * <h1>SqlBuilderSelectFactory</h1>
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

public class SqlBuilderSelectFactory<T> {
    private static final String desktopClassname = ConstantClassPath.sqlitePackageDesktop+".builder.BuildSqlSelect";
    private static final String androidClassname = ConstantClassPath.sqlitePackageAndroid+".builder.BuildSqlSelect";
    private SqlBuilderSelect<T> sqlBuilderSelect = null;

    /* ERROR CODES */
    private static final String EQ08 = "Error getting BuildSql class: %s: %s";

    public SqlBuilderSelect<T> builderSelect(ResultMapper<T> resultMapper) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        switch (Gdx.app.getType()) {
            case Android:
                try {
                    sqlBuilderSelect = (SqlBuilderSelect<T>) Class.forName(androidClassname).getDeclaredConstructor(ResultMapper.class).newInstance(resultMapper);
                } catch (Throwable ex) {
                    String errorMsg = String.format(EQ08, androidClassname, ex);
                    throw new GdxRuntimeException(errorMsg);
                }
                break;
            case Desktop:
                try {
                    sqlBuilderSelect = (SqlBuilderSelect<T>) Class.forName(desktopClassname).getDeclaredConstructor(ResultMapper.class).newInstance(resultMapper);
                } catch (Throwable ex) {
                    String errorMsg = String.format(EQ08, desktopClassname, ex);
                    throw new GdxRuntimeException(errorMsg);
                }
                break;
            default:
                break;
        }
        return sqlBuilderSelect;
    }
}
