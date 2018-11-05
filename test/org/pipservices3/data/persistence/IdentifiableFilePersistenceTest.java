//package org.pipservices3.data.persistence;
//
//import org.junit.Test;
//import org.pipservices3.commons.config.ConfigParams;
//import org.pipservices3.commons.errors.ApplicationException;
//
//public class IdentifiableFilePersistenceTest {
//	private static IdentifiableFilePersistence<Dummy, String> Db 
//    = new IdentifiableFilePersistence<Dummy, String>(Dummy.class);
//	private static PersistenceFixture _fixture;
//	public static PersistenceFixture getFixture() { return _fixture; }
//	public static void setFixture(PersistenceFixture fixture) { _fixture = fixture; }
//	
//	private PersistenceFixture _getFixture(){
//        return new PersistenceFixture(Db, Db, Db, Db, Db, Db, Db, Db);
//    }
//	
//	public IdentifiableFilePersistenceTest() throws ApplicationException {
//		if (Db == null)
//            return;
//
//        Db.configure(ConfigParams.fromTuples("path", IdentifiableFilePersistenceTest.class.getName()));
//
//        Db.open(null);
//        
//        Db.clear(null);
//        
//        _fixture = _getFixture();
//    }
//	
//	@Test
//	public void testCrudOperations() throws ApplicationException {
//        if (_fixture == null) return;
//
//        _fixture.testCrudOperations();
//    }
//}
