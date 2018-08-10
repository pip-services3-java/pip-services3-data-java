package org.pipservices.data;

import org.pipservices.commons.errors.*;

public interface ISetter<T> {
	T set(String correlationId, T entity) throws ApplicationException;
}
