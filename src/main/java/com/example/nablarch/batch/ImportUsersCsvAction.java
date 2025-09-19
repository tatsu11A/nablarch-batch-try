package com.example.nablarch.batch;

import com.example.nablarch.batch.model.UserRow;
import com.example.nablarch.batch.reader.UsersCsvReader;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.BatchAction;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ImportUsersCsvAction extends BatchAction<UserRow> {

    private static final Logger LOG = LoggerManager.get(ImportUsersCsvAction.class);

   @Override
public DataReader<UserRow> createReader(ExecutionContext ctx) {
    try {
        // 入力CSVパス（未指定なら従来の既定）
        String input = System.getProperty("input", "work/input/users.csv");
        // 文字コード（未指定ならUTF-8。SJISなら MS932 を指定）
        String csName = System.getProperty("charset", "UTF-8");

        java.nio.file.Path csv = java.nio.file.Paths.get(input);
        java.nio.charset.Charset cs = java.nio.charset.Charset.forName(csName);

        return new com.example.nablarch.batch.reader.UsersCsvReader(csv, cs);
    } catch (Exception e) {
        throw new RuntimeException("failed to open CSV (check -Dinput/-Dcharset): " + e.getMessage(), e);
    }
}

    @Override
    public Result handle(UserRow row, ExecutionContext ctx) {
        AppDbConnection conn = DbConnectionContext.getConnection();
        
        // H2 の簡易 UPSERT
        SqlPStatement ps = conn.prepareStatement(
            "MERGE INTO USERS KEY(ID) VALUES (?, ?, ?)"
        );
        ps.setInt(1, row.id);
        ps.setString(2, row.name);
        ps.setString(3, row.email);
        ps.executeUpdate();

        LOG.logInfo("upsert id=" + row.id);
        return new Result.Success();
    }
}
