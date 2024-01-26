package org.vaadin.artur.spring.dataprovider;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.util.Pair;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

public class PageableDataProviderTest {

    @Test
    public void pageConversion() {
        assertRangeToPageSizeAndNumber(0, 9, 10, 0); // 0-9 => size 10, page 0
        assertRangeToPageSizeAndNumber(1, 9, 10, 0);
        assertRangeToPageSizeAndNumber(2, 9, 10, 0);
        assertRangeToPageSizeAndNumber(3, 9, 10, 0);
        assertRangeToPageSizeAndNumber(4, 9, 10, 0);
        assertRangeToPageSizeAndNumber(5, 9, 5, 1);
        assertRangeToPageSizeAndNumber(6, 9, 5, 1);
        assertRangeToPageSizeAndNumber(7, 9, 5, 1);
        assertRangeToPageSizeAndNumber(8, 9, 2, 4);
        assertRangeToPageSizeAndNumber(9, 9, 1, 9);

        assertRangeToPageSizeAndNumber(6, 8, 3, 2);
        assertRangeToPageSizeAndNumber(0, 10, 11, 0);
        assertRangeToPageSizeAndNumber(1, 10, 11, 0);

        assertRangeToPageSizeAndNumber(100000, 100010, 16, 6250);
        assertRangeToPageSizeAndNumber(100000, 100009, 10, 10000);
    }

    private void assertRangeToPageSizeAndNumber(int from, int to, int pageSize,
            int pageNumber) {
        int limit = to - from + 1;
        Pair<Integer, Integer> resultPageSizeAndNumber = PageableDataProvider
                .limitAndOffsetToPageSizeAndNumber(from, limit);
        Assert.assertEquals("Converting " + from + "-" + to,
                Pair.of(pageSize, pageNumber), resultPageSizeAndNumber);

    }
}
