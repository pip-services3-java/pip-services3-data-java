package org.pipservices.data.file;

import org.pipservices.commons.config.*;
import org.pipservices.commons.errors.*;
import org.pipservices.data.*;
import org.pipservices.data.memory.*;

public class DummyFilePersistence extends DummyMemoryPersistence {
	protected JsonFilePersister<Dummy> _persister;

	public DummyFilePersistence() {
		this(null);
	}
	
    public DummyFilePersistence(String path) {
    	_persister = new JsonFilePersister<Dummy>(Dummy.class, path);
    	_loader = _persister;
    	_saver = _persister;
    }
    
    public void configure(ConfigParams config) throws ConfigException {
        super.configure(config);
    	_persister.configure(config);
    }
}
