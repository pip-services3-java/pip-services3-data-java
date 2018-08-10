package org.pipservices.data;

import java.util.*;

import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

public interface IQuerableReader<T> {
	List<T> getListByQuery(String correlationId, String query, SortParams sort) 
		throws ApplicationException;
}
