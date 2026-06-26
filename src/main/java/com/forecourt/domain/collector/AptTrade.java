package com.forecourt.domain.collector;

import com.forecourt.global.client.MolitApiResponse;

import java.time.LocalDate;

/**
 * 파싱·정규화된 단일 실거래.
 *
 * <p>API 원문 문자열을 숫자/일자로 변환한다(예: {@code dealAmount}="550,000" → {@code 550000} 만원).
 * 평단가 정규화 같은 지표 계산은 market 도메인의 다음 단계 몫이다.
 */
public record AptTrade(
        String aptSeq,
        String aptNm,
        String umdNm,
        String jibun,
        double areaM2,        // 전용면적(㎡)
        int amountManwon,     // 거래금액(만원)
        int floor,
        LocalDate dealDate,
        int buildYear
) {

    public static AptTrade from(MolitApiResponse.Item it) {
        return new AptTrade(
                trim(it.aptSeq),
                trim(it.aptNm),
                trim(it.umdNm),
                trim(it.jibun),
                parseDouble(it.excluUseAr),
                parseAmount(it.dealAmount),
                parseInt(it.floor),
                parseDate(it.dealYear, it.dealMonth, it.dealDay),
                parseInt(it.buildYear)
        );
    }

    private static int parseAmount(String s) {       // "550,000" → 550000
        if (s == null || s.isBlank()) {
            return 0;
        }
        return Integer.parseInt(s.replace(",", "").trim());
    }

    private static double parseDouble(String s) {
        return (s == null || s.isBlank()) ? 0 : Double.parseDouble(s.trim());
    }

    private static int parseInt(String s) {
        return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
    }

    private static LocalDate parseDate(String y, String m, String d) {
        if (y == null || m == null || d == null) {
            return null;
        }
        return LocalDate.of(parseInt(y), parseInt(m), parseInt(d));
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
