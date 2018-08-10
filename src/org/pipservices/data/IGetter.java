package org.pipservices.data;

import org.pipservices.commons.errors.*;

public interface IGetter<T, K> {
	T getOneById(String correlationId, K id) throws ApplicationException;
}
