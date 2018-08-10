package org.pipservices.data;

import org.pipservices.commons.errors.*;

public interface IWriter<T, K> {
	T create(String correlationId, T entity) throws ApplicationException;
	T update(String correlationId, T entity) throws ApplicationException;
	T deleteById(String correlationId, K id) throws ApplicationException;
}
