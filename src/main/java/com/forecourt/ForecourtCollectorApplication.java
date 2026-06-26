package com.forecourt;

import com.forecourt.domain.collector.CollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Forecourt 수집기 진입점.
 *
 * <p>상주 웹 서버가 아니라 1회 실행 후 종료하는 배치 JAR이다.
 * GitHub Actions cron(증분) 또는 로컬(백필)에서 {@code java -jar} 로 실행된다.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ForecourtCollectorApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ForecourtCollectorApplication.class);

    private final CollectorService collectorService;

    public ForecourtCollectorApplication(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ForecourtCollectorApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Forecourt 수집기 시작");
        collectorService.collect(args);
        log.info("Forecourt 수집기 종료");
    }
}
