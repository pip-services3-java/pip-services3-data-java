package org.pipservices.data;

import org.pipservices.commons.data.AnyValueMap;

/**
 * Interface for data processing components to update data items partially.
 */
public interface IPartialUpdater<T, K> {
	/**
	 * Updates only few selected fields in a data item.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param id            an id of data item to be updated.
	 * @param data          a map with fields to be updated.
	 * @return updated item.
	 */
	T updatePartially(String correlationId, K id, AnyValueMap data);
}
