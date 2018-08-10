package org.pipservices.data;

import org.pipservices.commons.errors.*;
import org.pipservices.commons.data.*;

public interface IDynamicWriter<T, K> {
	T create(String correlationId, AnyValueMap entityData) throws ApplicationException;
	T update(String correlationId, K id, AnyValueMap entityData) throws ApplicationException;
	T deleteById(String correlationId, K id) throws ApplicationException;
}
