package org.pipservices3.data;

import org.pipservices3.commons.errors.*;
import org.pipservices3.commons.data.*;

public interface IDynamicWriter<T, K> {
	T create(String correlationId, AnyValueMap entityData) throws ApplicationException;
	T update(String correlationId, K id, AnyValueMap entityData) throws ApplicationException;
	T deleteById(String correlationId, K id) throws ApplicationException;
}
