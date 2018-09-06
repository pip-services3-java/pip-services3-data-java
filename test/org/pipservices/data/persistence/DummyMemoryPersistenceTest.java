package org.pipservices.data.persistence;

import org.junit.*;
import org.pipservices.commons.config.ConfigParams;
import org.pipservices.commons.errors.*;

public class DummyMemoryPersistenceTest {
    private static DummyMemoryPersistence db;
    private static DummyPersistenceFixture fixture;

	@Before
	public void setUpBeforeClass() throws ConfigException {
        db = new DummyMemoryPersistence();
		db.configure(new ConfigParams());

        fixture = new DummyPersistenceFixture(db);
	}

	@Test
    public void testCrudOperations() throws ApplicationException {
        fixture.testCrudOperations();
    }
	
	@Test
    public void testBatchOperations() throws ApplicationException {
        fixture.testBatchOperations();
    }

}
