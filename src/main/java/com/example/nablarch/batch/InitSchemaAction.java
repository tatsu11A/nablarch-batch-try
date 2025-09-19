package com.example.nablarch.batch;

import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.NoInputDataBatchAction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * resources/db/schema.sql を読み、順に実行する初期化ジョブ。
 */
public class InitSchemaAction extends NoInputDataBatchAction {

    private static final Logger LOG = LoggerManager.get(InitSchemaAction.class);

    @Override
    public Result handle(ExecutionContext ctx) {
        LOG.logInfo("[InitSchemaAction] start");

        // クラスパスから schema.sql を読む
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("db/schema.sql");
        if (in == null) {
            throw new IllegalStateException("schema not found: classpath:db/schema.sql");
        }

        AppDbConnection conn = DbConnectionContext.getConnection();

        // 超シンプルなパーサ：-- で始まる行はコメント扱い。; 区切りで実行。
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder buf = new StringBuilder();
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                buf.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    String sql = buf.toString();
                    // 末尾の ; を削る
                    sql = sql.substring(0, sql.lastIndexOf(';'));
                    conn.prepareStatement(sql).execute();
                    count++;
                    buf.setLength(0);
                }
            }
            // セミコロンで終わっていない最後の文があれば実行
            if (buf.length() > 0) {
                String sql = buf.toString().trim();
                if (!sql.isEmpty()) {
                    conn.prepareStatement(sql).execute();
                    count++;
                }
            }
            LOG.logInfo("[InitSchemaAction] executed statements = " + count);
        } catch (Exception e) {
            throw new RuntimeException("schema initialization failed.", e);
        }

        LOG.logInfo("[InitSchemaAction] finish");
        return new Result.Success();
    }
}
