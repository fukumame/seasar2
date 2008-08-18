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
package org.seasar.extension.jdbc.gen.internal.factory;

import java.io.File;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.EntityMetaFactory;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.gen.command.Command;
import org.seasar.extension.jdbc.gen.data.Dumper;
import org.seasar.extension.jdbc.gen.data.Loader;
import org.seasar.extension.jdbc.gen.desc.DatabaseDescFactory;
import org.seasar.extension.jdbc.gen.desc.EntityDescFactory;
import org.seasar.extension.jdbc.gen.dialect.GenDialect;
import org.seasar.extension.jdbc.gen.generator.GenerationContext;
import org.seasar.extension.jdbc.gen.generator.Generator;
import org.seasar.extension.jdbc.gen.meta.DbTableMetaReader;
import org.seasar.extension.jdbc.gen.meta.EntityMetaReader;
import org.seasar.extension.jdbc.gen.model.ConditionModelFactory;
import org.seasar.extension.jdbc.gen.model.DdlModelFactory;
import org.seasar.extension.jdbc.gen.model.EntityModelFactory;
import org.seasar.extension.jdbc.gen.model.ServiceModelFactory;
import org.seasar.extension.jdbc.gen.model.SqlIdentifierCaseType;
import org.seasar.extension.jdbc.gen.model.SqlKeywordCaseType;
import org.seasar.extension.jdbc.gen.model.TestModelFactory;
import org.seasar.extension.jdbc.gen.sql.SqlFileExecutor;
import org.seasar.extension.jdbc.gen.sql.SqlUnitExecutor;
import org.seasar.extension.jdbc.gen.version.DdlVersionDirectory;
import org.seasar.extension.jdbc.gen.version.DdlVersionIncrementer;
import org.seasar.extension.jdbc.gen.version.Migrater;
import org.seasar.extension.jdbc.gen.version.SchemaInfoTable;
import org.seasar.framework.convention.PersistenceConvention;

/**
 * S2JDBC-Genのインタフェースの実装を作成するファクトリです。
 * 
 * @author taedium
 */
public interface Factory {

    /**
     * {@link EntityMetaReader}の実装を返します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param classpathDir
     *            ルートディレクトリ
     * @param packageName
     *            パッケージ名、パッケージ名を指定しない場合は{@code null}
     * @param entityMetaFactory
     *            エンティティメタデータのファクトリ
     * @param entityNamePattern
     *            対象とするエンティティ名の正規表現
     * @param ignoreEntityNamePattern
     *            対象としないエンティティ名の正規表現
     * @return {@link EntityMetaReader}の実装
     */
    public abstract EntityMetaReader createEntityMetaReader(Command command,
            File classpathDir, String packageName,
            EntityMetaFactory entityMetaFactory, String entityNamePattern,
            String ignoreEntityNamePattern);

    /**
     * {@link DatabaseDescFactory}の実装を返します。
     * 
     * @param command
     *            コマンド
     * @param entityMetaFactory
     *            エンティティメタデータのファクトリ
     * @param entityMetaReader
     *            エンティティメタデータのリーダ
     * @param dialect
     *            方言
     * @return {@link DatabaseDescFactory}の実装
     */
    public abstract DatabaseDescFactory createDatabaseDescFactory(
            Command command, EntityMetaFactory entityMetaFactory,
            EntityMetaReader entityMetaReader, GenDialect dialect);

    /**
     * {@link Dumper}の実装を返します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param dialect
     *            方言
     * @param dumpFileEncoding
     *            ダンプファイルのエンコーディング
     * @return {@link Dumper}の実装
     */
    public abstract Dumper createDumper(Command command, GenDialect dialect,
            String dumpFileEncoding);

    /**
     * {@link SqlUnitExecutor}の実装を返します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param dataSource
     *            データソース
     * @param haltOnError
     *            エラー発生時に処理を即座に中断する場合{@code true}、中断しない場合{@code false}
     * 
     * @return {@link SqlUnitExecutor}の実装
     */
    public abstract SqlUnitExecutor createSqlUnitExecutor(Command command,
            DataSource dataSource, boolean haltOnError);

