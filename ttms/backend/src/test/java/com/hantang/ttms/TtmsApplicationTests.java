package com.hantang.ttms;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.hantang.ttms.common.CommonUtils;

@SpringBootTest
class TtmsApplicationTests {

    @Test
    void contextLoads() {
        // 验证 Spring 上下文可正常加载
    }

    @Test
    void testCommonUtilsIsBlank() {
        assertThat(CommonUtils.isBlank(null)).isTrue();
        assertThat(CommonUtils.isBlank("")).isTrue();
        assertThat(CommonUtils.isBlank("  ")).isTrue();
        assertThat(CommonUtils.isBlank("hello")).isFalse();
    }

    @Test
    void testCommonUtilsIsEmptyCollection() {
        assertThat(CommonUtils.isEmpty(null)).isTrue();
        assertThat(CommonUtils.isEmpty(List.of())).isTrue();
        assertThat(CommonUtils.isEmpty(List.of("a"))).isFalse();
    }

    @Test
    void testCommonUtilsIsEmptyMap() {
        assertThat(CommonUtils.isEmpty((Map<?, ?>) null)).isTrue();
        assertThat(CommonUtils.isEmpty((Map<?, ?>) Map.of())).isTrue();
        assertThat(CommonUtils.isEmpty((Map<?, ?>) Map.of("k", "v"))).isFalse();
    }

    @Test
    void testCommonUtilsTruncate() {
        assertThat(CommonUtils.truncate(null, 5)).isNull();
        assertThat(CommonUtils.truncate("hello", 5)).isEqualTo("hello");
        assertThat(CommonUtils.truncate("hello world", 5)).isEqualTo("hello");
    }

    @Test
    void testCommonUtilsFormatNow() {
        String result = CommonUtils.formatNow("yyyy-MM-dd");
        assertThat(result).matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
