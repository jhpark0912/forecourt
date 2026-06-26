package com.forecourt.domain.collector;

import com.forecourt.global.config.MolitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 국토부 실거래가 수집의 진입 서비스. 이후 백필/증분 모드를 분기한다.
 *
 * <p>현재는 뼈대 단계 — 실제 수집(시군구×월 호출, aptSeq 필터링, 페이징)은 다음 단계에서 구현.
 */
@Service
public class CollectorService {

    private static final Logger log = LoggerFactory.getLogger(CollectorService.class);

    private final MolitProperties molitProperties;

    public CollectorService(MolitProperties molitProperties) {
        this.molitProperties = molitProperties;
    }

    public void collect(String... args) {
        log.info("serviceKey 주입 확인: {}", molitProperties.hasServiceKey() ? "OK" : "없음(MOLIT_SERVICE_KEY 미설정)");
        log.warn("수집 로직 미구현 — 뼈대 단계");
    }
}
