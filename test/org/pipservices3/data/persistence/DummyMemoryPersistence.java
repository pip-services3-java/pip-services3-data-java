package org.pipservices3.data.persistence;

import org.pipservices3.commons.data.*;
import org.pipservices3.commons.errors.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class DummyMemoryPersistence extends IdentifiableMemoryPersistence<Dummy, String> implements IDummyPersistence {

    protected DummyMemoryPersistence() {
        super(Dummy.class);
    }

    private Predicate<Dummy> composeFilter(FilterParams filter) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        return (item) -> {
            return key == null || Objects.equals(item.getKey(), key);
        };
    }

    public DataPage<Dummy> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging) {
        return super.getPageByFilter(correlationId, composeFilter(filter), paging, null);
    }

    @Override
    public int getCountByFilter(String correlationId, FilterParams filter) {
        return super.getCountByFilter(correlationId, composeFilter(filter));
    }

    @Override
    public DataPage<Dummy> getSortedPage(String correlationId, Comparator<Dummy> sort) {
        return super.getPageByFilter(correlationId, null, null, sort);
    }

    @Override
    public List<Dummy> getSortedList(String correlationId, Comparator<Dummy> sort) throws ApplicationException {
        return super.getListByFilter(correlationId, null, sort, null);
    }

}
