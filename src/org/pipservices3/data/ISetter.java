package org.pipservices3.data;

import org.pipservices3.commons.errors.ApplicationException;

/**
 * Interface for data processing components that can set (create or update) data items.
 */
public interface ISetter<T> {
	/**
	 * Sets a data item. If the data item exists it updates it, otherwise it create
	 * a new data item.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param entity        a item to be set.
	 * @return updated item.
	 * @throws ApplicationException when error occured.
	 */
	T set(String correlationId, T entity) throws ApplicationException;
}
