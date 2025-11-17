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
// ➤ 入力データ（つまり引数やリクエスト）なしで動くバッチ用クラス
import nablarch.fw.action.NoInputDataBatchAction;

/**
 * work/input/users.csv を読み、USERS テーブルへ登録する最小ジョブ。
 * 学習用として、テーブルがなければ自動でCREATEしてからINSERTします。
 */
public class ImportUsersAction extends NoInputDataBatchAction {  // NoInputDataBatchActionを継承 → handle()が実行される

    // ログ出力のためのLogger（ログ = 実行中の記録を残すもの）
    private static final Logger LOG = LoggerManager.get(ImportUsersAction.class);

    @Override
    public Result handle(ExecutionContext ctx) {  // バッチ処理のメインメソッド（ここから処理が始まる）
        LOG.logInfo("[ImportUsersAction] start"); // バッチ開始のログ

        // 1) DB接続を取得（data-source.xml の設定を使う）
        AppDbConnection conn = DbConnectionContext.getConnection();

        // 2) 学習用：もしUSERSテーブルが無ければ新規作成（H2データベース想定）
        conn.prepareStatement(
            "CREATE TABLE IF NOT EXISTS USERS (" +
            "  ID INT PRIMARY KEY," +           // ユーザーID（主キー）
            "  NAME VARCHAR(100) NOT NULL," +   // 名前（必須）
            "  EMAIL VARCHAR(255) NOT NULL" +   // メールアドレス（必須）
            ")"
        ).execute(); // SQLを実行

        // 3) 読み込むCSVファイルのパスを指定
        Path csv = Paths.get("work", "input", "users.csv");
        int inserted = 0; // 何件INSERTしたかをカウントする変数

        // try-with-resources → 自動でファイルをcloseしてくれる
        try (BufferedReader br = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
            String line;
            // 4) CSVを1行ずつ読み込む
            while ((line = br.readLine()) != null) {
                // 空行や "#" で始まるコメント行は無視する
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                // カンマ区切りで分割して配列にする
                String[] cols = line.split(",", -1);
                if (cols.length < 3) { // カラムが3つ無い行は不正としてスキップ
                    LOG.logWarn("skip invalid line: " + line);
                    continue;
                }

                // 各列の値を取り出し、トリムして整える
                int id = Integer.parseInt(cols[0].trim());
                String name = cols[1].trim();
                String email = cols[2].trim();

                // 5) DBにINSERTするためのSQL準備
                SqlPStatement ps = conn.prepareStatement(
                    "INSERT INTO USERS (ID, NAME, EMAIL) VALUES (?, ?, ?)"
                );
                ps.setInt(1, id);        // 1つ目の「?」にIDをセット
                ps.setString(2, name);   // 2つ目に名前
                ps.setString(3, email);  // 3つ目にメールアドレス
                ps.executeUpdate();      // SQL実行（実際にDBに登録される）

                inserted++; // 成功した件数をカウント
            }
        } catch (IOException e) {
            // CSV読み込みに失敗した場合はエラーにする
            throw new RuntimeException("CSV read failed: " + csv.toAbsolutePath(), e);
        }

        // 6) INSERTした件数をログに出力
        LOG.logInfo("[ImportUsersAction] inserted rows = " + inserted);
        LOG.logInfo("[ImportUsersAction] finish");

        // バッチの正常終了を通知
        return new Result.Success();
    }
}
