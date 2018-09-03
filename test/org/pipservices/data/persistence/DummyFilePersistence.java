package org.pipservices.data.persistence;

import org.pipservices.commons.config.*;
import org.pipservices.commons.errors.*;
import org.pipservices.data.*;
<<<<<<< HEAD:test/org/pipservices/data/file/DummyFilePersistence.java
import org.pipservices.data.memory.*;
import org.pipservices.data.persistence.JsonFilePersister;
=======
>>>>>>> 5aed1309012c035f27fcea996e6becb69cbf2522:test/org/pipservices/data/persistence/DummyFilePersistence.java

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
