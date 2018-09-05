package org.pipservices.data.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.pipservices.commons.config.ConfigParams;
import org.pipservices.commons.errors.ConfigException;

public class JsonFilePersisterTest {
	
	private final JsonFilePersister<Dummy> _persister;

    public JsonFilePersisterTest() {
        _persister = new JsonFilePersister<Dummy>(Dummy.class);
    }
    
    @Test
    public void configureIfNoPathKeyFails() {
    	Throwable e = null;
    	
    	try {
			_persister.configure(new ConfigParams());
		} catch (Throwable  ex) {
			e = ex;
		}       
    	assertTrue(e instanceof ConfigException);
    }

    @Test
    public void configureIfPathKeyCheckPropertyIsOk()
    {
        String fileName = JsonFilePersisterTest.class.getName();

        try {
			_persister.configure(ConfigParams.fromTuples("path", fileName));
		} catch (ConfigException e) {
			e.printStackTrace();
		}

        assertEquals(fileName, _persister.getPath());
    }
}
