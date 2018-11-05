package org.pipservices3.data.persistence;

import org.pipservices3.commons.config.*;
import org.pipservices3.commons.errors.*;
import org.pipservices3.data.persistence.JsonFilePersister;


public class DummyFilePersistence extends DummyMemoryPersistence {
	protected JsonFilePersister<Dummy> _persister;

	public DummyFilePersistence() {
		super();
	}
	
    public DummyFilePersistence(String path) {
    	super();
    	
    	_persister = new JsonFilePersister<Dummy>(Dummy.class, path);
    	_loader = _persister;
    	_saver = _persister;
    }
    
    public void configure(ConfigParams config) throws ConfigException {
        super.configure(config);
    	_persister.configure(config);
    }
}
