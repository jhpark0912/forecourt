package com.forecourt.global.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * 국토부 실거래가 상세 API의 XML 응답 매핑.
 *
 * <p>record가 아닌 일반 클래스(필드 바인딩)다 — jackson-xml의 {@code <items><item>} 래퍼 리스트는
 * record 생성자 바인딩에서 깨지기 때문. 모든 필드는 원문 문자열로 받고(예: {@code dealAmount}="550,000"),
 * 숫자/일자 변환은 {@link com.forecourt.domain.collector.AptTrade} 에서 수행한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "response")
public class MolitApiResponse {

    @JacksonXmlProperty(localName = "header")
    public Header header;

    @JacksonXmlProperty(localName = "body")
    public Body body;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        public String resultCode;
        @JacksonXmlProperty(localName = "resultMsg")
        public String resultMsg;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "item")
        public List<Item> items;

        @JacksonXmlProperty(localName = "numOfRows")
        public int numOfRows;
        @JacksonXmlProperty(localName = "pageNo")
        public int pageNo;
        @JacksonXmlProperty(localName = "totalCount")
        public int totalCount;

        /** 거래가 없는 달이면 items 가 null 일 수 있다. */
        public List<Item> safeItems() {
            return items == null ? List.of() : items;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JacksonXmlProperty(localName = "aptSeq")
        public String aptSeq;
        @JacksonXmlProperty(localName = "aptNm")
        public String aptNm;
        @JacksonXmlProperty(localName = "umdNm")
        public String umdNm;
        @JacksonXmlProperty(localName = "jibun")
        public String jibun;
        @JacksonXmlProperty(localName = "excluUseAr")
        public String excluUseAr;
        @JacksonXmlProperty(localName = "dealAmount")
        public String dealAmount;
        @JacksonXmlProperty(localName = "floor")
        public String floor;
        @JacksonXmlProperty(localName = "dealYear")
        public String dealYear;
        @JacksonXmlProperty(localName = "dealMonth")
        public String dealMonth;
        @JacksonXmlProperty(localName = "dealDay")
        public String dealDay;
        @JacksonXmlProperty(localName = "buildYear")
        public String buildYear;
    }
}
