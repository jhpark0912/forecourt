package com.forecourt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 스프링 컨텍스트가 정상 로드되는지 검증하는 뼈대 테스트.
 *
 * <p>{@code test} 프로파일이라 CollectorRunner 가 비활성 → 테스트 중 실거래 API를 호출하지 않는다.
 */
@SpringBootTest
@ActiveProfiles("test")
class ForecourtCollectorApplicationTests {

    @Test
    void contextLoads() {
    }
}
