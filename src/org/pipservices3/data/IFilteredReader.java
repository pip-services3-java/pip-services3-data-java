package org.pipservices3.data;

import org.pipservices3.commons.data.FilterParams;
import org.pipservices3.commons.data.SortParams;
import org.pipservices3.commons.errors.ApplicationException;

import java.util.List;

/**
 * Interface for data processing components that can retrieve a list of data items by filter.
 */
public interface IFilteredReader<T> {
	/**
	 * Gets a list of data items using filter parameters.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param filter        (optional) filter parameters
	 * @param sort          (optional) sort parameters
	 * @return list of filtered items.
	 * @throws ApplicationException when error occured.
	 */
	List<T> getListByFilter(String correlationId, FilterParams filter, SortParams sort) throws ApplicationException;
}
