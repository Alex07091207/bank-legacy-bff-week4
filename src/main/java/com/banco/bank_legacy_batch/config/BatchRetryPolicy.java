package com.banco.bank_legacy_batch.config;

import java.util.Collections;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

public class BatchRetryPolicy {

    private BatchRetryPolicy() {
    }

    public static RetryPolicy create() {

        return new SimpleRetryPolicy(
                3,
                Collections.singletonMap(
                        Exception.class,
                        true
                )
        );
    }
}