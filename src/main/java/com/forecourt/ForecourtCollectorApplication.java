package com.forecourt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Forecourt 수집기 진입점.
 *
 * <p>상주 웹 서버가 아니라 1회 실행 후 종료하는 배치 JAR이다.
 * 실제 수집 구동은 {@link com.forecourt.domain.collector.CollectorRunner} 가 맡는다
 * (GitHub Actions cron 증분 / 로컬 백필 모두 {@code java -jar} 로 실행).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ForecourtCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForecourtCollectorApplication.class, args);
    }
}
