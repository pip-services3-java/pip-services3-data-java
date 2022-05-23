package org.pipservices3.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pipservices3.commons.data.AnyValueMap;
import org.pipservices3.commons.errors.ApplicationException;

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
	T updatePartially(String correlationId, K id, AnyValueMap data) throws ApplicationException;
}
