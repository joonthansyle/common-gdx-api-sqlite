/**<p>*********************************************************************************************************************
 * <h1>ClassResultMapper</h1>
 * @since 20201014
 * =====================================================================================================================
 * DATE      VSN/MOD               BY....
 * =====================================================================================================================
 * 20201014  Original author       Evan White
 * 20230329  Modified methods to accept Android lower version:
 *           . getParameterCount to getConstructorParameterCount
 *           . setField: old switch function
 * =====================================================================================================================
 * INFO, ERRORS AND WARNINGS:
 *
 **********************************************************************************************************************</p>*/
package com.badlogic.gdx.sql.builder;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple ResultMapper to turn a DB row into an object of the supplied class. Loops over the returned
 * columns and tried to set values on fields that match any of the column names. Only works with simple
 * data types (integer, double, boolean, and string), anything else and it will attempt to set a generic
 * object on the field.
 *
 * @author evanwht1@gmail.com
 */
public class ClassResultMapper<T> implements ResultMapper<T> {
    private final Class<T> tClass;
    public ClassResultMapper(final Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T map(final ResultSet rs) throws SQLException {
        final Constructor<?> constructor = Arrays.stream(tClass.getConstructors())
//            .filter(c ->  c.getParameterCount() == 0 )
//            .filter(c ->  getCPCount(c) == 0 )
            .filter(c ->  getConstructorParameterCount(c) == 0 )
//            .filter(c -> {
//                try {
//                    return getConstructorParameterCount(c) == 0;
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            })
            .findAny()
            .orElseThrow(() -> {
                throw new RuntimeException("Can't instantiate instance of type: " + tClass.getSimpleName());
            });
        try {
            final T o = (T) constructor.newInstance();
            setAllFields(o, rs);
            return o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Can't instantiate instance of type: " + tClass.getSimpleName());
        }
    }
    private int getConstructorParameterCount(Constructor<?> constructor) {
        List<String> paramNames = null;
        try {
            paramNames = getParameterNames(constructor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(paramNames==null) {
            System.out.println("parameters: NONE ");
            return 0;
        }
        System.out.println("parameters: "+paramNames);
        return paramNames.size();
    }
    private void setAllFields(final T obj, final ResultSet rs) throws SQLException {
        final int columnCount = rs.getMetaData().getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String columnName = rs.getMetaData().getColumnName(i);
            try {
                final Field field = tClass.getField(columnName);
                final int columnType = rs.getMetaData().getColumnType(i);
                setField(field, columnType, i, obj, rs);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Silently move on
            }
        }
    }

    private void setField(final Field field, final int type, final int columnIndex, final T obj, final ResultSet rs) throws SQLException, IllegalAccessException {
        switch (type) {
            case Types.INTEGER:
                field.setInt(obj, rs.getInt(columnIndex));
                break;
            case Types.DOUBLE:
                field.setDouble(obj, rs.getDouble(columnIndex));
                break;
            case Types.BOOLEAN:
                field.setBoolean(obj, rs.getBoolean(columnIndex));
                break;
            case Types.VARCHAR:
                field.set(obj, rs.getString(columnIndex));
                break;
            default:
                field.set(obj, rs.getObject(columnIndex));
                break;
        }
//        switch (type) {
//            case Types.INTEGER -> field.setInt(obj, rs.getInt(columnIndex));
//            case Types.DOUBLE -> field.setDouble(obj, rs.getDouble(columnIndex));
//            case Types.BOOLEAN -> field.setBoolean(obj, rs.getBoolean(columnIndex));
//            case Types.VARCHAR -> field.set(obj, rs.getString(columnIndex));
//            default -> field.set(obj, rs.getObject(columnIndex));
//        }
    }



    /**
     * Returns a list containing one parameter name for each argument accepted
     * by the given constructor. If the class was compiled with debugging
     * symbols, the parameter names will match those provided in the Java source
     * code. Otherwise, a generic "arg" parameter name is generated ("arg0" for
     * the first argument, "arg1" for the second...).
     *
     * This method relies on the constructor's class loader to locate the
     * bytecode resource that defined its class.
     *
     * @param constructor
     * @return
     * @throws IOException
     */
    public static List<String> getParameterNames(Constructor<?> constructor) throws IOException {
        Class<?> declaringClass = constructor.getDeclaringClass();
        ClassLoader declaringClassLoader = declaringClass.getClassLoader();

        Type declaringType = Type.getType(declaringClass);
        String constructorDescriptor = Type.getConstructorDescriptor(constructor);
        String url = declaringType.getInternalName() + ".class";

        InputStream classFileInputStream = declaringClassLoader.getResourceAsStream(url);
        if (classFileInputStream == null) {
            throw new IllegalArgumentException("The constructor's class loader cannot find the bytecode that defined the constructor's class (URL: " + url + ")");
        }

        ClassNode classNode;
        try {
            classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classFileInputStream);
            classReader.accept(classNode, 0);
        } finally {
            classFileInputStream.close();
        }

        @SuppressWarnings("unchecked")
        List<MethodNode> methods = classNode.methods;
        for (MethodNode method : methods) {
            if (method.name.equals("<init>") && method.desc.equals(constructorDescriptor)) {
                Type[] argumentTypes = Type.getArgumentTypes(method.desc);
                List<String> parameterNames = new ArrayList<>(argumentTypes.length);

                @SuppressWarnings("unchecked")
                List<LocalVariableNode> localVariables = method.localVariables;
                for (int i = 0; i < argumentTypes.length; i++) {
                    // The first local variable actually represents the "this" object
                    parameterNames.add(localVariables.get(i + 1).name);
                }

                return parameterNames;
            }
        }

        return null;
    }
}

