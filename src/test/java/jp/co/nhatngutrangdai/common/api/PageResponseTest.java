package jp.co.nhatngutrangdai.common.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class PageResponseTest {

    @Test
    void mapsPageMetadataWithoutExposingPersistenceTypes() {
        var source = new PageImpl<>(
                List.of("first", "second"),
                PageRequest.of(1, 2),
                5);

        PageResponse<String> response = PageResponse.from(source);

        assertEquals(List.of("first", "second"), response.content());
        assertEquals(1, response.page());
        assertEquals(2, response.size());
        assertEquals(5, response.totalElements());
        assertEquals(3, response.totalPages());
        assertFalse(response.first());
        assertFalse(response.last());
    }

    @Test
    void copiesContentToKeepResponseImmutable() {
        var content = new java.util.ArrayList<>(List.of("item"));
        var response = new PageResponse<>(content, 0, 20, 1, 1, true, true);

        content.add("later");

        assertEquals(List.of("item"), response.content());
        assertTrue(response.first());
        assertTrue(response.last());
    }
}