    /**
     * {@link DbTableMetaReader}の実装を返します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param dataSource
     *            データソース
     * @param dialect
     *            方言
     * @param schemaName
     *            スキーマ名、デフォルトのスキーマ名を表す場合は{@code null}
     * @param tableNamePattern
     *            対象とするテーブル名の正規表現
     * @param ignoreTableNamePattern
     *            対象としないテーブル名の正規表現
     * @return
     */
    public abstract DbTableMetaReader createDbTableMetaReader(Command command,
            DataSource dataSource, GenDialect dialect, String schemaName,
            String tableNamePattern, String ignoreTableNamePattern);

    /**
     * {@link SqlFileExecutor}の実装を返します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param dialect
     *            方言
     * @param sqlFileEncoding
     *            SQLファイルのエンコーディング
     * @param statementDelimiter
     *            SQLステートメントの区切り文字
     * @param blockDelimiter
     *            SQLブロックの区切り文字
     * @return {@link SqlFileExecutor}の実装
     */
    public abstract SqlFileExecutor createSqlFileExecutor(Command command,
            GenDialect dialect, String sqlFileEncoding,
            char statementDelimiter, String blockDelimiter);

    /**
     * {@link ConditionModelFactory}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param packageName
     *            パッケージ名、パッケージ名を指定しない場合は{@code null}
     * @param conditionClassNameSuffix
     *            条件クラス名のサフィックス
     * @return {@link ConditionModelFactory}の実装
     */
    public abstract ConditionModelFactory createConditionModelFactory(
            Command command, String packageName, String conditionClassNameSuffix);

    /**
     * {@link Generator}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param templateFileEncoding
     *            テンプレートファイルのエンコーディング
     * @param templateFilePrimaryDir
     *            テンプレートファイルを格納したディレクトリ
     * @return {@link Generator}の実装
     */
    public abstract Generator createGenerator(Command command,
            String templateFileEncoding, File templateFilePrimaryDir);

    /**
     * {@link DdlVersionDirectory}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param baseDir
     *            バージョン管理のベースディレクトリ
     * @param versionFile
     *            バージョンファイル
     * @param versionNoPattern
     *            バージョン番号のパターン
     * @return {@link DdlVersionDirectory}の実装
     */
    public abstract DdlVersionDirectory createDdlVersionDirectory(
            Command command, File baseDir, File versionFile,
            String versionNoPattern);

    /**
     * {@link DdlVersionIncrementer}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param ddlVersionDirectory
     *            DDLのバージョンを管理するディレクトリ
     * @param createFileNameList
     *            createファイル名のリスト
     * @param dropFileNameList
     *            dropファイル名のリスト
     * @return {@link DdlVersionIncrementer}の実装
     */
    public abstract DdlVersionIncrementer createDdlVersionIncrementer(
            Command command, DdlVersionDirectory ddlVersionDirectory,
            List<String> createFileNameList, List<String> dropFileNameList);

    /**
     * {@link DdlModelFactory}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param dialect
     *            方言
     * @param sqlKeywordCaseType
     *            SQLのキーワードの大文字小文字を変換するかどうかを示す列挙型
     * @param sqlIdentifierCaseType
     *            SQLの識別子の大文字小文字を変換するかどうかを示す列挙型
     * @param statementDelimiter
     *            SQLステートメントの区切り文字
     * @param schemaInfoFullTableName
     *            スキーマ情報を格納するテーブル名
     * @param schemaInfoColumnName
     *            スキーマのバージョン番号を格納するカラム名
     * @param tableOption
     *            テーブルオプション、存在しない場合は{@code null}
     * @return {@link DdlModelFactory}の実装
     */
    public abstract DdlModelFactory createDdlModelFactory(Command command,
            GenDialect dialect, SqlKeywordCaseType sqlKeywordCaseType,
            SqlIdentifierCaseType sqlIdentifierCaseType,
            char statementDelimiter, String schemaInfoFullTableName,
            String schemaInfoColumnName, String tableOption);

