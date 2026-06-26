package com.forecourt.domain.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * {@code java -jar} 실행 시 수집을 1회 구동하고 종료한다.
 *
 * <p>{@code test} 프로파일에선 비활성 — 테스트가 실거래 API를 호출하지 않도록.
 */
@Component
@Profile("!test")
public class CollectorRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CollectorRunner.class);

    private final CollectorService collectorService;

    public CollectorRunner(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    @Override
    public void run(String... args) {
        log.info("Forecourt 수집기 시작");
        collectorService.collect(args);
        log.info("Forecourt 수집기 종료");
    }
}
