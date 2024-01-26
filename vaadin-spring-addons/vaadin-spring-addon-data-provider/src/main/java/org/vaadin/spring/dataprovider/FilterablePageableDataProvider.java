package org.vaadin.artur.spring.dataprovider;

import java.util.Optional;

public abstract class FilterablePageableDataProvider<T, F>
        extends PageableDataProvider<T, F> {
    private String filter = "";

    public void setFilter(String filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }
        this.filter = filter;
        refreshAll();
    }

    protected Optional<String> getOptionalFilter() {
        if ("".equals(filter)) {
            return Optional.empty();
        } else {
            return Optional.of(filter);
        }
    }
}
