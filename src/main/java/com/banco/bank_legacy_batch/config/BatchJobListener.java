package com.banco.bank_legacy_batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class BatchJobListener implements JobExecutionListener {

    private static final Logger log =
            LoggerFactory.getLogger(BatchJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {

        log.info("========== JOB INICIADO ==========");
        log.info(
                "Job: {}",
                jobExecution
                        .getJobInstance()
                        .getJobName()
        );

        log.info(
                "Execution ID: {}",
                jobExecution.getId()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        log.info("========== JOB FINALIZADO ==========");

        log.info(
                "Job: {}",
                jobExecution
                        .getJobInstance()
                        .getJobName()
        );

        log.info(
                "Estado: {}",
                jobExecution.getStatus()
        );

        log.info(
                "Exit Status: {}",
                jobExecution
                        .getExitStatus()
                        .getExitCode()
        );
    }
}