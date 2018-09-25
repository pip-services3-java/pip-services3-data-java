package org.pipservices.data;

import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

/**
 * Interface for data processing components that can create, update and delete data items.
 */
public interface IWriter<T extends IIdentifiable<K>, K> {
	/**
	 * Creates a data item.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param entity        an item to be created.
	 * @return created item.
	 * @throws ApplicationException when error occured.
	 */
	T create(String correlationId, T entity) throws ApplicationException;

	/**
	 * Updates a data item.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param entity        an item to be updated.
	 * @return updated item.
	 * @throws ApplicationException when error occured.
	 */
	T update(String correlationId, T entity) throws ApplicationException;

	/**
	 * Deleted a data item by it's unique id.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param id            an id of the item to be deleted
	 * @return deleted item by unique id.
	 * @throws ApplicationException when error occured.
	 */
	T deleteById(String correlationId, K id) throws ApplicationException;
}
