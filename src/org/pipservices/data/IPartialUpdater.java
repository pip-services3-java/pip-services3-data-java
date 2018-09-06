package org.pipservices.data;

import org.pipservices.commons.data.AnyValueMap;

public interface IPartialUpdater<T, K> {
	T updatePartially(String correlationId, K id, AnyValueMap data);
}
