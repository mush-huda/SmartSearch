package com.huda.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchParamsCacheKeyTest {

    @Test
    void allFieldsPresent() {
        var params = new SearchParams("Berlin", "Jazz", "Coldplay", 40.0, "2026-06-01");
        assertThat(params.cacheKey()).isEqualTo("cache:berlin:jazz:coldplay:40.0:2026-06-01");
    }

    @Test
    void nullFieldsBecomesAny() {
        var params = new SearchParams(null, null, null, null, null);
        assertThat(params.cacheKey()).isEqualTo("cache:any:any:any:any:any");
    }

    @Test
    void cityAndGenreOnlyRestAny() {
        var params = new SearchParams("Berlin", "jazz", null, null, null);
        assertThat(params.cacheKey()).isEqualTo("cache:berlin:jazz:any:any:any");
    }

    @Test
    void cacheKeyIsCaseInsensitive() {
        var lower = new SearchParams("berlin", "jazz", null, null, null);
        var upper = new SearchParams("BERLIN", "JAZZ", null, null, null);
        assertThat(lower.cacheKey()).isEqualTo(upper.cacheKey());
    }
}
