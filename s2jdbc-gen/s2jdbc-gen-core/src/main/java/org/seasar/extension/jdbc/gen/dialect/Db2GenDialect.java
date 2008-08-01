/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.extension.jdbc.gen.dialect;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.GenerationType;
import javax.persistence.TemporalType;

/**
 * DB2の方言を扱うクラスです。
 * 
 * @author taedium
 */
public class Db2GenDialect extends StandardGenDialect {

    /** テーブルが見つからないことを示すSQLステート */
    protected static String TABLE_NOT_FOUND_SQL_STATE = "42704";

    /**
     * インスタンスを構築します。
     */
    public Db2GenDialect() {
        sqlTypeMap.put(Types.BINARY, Db2SqlType.BINARY);
        sqlTypeMap.put(Types.BLOB, Db2SqlType.BLOB);
        sqlTypeMap.put(Types.BOOLEAN, Db2SqlType.BOOLEAN);
        sqlTypeMap.put(Types.CLOB, Db2SqlType.CLOB);
        sqlTypeMap.put(Types.DECIMAL, Db2SqlType.DECIMAL);
        sqlTypeMap.put(Types.FLOAT, Db2SqlType.FLOAT);

        columnTypeMap.put("blob", Db2ColumnType.BLOB);
        columnTypeMap.put("char () for bit data", Db2ColumnType.CHAR_BIT);
        columnTypeMap.put("clob", Db2ColumnType.CLOB);
        columnTypeMap.put("decimal", Db2ColumnType.DECIMAL);
        columnTypeMap.put("long varchar for bit data",
                Db2ColumnType.LONGVARCHAR_BIT);
        columnTypeMap.put("long varchar", Db2ColumnType.LONGVARCHAR);
        columnTypeMap.put("varchar () for bit data", Db2ColumnType.VARCHAR_BIT);
    }

    @Override
    public String getDefaultSchemaName(String userName) {
        return userName != null ? userName.toUpperCase() : null;
    }

    @Override
    public GenerationType getDefaultGenerationType() {
        return GenerationType.IDENTITY;
    }

    @Override
    public boolean supportsSequence() {
        return true;
    }

    @Override
    public String getSequenceDefinitionFragment(String dataType, int initValue,
            int allocationSize) {
        return "as " + dataType + " start with " + allocationSize
                + " increment by " + initValue;
    }

    @Override
    public String getIdentityColumnDefinition() {
        return "generated by default as identity";
    }

    @Override
    public String getSqlBlockDelimiter() {
        return "@";
    }

    @Override
    public boolean isTableNotFound(Throwable t) {
        for (SQLException e : getAllSQLExceptions(t)) {
            if (TABLE_NOT_FOUND_SQL_STATE.equals(e.getSQLState())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 原因となった、もしくは関連付けられたすべての{@link SQLException}を取得します。
     * 
     * @param t
     *            例外
     * @return 原因となった、もしくは関連付けられた{@link SQLException}のリスト
     */
    protected List<SQLException> getAllSQLExceptions(Throwable t) {
        List<SQLException> sqlExceptionList = new ArrayList<SQLException>();
        while (t != null) {
            if (t instanceof SQLException) {
                SQLException cause = SQLException.class.cast(t);
                sqlExceptionList.add(cause);
                if (cause.getNextException() != null) {
                    cause = cause.getNextException();
                    sqlExceptionList.add(cause);
                    t = cause;
                    continue;
                }
            }
            t = t.getCause();
        }
        return sqlExceptionList;
    }

    @Override
    public SqlBlockContext createSqlBlockContext() {
        return new Db2SqlBlockContext();
    }

    /**
     * DB2用の link StandardSqlType}の実装です。
     * 
     * @author taedium
     */
    public static class Db2SqlType extends StandardSqlType {

        private static Db2SqlType BINARY = new Db2SqlType(
                "varchar($l) for bit data");

        private static Db2SqlType BLOB = new Db2SqlType("blob($l)");

        private static Db2SqlType BOOLEAN = new Db2SqlType("smallint");

        private static Db2SqlType CLOB = new Db2SqlType("clob($l)");

        private static Db2SqlType DECIMAL = new Db2SqlType("decimal($p,$s)");

        private static Db2SqlType FLOAT = new Db2SqlType("real");

        /**
         * インスタンスを構築します。
         * 
         * @param columnDefinition
         *            カラム定義
         */
        protected Db2SqlType(String columnDefinition) {
            super(columnDefinition);
        }
    }

    /**
     * DB2用の{@link StandardColumnType}の実装です。
     * 
     * @author taedium
     */
    public static class Db2ColumnType extends StandardColumnType {

        private static Db2ColumnType BLOB = new Db2ColumnType("blob($l)",
                byte[].class);

        private static Db2ColumnType CHAR_BIT = new Db2ColumnType(
                "char($l) for bit data", byte[].class);

        private static Db2ColumnType CLOB = new Db2ColumnType("clob($l)",
                String.class);

        private static Db2ColumnType DECIMAL = new Db2ColumnType(
                "decimal($p,$s)", BigDecimal.class);

        private static Db2ColumnType LONGVARCHAR_BIT = new Db2ColumnType(
                "long varchar for bit data", byte[].class);

        private static Db2ColumnType LONGVARCHAR = new Db2ColumnType(
                "long varchar", String.class);

        private static Db2ColumnType VARCHAR_BIT = new Db2ColumnType(
                "varchar($l) for bit data", byte[].class);

        /**
         * インスタンスを構築します。
         * 
         * @param columnDefinition
         *            カラム定義
         * @param attributeClass
         *            属性のクラス
         */
        public Db2ColumnType(String columnDefinition, Class<?> attributeClass) {
            super(columnDefinition, attributeClass);
        }

        /**
         * インスタンスを構築します。
         * 
         * @param columnDefinition
         *            カラム定義
         * @param attributeClass
         *            属性のクラス
         * @param lob
         *            LOBの場合{@code true}
         */
        public Db2ColumnType(String columnDefinition, Class<?> attributeClass,
                boolean lob) {
            super(columnDefinition, attributeClass, lob);
        }

        /**
         * インスタンスを構築します。
         * 
         * @param columnDefinition
         *            カラム定義
         * @param attributeClass
         *            属性のクラス
         * @param temporalType
         *            時制型
         */
        public Db2ColumnType(String columnDefinition, Class<?> attributeClass,
                TemporalType temporalType) {
            super(columnDefinition, attributeClass, temporalType);
        }
    }

    /**
     * DB2用の{@link SqlBlockContext}の実装クラスです。
     * 
     * @author taedium
     */
    public static class Db2SqlBlockContext extends StandardSqlBlockContext {

        /**
         * インスタンスを構築します。
         */
        protected Db2SqlBlockContext() {
            sqlBlockStartKeywordsList.add(Arrays.asList("create", "procedure"));
            sqlBlockStartKeywordsList.add(Arrays.asList("create", "function"));
            sqlBlockStartKeywordsList.add(Arrays.asList("create", "triger"));
            sqlBlockStartKeywordsList.add(Arrays.asList("alter", "procedure"));
            sqlBlockStartKeywordsList.add(Arrays.asList("alter", "function"));
            sqlBlockStartKeywordsList.add(Arrays.asList("alter", "triger"));
        }
    }
}
