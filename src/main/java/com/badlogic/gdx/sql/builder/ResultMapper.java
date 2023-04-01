/**<p>*********************************************************************************************************************
 * <h1>ResultMapper</h1>
 * @since 20230328
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20230328  original author       evanwht1@gmail.com
 *           removed SelectBuilder link
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 *
 **********************************************************************************************************************</p>*/

package com.badlogic.gdx.sql.builder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author evanwht1@gmail.com
 */
@FunctionalInterface
public interface ResultMapper<T> {
    T map(final ResultSet rs) throws SQLException;
}
