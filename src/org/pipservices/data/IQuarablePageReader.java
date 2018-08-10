package org.pipservices.data;

import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

public interface IQuarablePageReader<T> {
	DataPage<T> getPageByQuery(String correlationId, String query, PagingParams paging, SortParams sort) 
		throws ApplicationException;
}
