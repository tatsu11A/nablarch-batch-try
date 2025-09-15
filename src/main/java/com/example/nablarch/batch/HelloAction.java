package com.example.nablarch.batch;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.NoInputDataBatchAction;

/**
 * 入力なし（NoInput）の最小バッチ。
 * 雛形の SampleBatch と同じ継承構造（NoInputDataBatchAction）に合わせています。
 */
public class HelloAction extends NoInputDataBatchAction {

    private static final Logger LOGGER = LoggerManager.get(HelloAction.class);

    @Override
    public Result handle(ExecutionContext ctx) {
        LOGGER.logInfo("[HelloAction] バッチ開始");
        // ここに最小の処理を書けます（DB接続確認・ログ出力など）
        LOGGER.logInfo("[HelloAction] バッチ終了");
        return new Result.Success();
    }
}
