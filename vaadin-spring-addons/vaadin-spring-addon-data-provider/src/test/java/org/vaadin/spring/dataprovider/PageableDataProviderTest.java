/*
 * Copyright 2015-2025 The original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.spring.dataprovider;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.util.Pair;

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
