package com.example.nablarch.batch.test;

import nablarch.test.event.TestEventDispatcher;
import nablarch.test.junit5.extension.batch.BatchRequestTestExtension;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * {@link XxxxBatchRequestTestSupport}をテストで使用できるようにするためのExtension。
 */
// TODO XxxxをPJ名に変更してください(例:MyProjectBatchRequestExtension)。
public class XxxxBatchRequestExtension extends BatchRequestTestExtension {
    @Override
    protected TestEventDispatcher createSupport(Object testInstance, ExtensionContext context) {
        return new XxxxBatchRequestTestSupport(testInstance.getClass());
    }
}