    /**
     * {@link EntityDescFactory}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param persistenceConvention
     *            永続化層の命名規約
     * @param dialect
     *            方言
     * @param versionColumnName
     *            バージョンカラム名
     * @param schemaName
     *            スキーマ名、指定されていない場合は{@code null}
     * @return {@link EntityDescFactory}の実装
     */
    public abstract EntityDescFactory createEntityDescFactory(Command command,
            PersistenceConvention persistenceConvention, GenDialect dialect,
            String versionColumnName, String schemaName);

    /**
     * {@link EntityModelFactory}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param packageName
     *            パッケージ名、パッケージ名を指定しない場合は{@code null}
     * @return {@link EntityModelFactory}の実装
     */
    public abstract EntityModelFactory createEntityModelFactory(
            Command command, String packageName);

    /**
     * {@link ServiceModelFactory}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param packageName
     *            パッケージ名
     * @param serviceClassNameSuffix
     *            サービスクラス名のサフィックス
     * @return {@link ServiceModelFactory}の実装
     */
    public abstract ServiceModelFactory createServiceModelFactory(
            Command command, String packageName, String serviceClassNameSuffix);

    /**
     * {@link TestModelFactory}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param configPath
     *            設定ファイルのパス
     * @param jdbcManagerName
     *            {@link JdbcManager}のコンポーネント名
     * @param testClassNameSuffix
     *            テストクラス名のサフィックス
     * @return {@link TestModelFactory}の実装
     */
    public abstract TestModelFactory createTestModelFactory(Command command,
            String configPath, String jdbcManagerName,
            String testClassNameSuffix);

    /**
     * {@link SchemaInfoTable}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param dataSource
     *            データソース
     * @param dialect
     *            方言
     * @param fullTableName
     *            カタログ名やスキーマ名を含む完全なテーブル名
     * @param columnName
     *            カラム名
     * @return {@link SchemaInfoTable}の実装
     */
    public abstract SchemaInfoTable createSchemaInfoTable(Command command,
            DataSource dataSource, GenDialect dialect, String fullTableName,
            String columnName);

    /**
     * {@link Migrater}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param sqlUnitExecutor
     *            SQLのひとまとまりの実行者
     * @param schemaInfoTable
     *            スキーマのバージョン
     * @param ddlVersionDirectory
     *            DDLをバージョン管理するディレクトリ
     * @param version
     *            バージョン
     * @param env
     *            環境名
     * @return {@link Migrater}の実装
     */
    public abstract Migrater createMigrater(Command command,
            SqlUnitExecutor sqlUnitExecutor, SchemaInfoTable schemaInfoTable,
            DdlVersionDirectory ddlVersionDirectory, String version, String env);

    /**
     * {@link Loader}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param dialect
     *            方言
     * @param dumpFileEncoding
     *            ダンプファイルのエンコーディング
     * @param batchSize
     *            バッチサイズ
     * @return {@link Loader}の実装
     */
    public abstract Loader createLoader(Command command, GenDialect dialect,
            String dumpFileEncoding, int batchSize);

    /**
     * {@link GenerationContext}の実装を作成します。
     * 
     * @param command
     *            呼び出し元のコマンド
     * @param model
     *            データモデル
     * @param dir
     *            生成するファイルの出力先ディレクトリ
     * @param file
     *            生成するファイル
     * @param templateName
     *            テンプレート名
     * @param encoding
     *            生成するファイルのエンコーディング
     * @param overwrite
     *            上書きする場合{@code true}、しない場合{@code false}
     * 
     * @return {@link GenerationContext}の実装
     */
    public abstract GenerationContext createGenerationContext(Command command,
            Object model, File dir, File file, String templateName,
            String encoding, boolean overwrite);

}