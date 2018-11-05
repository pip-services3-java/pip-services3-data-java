package org.pipservices3.data.persistence;

import org.junit.*;
import org.pipservices3.commons.errors.*;

public class DummyFilePersistenceTest {
    private static DummyFilePersistence db;
    private static DummyPersistenceFixture fixture;

	@Before
	public void setUpBeforeClass() throws Exception {
        db = new DummyFilePersistence("./data/dummies.json");
        fixture = new DummyPersistenceFixture(db);
        db.open(null);
        db.clear(null);
	}
	

//	@Before
//	public void setUp() throws ApplicationException {
//		
//		db.clear(null);
//	}

	@Test
    public void testCrudOperations() throws ApplicationException {
        fixture.testCrudOperations();
    }
	
	@Test
    public void testBatchOperations() throws ApplicationException {
        fixture.testBatchOperations();
    }	
}
