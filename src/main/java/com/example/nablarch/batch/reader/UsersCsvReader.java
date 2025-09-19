package com.example.nablarch.batch.reader;

import com.example.nablarch.batch.model.UserRow;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * users.csv を1行ずつ UserRow に変換して供給する Reader。
 * ・UTF-8 前提、BOM/先頭ヘッダを自動スキップ
 * ・空行/#始まり行をスキップ
 */
public class UsersCsvReader implements DataReader<UserRow> {

    private static final Logger LOG = LoggerManager.get(UsersCsvReader.class);

    private final BufferedReader br;
    private UserRow next;

    public UsersCsvReader(Path path) throws IOException {
        this(path, StandardCharsets.UTF_8);
    }

    public UsersCsvReader(Path path, Charset cs) throws IOException {
        this.br = Files.newBufferedReader(path, cs);
        skipHeaderIfPresent();
    }

    private void skipHeaderIfPresent() throws IOException {
        br.mark(8192);
        String first = br.readLine();
        if (first == null) return;

        // BOM除去
        if (!first.isEmpty() && first.charAt(0) == '\uFEFF') {
            first = first.substring(1);
        }
        // 数字で始まらない＝ヘッダっぽい → スキップ
        if (!first.matches("^\\s*\\d+\\s*,.*")) {
            return;
        }
        // データ行だった → 巻き戻す
        br.reset();
    }

    // ★ DataReader 規約に合わせて ExecutionContext 引数を取る
    @Override
    public boolean hasNext(ExecutionContext ctx) {
        if (next != null) return true;
        next = readOne();
        return next != null;
    }

    @Override
    public UserRow read(ExecutionContext ctx) {
        if (!hasNext(ctx)) return null;
        UserRow r = next;
        next = null;
        return r;
    }

    private UserRow readOne() {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] c = line.split(",", -1);
                if (c.length < 3) {
                    LOG.logWarn("skip invalid line: " + line);
                    continue;
                }
                int id = Integer.parseInt(c[0].trim());
                String name = c[1].trim();
                String email = c[2].trim();
                return new UserRow(id, name, email);
            }
            return null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ★ これも規約通り（例外は握りつぶしてOK）
    @Override
    public void close(ExecutionContext ctx) {
        try {
            br.close();
        } catch (IOException ignore) {
            // ログしたければ LOG.logWarn(...);
        }
    }
}
