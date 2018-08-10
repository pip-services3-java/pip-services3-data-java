package org.pipservices.data;

import java.util.*;

import org.pipservices.commons.errors.*;

public interface ISaver<T> {
	void save(String correlationId, List<T> entities) throws ApplicationException;
}
