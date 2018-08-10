package org.pipservices.data;

import java.util.*;

import org.pipservices.commons.errors.*;

public interface ILoader<T> {
	List<T> load(String correlationId) throws ApplicationException;
}
