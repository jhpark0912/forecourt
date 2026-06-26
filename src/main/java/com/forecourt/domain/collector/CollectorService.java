package com.forecourt.domain.collector;

import com.forecourt.global.client.MolitApiClient;
import com.forecourt.global.client.MolitApiResponse;
import com.forecourt.global.config.MolitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 국토부 실거래가 수집의 진입 서비스.
 *
 * <p>현재 단계 — 시군구×월 1회 수집·파싱 후 콘솔 출력(검토용). 백필/증분 분기와 DB 적재는 다음 단계.
 * 인자: {@code <LAWD_CD> <DEAL_YMD>} (생략 시 강남구·지난달).
 */
@Service
public class CollectorService {

    private static final Logger log = LoggerFactory.getLogger(CollectorService.class);
    private static final DateTimeFormatter YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");
    private static final String DEFAULT_LAWD_CD = "11680";   // 강남구 (DESIGN 예시)
    private static final int PRINT_LIMIT = 20;

    private final MolitProperties props;
    private final MolitApiClient client;

    public CollectorService(MolitProperties props, MolitApiClient client) {
        this.props = props;
        this.client = client;
    }

    public void collect(String... args) {
        if (!props.hasServiceKey()) {
            log.warn("MOLIT_SERVICE_KEY 미설정 - 수집 건너뜀");
            return;
        }
        String lawdCd = args.length > 0 ? args[0] : DEFAULT_LAWD_CD;
        String dealYmd = args.length > 1 ? args[1] : YearMonth.now().minusMonths(1).format(YYYYMM);
        log.info("수집 시작 - LAWD_CD={} DEAL_YMD={}", lawdCd, dealYmd);

        List<MolitApiResponse.Item> items = client.fetchAll(lawdCd, dealYmd);
        List<AptTrade> trades = items.stream().map(AptTrade::from).toList();

        print(trades);
    }

    private void print(List<AptTrade> trades) {
        log.info("파싱 결과 {}건 (상위 {}건 출력)", trades.size(), Math.min(trades.size(), PRINT_LIMIT));
        log.info(String.format("%-12s %-18s %-8s %8s %11s %4s %-11s %6s",
                "aptSeq", "단지명", "법정동", "전용㎡", "금액(만원)", "층", "거래일", "준공"));
        trades.stream().limit(PRINT_LIMIT).forEach(t -> log.info(String.format(
                "%-12s %-18s %-8s %8.2f %,11d %4d %-11s %6d",
                nz(t.aptSeq()), nz(t.aptNm()), nz(t.umdNm()),
                t.areaM2(), t.amountManwon(), t.floor(),
                t.dealDate() == null ? "-" : t.dealDate().toString(), t.buildYear())));
        if (trades.size() > PRINT_LIMIT) {
            log.info("... 외 {}건", trades.size() - PRINT_LIMIT);
        }
    }

    private static String nz(String s) {
        return s == null ? "-" : s;
    }
}
