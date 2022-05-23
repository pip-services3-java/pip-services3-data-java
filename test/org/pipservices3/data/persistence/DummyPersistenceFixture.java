package org.pipservices3.data.persistence;

import static org.junit.Assert.*;

import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pipservices3.commons.data.AnyValueMap;
import org.pipservices3.commons.errors.*;
import org.pipservices3.commons.random.RandomString;

public class DummyPersistenceFixture {

    private final Dummy _dummy1 = new Dummy("1", "Key 1", "Content 1", new Date(),
            new InnerDummy("1", "Inner dummy name 1", "Inner dummy description 1"), DummyType.Dummy, new ArrayList<InnerDummy>());
    private final Dummy _dummy2 = new Dummy("2", "Key 2", "Content 2", new Date(),
            new InnerDummy("5", "Inner dummy name 5", "Inner dummy description 5"), DummyType.NotDummy, new ArrayList<InnerDummy>());

    private final IDummyPersistence _persistence;

    public DummyPersistenceFixture(IDummyPersistence persistence) {
        //assertNotNull(persistence);

        _persistence = persistence;
    }

    public void testCrudOperations() throws ApplicationException {
        // Create one dummy
        Dummy dummy1 = _persistence.create(null, _dummy1);

        assertNotNull(dummy1);
        assertNotNull(dummy1.getId());
        assertEquals(_dummy1.getKey(), dummy1.getKey());
        assertEquals(_dummy1.getContent(), dummy1.getContent());

        // Create another dummy
        Dummy dummy2 = _persistence.create("", _dummy2);

        assertNotNull(dummy2);
        assertNotNull(dummy2.getId());
        assertEquals(_dummy2.getKey(), dummy2.getKey());
        assertEquals(_dummy2.getContent(), dummy2.getContent());

        var page = this._persistence.getPageByFilter(null, null, null);
        assertNotNull(page);
        assertEquals(page.getData().size(), 2);

        // Update the dummy
        dummy1.setContent("Updated Content 1");
        Dummy dummy = _persistence.update(null, dummy1);

        assertNotNull(dummy);
        assertEquals(dummy1.getId(), dummy.getId());
        assertEquals(_dummy1.getKey(), dummy.getKey());
        assertEquals(_dummy1.getContent(), dummy.getContent());

        // Partially update the dummy
        dummy = this._persistence.updatePartially(
                null, dummy1.getId(),
                AnyValueMap.fromTuples(
                        "content", "Partially Updated Content 1"
                )
        );

        assertNotNull(dummy);
        assertEquals(dummy1.getId(), dummy.getId());
        assertEquals(dummy1.getKey(), dummy.getKey());
        assertEquals("Partially Updated Content 1", dummy.getContent());

        // Get the dummy by Id
        dummy = this._persistence.getOneById(null, dummy1.getId());
        assertNotNull(dummy);
        assertEquals(dummy1.getId(), dummy.getId());
        assertEquals(dummy1.getKey(), dummy.getKey());
        assertEquals("Partially Updated Content 1", dummy.getContent());


        // Delete the dummy
        _persistence.deleteById(null, dummy1.getId());
        assertNotNull(dummy);
        assertEquals(dummy1.getId(), dummy.getId());
        assertEquals(dummy1.getKey(), dummy.getKey());
        assertEquals("Partially Updated Content 1", dummy.getContent());

        // Try to get deleted dummy
        dummy = _persistence.getOneById(null, dummy1.getId());
        assertNull(dummy);

        // Count total number of objects
        var count = this._persistence.getCountByFilter(null, null);
        assertEquals(count, 1);
    }

    public void testPageSortingOperations() throws ApplicationException {
        for (var d = 0; d < 20; d++) {
            this._persistence.create(
                    null,
                    new Dummy(
                            RandomString.nextString(16, 16),
                            RandomString.nextString(1, 50),
                            String.format("Key %d", d),
                            null, null, null, null
                    )
            );
        }

        Comparator<Dummy> sortFunc = Comparator.comparingInt(o -> o.getContent().length());

        var page = this._persistence.getSortedPage(null, sortFunc);

        var prevDp = page.getData().get(0);
        var atLeastAssert = false;
        for (var index = 1; index < page.getData().size(); index++) {
            if (prevDp.getContent().length() == page.getData().get(index).getContent().length())
                atLeastAssert = true;
            prevDp = page.getData().get(index);
        }

        assertTrue(atLeastAssert);
    }

    public void testListSortingOperations() throws ApplicationException {
        // Create random objects
        for (var index = 0; index < 20; index++) {
            this._persistence.create(
                    null,
                    new Dummy(
                            RandomString.nextString(16, 16),
                            RandomString.nextString(1, 50),
                            String.format("Key %d", index),
                            null, null, null, null
                    )
            );
        }

        Comparator<Dummy> sortFunc = Comparator.comparingInt(o -> o.getContent().length());
        var list = this._persistence.getSortedList(null, sortFunc);

        var prevDp = list.get(0);
        var atLeastAssert = false;
        for (var dp = 1; dp < list.size(); dp++) {
            if (prevDp.getContent().length() == list.get(dp).getContent().length())
                atLeastAssert = true;
            prevDp = list.get(dp);
        }
        assertTrue(atLeastAssert);
    }

    public void testBatchOperations() throws ApplicationException {
        // Create one dummy
        Dummy dummy1 = _persistence.create(null, _dummy1);

        assertNotNull(dummy1);
        assertNotNull(dummy1.getId());
        assertEquals(_dummy1.getKey(), dummy1.getKey());
        assertEquals(_dummy1.getContent(), dummy1.getContent());

        // Create another dummy
        Dummy dummy2 = _persistence.create("", _dummy2);

        assertNotNull(dummy2);
        assertNotNull(dummy2.getId());
        assertEquals(_dummy2.getKey(), dummy2.getKey());
        assertEquals(_dummy2.getContent(), dummy2.getContent());

        // Read batch
        List<Dummy> dummies = _persistence.getListByIds(null, new String[]{dummy1.getId(), dummy2.getId()});
        assertEquals(2, dummies.size());

        // Read batch
        var items = this._persistence.getListByIds(null, List.of(dummy1.getId(), dummy2.getId()));
        assertNotNull(items);
        assertEquals(2, items.size());

        // Delete batch
        _persistence.deleteByIds(null, new String[]{dummy1.getId(), dummy2.getId()});

        // Read empty batch
        dummies = _persistence.getListByIds(null, new String[]{dummy1.getId(), dummy2.getId()});
        assertNotNull(items);
        assertEquals(0, dummies.size());
    }
}
