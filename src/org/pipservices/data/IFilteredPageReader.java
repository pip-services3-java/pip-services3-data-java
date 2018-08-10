package org.pipservices.data;

import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

public interface IFilteredPageReader<T> {
	DataPage<T> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging, SortParams sort) 
		throws ApplicationException;
}
