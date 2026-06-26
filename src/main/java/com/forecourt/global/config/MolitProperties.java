package com.forecourt.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 국토부 실거래가 API 설정.
 *
 * <p>{@code service-key}는 환경변수 {@code MOLIT_SERVICE_KEY}에서 주입된다
 * (로컬은 .env, CI는 GitHub Actions 시크릿). data.go.kr Encoding 인증키를 그대로 사용한다.
 */
@ConfigurationProperties(prefix = "molit")
public record MolitProperties(String serviceKey) {

    public boolean hasServiceKey() {
        return serviceKey != null && !serviceKey.isBlank();
    }
}
