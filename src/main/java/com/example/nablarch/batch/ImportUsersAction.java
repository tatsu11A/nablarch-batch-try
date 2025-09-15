package com.example.nablarch.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.NoInputDataBatchAction;

/**
 * work/input/users.csv を読み、USERS テーブルへ登録する最小ジョブ。
 * 学習用に、テーブルが無ければ最初に CREATE してから INSERT します。
 */
public class ImportUsersAction extends NoInputDataBatchAction {

    private static final Logger LOG = LoggerManager.get(ImportUsersAction.class);

    @Override
    public Result handle(ExecutionContext ctx) {
        LOG.logInfo("[ImportUsersAction] start");

        // 1) DB接続（data-source.xml に従う）
        AppDbConnection conn = DbConnectionContext.getConnection();

        // 2) 学習用: テーブルが無ければ作成（H2想定）
        conn.prepareStatement(
            "CREATE TABLE IF NOT EXISTS USERS (" +
            "  ID INT PRIMARY KEY," +
            "  NAME VARCHAR(100) NOT NULL," +
            "  EMAIL VARCHAR(255) NOT NULL" +
            ")"
        ).execute();

        // 3) CSV 読み込み → INSERT
        Path csv = Paths.get("work", "input", "users.csv");
        int inserted = 0;

        try (BufferedReader br = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue; // 空行やコメント行はスキップ
                }
                String[] cols = line.split(",", -1);
                if (cols.length < 3) {
                    LOG.logWarn("skip invalid line: " + line);
                    continue;
                }

                int id = Integer.parseInt(cols[0].trim());
                String name = cols[1].trim();
                String email = cols[2].trim();

                SqlPStatement ps = conn.prepareStatement(
                    "INSERT INTO USERS (ID, NAME, EMAIL) VALUES (?, ?, ?)"
                );
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.executeUpdate();

                inserted++;
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV read failed: " + csv.toAbsolutePath(), e);
        }

        LOG.logInfo("[ImportUsersAction] inserted rows = " + inserted);
        LOG.logInfo("[ImportUsersAction] finish");
        return new Result.Success();
    }
}
