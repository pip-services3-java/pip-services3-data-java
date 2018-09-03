package org.pipservices.data.persistence;

import org.junit.*;
import org.pipservices.commons.errors.*;
import org.pipservices.data.*;

public class DummyMemoryPersistenceTest {
    private static DummyMemoryPersistence db;
    private static DummyPersistenceFixture fixture;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        db = new DummyMemoryPersistence();
        fixture = new DummyPersistenceFixture(db);
	}

	@Test
    public void testCrudOperations() throws ApplicationException {
        fixture.testCrudOperations();
    }

}
