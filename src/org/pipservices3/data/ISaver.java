package org.pipservices3.data;

import java.util.*;

import org.pipservices3.commons.errors.*;

/**
 * Interface for data processing components that save data items.
 */
public interface ISaver<T> {
	/**
	 * Saves given data items.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param entities      a list of items to save.
	 * @throws ApplicationException when error occured.
	 */
	void save(String correlationId, List<T> entities) throws ApplicationException;
}
