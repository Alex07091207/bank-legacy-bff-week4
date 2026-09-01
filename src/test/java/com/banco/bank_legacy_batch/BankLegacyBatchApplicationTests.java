package com.banco.bank_legacy_batch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.batch.job.enabled=false")
class BankLegacyBatchApplicationTests {

    @Test
    void contextLoads() {
    }
}