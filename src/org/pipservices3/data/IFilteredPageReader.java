package org.pipservices3.data;

import org.pipservices3.commons.data.DataPage;
import org.pipservices3.commons.data.FilterParams;
import org.pipservices3.commons.data.PagingParams;
import org.pipservices3.commons.data.SortParams;
import org.pipservices3.commons.errors.ApplicationException;

/**
 * Interface for data processing components that can retrieve a page of data items by a filter.
 */
public interface IFilteredPageReader<T> {
	/**
	 * Gets a page of data items using filter parameters.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param filter        (optional) filter parameters
	 * @param paging        (optional) paging parameters
	 * @param sort          (optional) sort parameters
	 * @return list of filtered items.
	 * @throws ApplicationException when error occured.
	 */
	DataPage<T> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging, SortParams sort)
			throws ApplicationException;
}
