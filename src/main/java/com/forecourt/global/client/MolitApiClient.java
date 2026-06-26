package com.forecourt.global.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.forecourt.global.config.MolitProperties;
import com.forecourt.global.exception.CollectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 국토부 실거래가 상세 API 클라이언트.
 *
 * <p>JDK 내장 {@link HttpClient}로 호출한다(spring-web 불필요). User-Agent 헤더를 붙이고
 * (없으면 게이트웨이가 Request Blocked 400), XML 응답을 파싱하며, {@code totalCount}로 페이징한다.
 */
@Component
public class MolitApiClient {

    private static final Logger log = LoggerFactory.getLogger(MolitApiClient.class);

    private static final String ENDPOINT =
            "https://apis.data.go.kr/1613000/RTMSDataSvcAptTradeDev/getRTMSDataSvcAptTradeDev";
    private static final int PAGE_SIZE = 1000;      // 기본 10건 함정 회피
    private static final String SUCCESS_CODE = "000";

    private final MolitProperties props;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final XmlMapper xml = (XmlMapper) new XmlMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public MolitApiClient(MolitProperties props) {
        this.props = props;
    }

    /** 시군구(LAWD_CD) × 월(DEAL_YMD) 의 모든 실거래를 페이징하여 수집한다. */
    public List<MolitApiResponse.Item> fetchAll(String lawdCd, String dealYmd) {
        MolitApiResponse first = fetchPage(lawdCd, dealYmd, 1);
        int total = first.body.totalCount;
        List<MolitApiResponse.Item> all = new ArrayList<>(first.body.safeItems());

        int pages = (int) Math.ceil(total / (double) PAGE_SIZE);
        for (int page = 2; page <= pages; page++) {
            all.addAll(fetchPage(lawdCd, dealYmd, page).body.safeItems());
        }
        log.info("API 수집 완료 - LAWD_CD={} DEAL_YMD={} totalCount={} 수신={}",
                lawdCd, dealYmd, total, all.size());
        return all;
    }

    private MolitApiResponse fetchPage(String lawdCd, String dealYmd, int pageNo) {
        HttpRequest req = HttpRequest.newBuilder(buildUri(lawdCd, dealYmd, pageNo))
                .header("User-Agent", "Mozilla/5.0")
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        try {
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                throw new CollectorException(
                        "국토부 API HTTP " + res.statusCode() + " - " + snippet(res.body()));
            }
            MolitApiResponse parsed = xml.readValue(res.body(), MolitApiResponse.class);
            String code = parsed.header != null ? parsed.header.resultCode : null;
            if (!SUCCESS_CODE.equals(code)) {
                String msg = parsed.header != null ? parsed.header.resultMsg : "?";
                throw new CollectorException("국토부 API 오류 resultCode=" + code + " resultMsg=" + msg);
            }
            return parsed;
        } catch (CollectorException e) {
            throw e;
        } catch (Exception e) {
            throw new CollectorException("국토부 API 호출/파싱 실패 (page=" + pageNo + ")", e);
        }
    }

    private URI buildUri(String lawdCd, String dealYmd, int pageNo) {
        // serviceKey 는 Encoding 인증키 → 그대로 사용(재인코딩 금지). 나머지 파라미터는 영숫자라 안전.
        String query = "serviceKey=" + props.serviceKey()
                + "&LAWD_CD=" + lawdCd
                + "&DEAL_YMD=" + dealYmd
                + "&pageNo=" + pageNo
                + "&numOfRows=" + PAGE_SIZE;
        return URI.create(ENDPOINT + "?" + query);
    }

    private static String snippet(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 200 ? body.substring(0, 200) : body;
    }
}
