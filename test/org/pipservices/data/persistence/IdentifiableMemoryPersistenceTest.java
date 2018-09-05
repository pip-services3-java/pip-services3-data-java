package org.pipservices.data.persistence;

import org.junit.Test;
import org.pipservices.commons.config.ConfigParams;
import org.pipservices.commons.errors.ApplicationException;

public class IdentifiableMemoryPersistenceTest {

	private static IdentifiableMemoryPersistence<Dummy, String> Db
	= new IdentifiableMemoryPersistence<Dummy, String>(Dummy.class);;
	private static PersistenceFixture _fixture;
	public static PersistenceFixture getFixture() { return _fixture; }
	public static void setFixture(PersistenceFixture fixture) { _fixture = fixture; }
	
	private PersistenceFixture _getFixture(){
        return new PersistenceFixture(Db, Db, Db, Db, Db, Db, Db, Db);
    }
	
	public IdentifiableMemoryPersistenceTest() throws ApplicationException {
		if (Db == null)
            return;

        Db.configure(new ConfigParams());

        Db.open(null);
        
        Db.clear(null);
        
        _fixture = _getFixture();
    }
	
	@Test
	public void testCrudOperations() throws ApplicationException {
        if (_fixture == null) return;

        _fixture.testCrudOperations();
    }
}
