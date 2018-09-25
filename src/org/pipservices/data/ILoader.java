package org.pipservices.data;

import java.util.*;

import org.pipservices.commons.errors.*;

/**
 * Interface for data processing components that load data items.
 */
public interface ILoader<T> {
	/**
	 * Loads data items.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @return a list of data items.
	 * @throws ApplicationException when error occured.
	 */
	List<T> load(String correlationId) throws ApplicationException;
}
