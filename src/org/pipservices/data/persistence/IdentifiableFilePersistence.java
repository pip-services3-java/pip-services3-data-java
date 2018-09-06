package org.pipservices.data.persistence;

import org.pipservices.commons.config.*;
import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

public class IdentifiableFilePersistence<T extends IIdentifiable<K>, K> extends IdentifiableMemoryPersistence<T, K> {
    protected JsonFilePersister<T> _persister;

    // Pass the item type since Jackson cannot recognize type from generics
    // This is related to Java type erasure issue
    protected IdentifiableFilePersistence(Class<T> type) {
    	this(type, null);
    }
    
    protected IdentifiableFilePersistence(Class<T> type, JsonFilePersister<T> persister) {
    	super(type, persister == null ? new JsonFilePersister<T>(type) : persister, 
			    persister == null ? new JsonFilePersister<T>(type) : persister);
	
    	_persister = persister;
//    	super(type);
//    	
//    	_persister = new JsonFilePersister<T>(type);
//    	_loader = _persister;
//    	_saver = _persister;
    }

    public void configure(ConfigParams config) throws ConfigException {
        super.configure(config);
    	_persister.configure(config);
    }
}