package org.pipservices3.data;

import org.pipservices3.commons.data.DataPage;
import org.pipservices3.commons.data.PagingParams;
import org.pipservices3.commons.data.SortParams;
import org.pipservices3.commons.errors.ApplicationException;

/**
 * Interface for data processing components that can query a page of data items.
 */
public interface IQuarablePageReader<T> {
	/**
	 * Gets a page of data items using a query string.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param query         (optional) a query string
	 * @param paging        (optional) paging parameters
	 * @param sort          (optional) sort parameters
	 * @return a list of items by query.
	 * @throws ApplicationException when error occured.
	 */
	DataPage<T> getPageByQuery(String correlationId, String query, PagingParams paging, SortParams sort)
			throws ApplicationException;
}
