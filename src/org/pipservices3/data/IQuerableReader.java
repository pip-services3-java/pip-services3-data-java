package org.pipservices3.data;

import org.pipservices3.commons.data.SortParams;
import org.pipservices3.commons.errors.ApplicationException;

import java.util.List;

/**
 * Interface for data processing components that can query a list of data items.
 */
public interface IQuerableReader<T> {
	/**
	 * Gets a list of data items using a query string.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param query         (optional) a query string
	 * @param sort          (optional) sort parameters
	 * @return a list of items by query.
	 * @throws ApplicationException when error occured.
	 */
	List<T> getListByQuery(String correlationId, String query, SortParams sort) throws ApplicationException;
}
