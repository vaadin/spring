package org.vaadin.artur.spring.dataprovider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.util.Pair;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;

public abstract class PageableDataProvider<T, F>
        extends AbstractBackEndDataProvider<T, F> {

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, F> query) {
        Pageable pageable = getPageable(query);
        Page<T> result = fetchFromBackEnd(query, pageable);
        return fromPageable(result, pageable, query);
    }

    protected abstract Page<T> fetchFromBackEnd(Query<T, F> query,
            Pageable pageable);

    private Pageable getPageable(Query<T, F> q) {
        Pair<Integer, Integer> pageSizeAndNumber = limitAndOffsetToPageSizeAndNumber(
                q.getOffset(), q.getLimit());
        return PageRequest.of(pageSizeAndNumber.getSecond(),
                pageSizeAndNumber.getFirst(), createSpringSort(q));
    }

    private Sort createSpringSort(Query<T, F> q) {
        List<QuerySortOrder> sortOrders;
        if (q.getSortOrders().isEmpty()) {
            sortOrders = getDefaultSortOrders();
        } else {
            sortOrders = q.getSortOrders();
        }
        List<Order> orders = sortOrders.stream()
                .map(PageableDataProvider::queryOrderToSpringOrder)
                .collect(Collectors.toList());
        if (orders.isEmpty()) {
            return null;
        } else {
            return Sort.by(orders);
        }
    }

    protected abstract List<QuerySortOrder> getDefaultSortOrders();

    private static Order queryOrderToSpringOrder(QuerySortOrder queryOrder) {
        return new Order(
                queryOrder.getDirection() == SortDirection.ASCENDING
                        ? Direction.ASC : Direction.DESC,
                queryOrder.getSorted());
    }

    public static Pair<Integer, Integer> limitAndOffsetToPageSizeAndNumber(
            int offset, int limit) {
        int minPageSize = limit;
        int lastIndex = offset + limit - 1;
        int maxPageSize = lastIndex + 1;

        for (double pageSize = minPageSize; pageSize <= maxPageSize; pageSize++) {
            int startPage = (int) (offset / pageSize);
            int endPage = (int) (lastIndex / pageSize);
            if (startPage == endPage) {
                // It fits on one page, let's go with that
                return Pair.of((int) pageSize, startPage);
            }
        }

        // Should not really get here
        return Pair.of(maxPageSize, 0);
    }

    private Stream<T> fromPageable(Page<T> result, Pageable pageable,
            Query<T, ?> query) {
        List<T> items = result.getContent();

        int firstRequested = query.getOffset();
        int nrRequested = query.getLimit();
        int firstReturned = (int) pageable.getOffset();
        int firstReal = firstRequested - firstReturned;
        int afterLastReal = firstReal + nrRequested;
        if (afterLastReal > items.size()) {
            afterLastReal = items.size();
        }
        return items.subList(firstReal, afterLastReal).stream();
    }

}
