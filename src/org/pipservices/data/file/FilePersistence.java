package org.pipservices.data.file;

import org.pipservices.commons.config.*;
import org.pipservices.commons.errors.*;
import org.pipservices.data.memory.*;

public abstract class FilePersistence<T> extends MemoryPersistence<T> implements IReconfigurable {
    protected JsonFilePersister<T> _persister;

    // Pass the item type since Jackson cannot recognize type from generics
    // This is related to Java type erasure issue
    protected FilePersistence(Class<T> type) {
    	super(type);
    	
    	_persister = new JsonFilePersister<T>(type);
    	_loader = _persister;
    	_saver = _persister;
    }

    public void configure(ConfigParams config) throws ConfigException {
    	_persister.configure(config);
    }
}