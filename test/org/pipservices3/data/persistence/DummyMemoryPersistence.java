package org.pipservices3.data.persistence;

import org.pipservices3.commons.data.*;
import org.pipservices3.commons.errors.*;

public class DummyMemoryPersistence extends IdentifiableMemoryPersistence<Dummy, String> implements IDummyPersistence {
	
	protected DummyMemoryPersistence() {
		super(Dummy.class);
	}

    public DataPage<Dummy> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging) 
		throws ApplicationException {
		
        filter = filter != null ? filter : new FilterParams();
        String key = filter.getAsNullableString("key");

        return getPageByFilter(
        	correlationId, 
    		(v) -> {
    			// Filter by key
                if (key != null && v.getKey() != key)
                    return false;
                return true;
            },
            paging, 
            // Todo: Add sorting later
            null
        );
    }

}
