package org.pipservices.data;

import org.pipservices.commons.data.IIdentifiable;
import org.pipservices.commons.errors.*;

public interface IGetter<T extends IIdentifiable<K>, K> {
	T getOneById(String correlationId, K id) throws ApplicationException;
}
