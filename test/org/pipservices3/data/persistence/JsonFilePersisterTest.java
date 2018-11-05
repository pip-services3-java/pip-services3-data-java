package org.pipservices3.data.persistence;

import static org.junit.Assert.*;

import org.junit.*;
import org.pipservices3.commons.config.*;
import org.pipservices3.commons.errors.*;

public class JsonFilePersisterTest {
	
	private static JsonFilePersister<Dummy> _persister;

	@Before
	public void setUpBeforeClass() {
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
