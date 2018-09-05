package org.pipservices.data.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;

import org.pipservices.commons.config.IConfigurable;
import org.pipservices.commons.errors.ApplicationException;
import org.pipservices.commons.refer.IReferenceable;
import org.pipservices.commons.run.*;
import org.pipservices.data.*;

public class PersistenceFixture {

	private final IReferenceable _refs;
    private final IConfigurable _conf;
    private final IOpenable _open;
    private final IClosable _close;
    private final ICleanable _clean;
    private final IWriter<Dummy,String> _write;
    private final IGetter<Dummy,String> _get;
    private final ISetter<Dummy> _set;

    private final Dummy _dummy1 = new Dummy("1", "Key 1", "Content 1", new Date(), 
			new InnerDummy("1", "Inner dummy name 1", "Inner dummy description 1"), DummyType.Dummy, new ArrayList<InnerDummy>());
	private final Dummy _dummy2 = new Dummy("2", "Key 2", "Content 2", new Date(), 
			new InnerDummy("5", "Inner dummy name 5", "Inner dummy description 5"), DummyType.NotDummy, new ArrayList<InnerDummy>());
    
    public PersistenceFixture(IReferenceable refs, IConfigurable conf, IOpenable open, IClosable close, ICleanable clean,
        IWriter<Dummy, String> write, IGetter<Dummy, String> get, ISetter<Dummy> set)
    {
        assertNotNull(refs);
        _refs = refs;

        assertNotNull(conf);
        _conf = conf;

        assertNotNull(open);
        _open = open;

        assertNotNull(close);
        _close = close;

        assertNotNull(clean);
        _clean = clean;

        assertNotNull(write);
        _write = write;

        assertNotNull(get);
        _get = get;

        assertNotNull(set);
        _set = set;
    }
    
    public void testCrudOperations() throws ApplicationException {
   	 // Create one dummy
       Dummy dummy1 = _write.create(null, _dummy1);

       assertNotNull(dummy1);
       assertNotNull(dummy1.getId());
       assertEquals(_dummy1.getKey(), dummy1.getKey());
       assertEquals(_dummy1.getContent(), dummy1.getContent());

       // Create another dummy
       Dummy dummy2 = _write.create("", _dummy2);

       assertNotNull(dummy2);
       assertNotNull(dummy2.getId());
       assertEquals(_dummy2.getKey(), dummy2.getKey());
       assertEquals(_dummy2.getContent(), dummy2.getContent());

       // Update the dummy
       dummy1.setContent("Updated Content 1");
       Dummy dummy = _write.update(null, dummy1);

       assertNotNull(dummy);
       assertEquals(dummy1.getId(), dummy.getId());
       assertEquals(_dummy1.getKey(), dummy.getKey());
       assertEquals(_dummy1.getContent(), dummy.getContent());

       // Delete the dummy
       _write.deleteById(null, dummy1.getId());

       // Try to get deleted dummy
       dummy = _get.getOneById(null, dummy1.getId());
       assertNull(dummy);
   }    
}
