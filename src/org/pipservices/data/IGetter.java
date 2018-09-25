package org.pipservices.data;

import org.pipservices.commons.data.IIdentifiable;
import org.pipservices.commons.errors.*;

/**
 * Interface for data processing components that can get data items.
 */
public interface IGetter<T extends IIdentifiable<K>, K> {
	/**
	 * Gets a data items by its unique id.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param id            an id of item to be retrieved.
	 * @return an item by its id.
	 * @throws ApplicationException when error occured.
	 */
	T getOneById(String correlationId, K id) throws ApplicationException;
}
