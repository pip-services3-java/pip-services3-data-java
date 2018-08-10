package org.pipservices.data.file;

import org.junit.*;
import org.pipservices.commons.errors.*;
import org.pipservices.data.*;

public class DummyFilePersistenceTest {
    private static DummyFilePersistence db;
    private static DummyPersistenceFixture fixture;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        db = new DummyFilePersistence("data/dummies.json");
        fixture = new DummyPersistenceFixture(db);
	}

	@Before
	public void setUp() throws ApplicationException {
		db.clear(null);
	}

	@Test
    public void testCrudOperations() throws ApplicationException {
        fixture.testCrudOperations();
    }
	
	@Test
	public void testLoadData() throws ApplicationException {
		db.load(null);
	}
}
