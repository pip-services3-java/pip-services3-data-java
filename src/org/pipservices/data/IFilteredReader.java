package org.pipservices.data;

import java.util.*;

import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

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
