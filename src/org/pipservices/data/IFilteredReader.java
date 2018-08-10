package org.pipservices.data;

import java.util.*;

import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

public interface IFilteredReader<T> {
	List<T> getListByFilter(String correlationId, FilterParams filter, SortParams sort)
		throws ApplicationException;
}
