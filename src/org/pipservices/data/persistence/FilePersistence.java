package org.pipservices.data.persistence;

import org.pipservices.commons.config.*;
import org.pipservices.commons.errors.*;

public abstract class FilePersistence<T> extends MemoryPersistence<T> implements IConfigurable {
    protected JsonFilePersister<T> _persister;

    // Pass the item type since Jackson cannot recognize type from generics
    // This is related to Java type erasure issue
    
    protected FilePersistence(Class<T> type) {
    	this(type, null);
    }
    
    protected FilePersistence(Class<T> type, JsonFilePersister<T> persister) {
    	super(type, persister == null ? new JsonFilePersister<T>(type) : persister, 
    			    persister == null ? new JsonFilePersister<T>(type) : persister);
    	
    	_persister = persister;
//    	_loader = _persister;
//    	_saver = _persister;
    }

    public void configure(ConfigParams config) throws ConfigException {
    	_persister.configure(config);
    }
}