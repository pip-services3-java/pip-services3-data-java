package org.pipservices.data.persistence;

import java.util.*;

import org.pipservices.commons.errors.*;
import org.pipservices.commons.refer.*;
import org.pipservices.commons.run.*;
import org.pipservices.components.log.CompositeLogger;
import org.pipservices.data.*;

public abstract class MemoryPersistence<T> implements IReferenceable, IOpenable, ICleanable {
	protected Class<?> _type;
	protected String _typeName;
    
    protected CompositeLogger _logger = new CompositeLogger();
    
    protected List<T> _items = new ArrayList<T>();
    protected ILoader<T> _loader;
    protected ISaver<T> _saver;
    protected boolean _opened = false;
    protected Object _lock = new Object();

    protected MemoryPersistence(Class<T> type) {
    	this(type, null, null);
    }

    // Pass the item type since Jackson cannot recognize type from generics
    // This is related to Java type erasure issue
    protected MemoryPersistence(Class<T> type, ILoader<T> loader, ISaver<T> saver) {
        _type = type;
        _typeName = type.getName();
        _loader = loader;
        _saver = saver;
    }

    public void setReferences(IReferences references) throws ReferenceException {
    	_logger.setReferences(references);
    }
        
    public boolean isOpen() {
    	return _opened;
    }
    
    public void open(String correlationId) throws ApplicationException {
    	load(correlationId);
    	_opened = true;
    }

    public void close(String correlationId) throws ApplicationException {
    	save(correlationId);
    	_opened = false;
    }

    public void load(String correlationId) throws ApplicationException {
    	if (_loader != null) {
    		synchronized (_lock) {
    			_items = _loader.load(correlationId);
    	        _logger.trace(correlationId, "Loaded %d of %s", _items.size(), _typeName);
    		}
    	}
    }

    public void save(String correlationId) throws ApplicationException {
    	if (_saver != null) {
    		synchronized (_lock) {
    			_saver.save(correlationId, _items);
    	        _logger.trace(correlationId, "Saved %d of %s", _items.size(), _typeName);
    		}
    	}
    }

    public void clear(String correlationId) throws ApplicationException {
    	synchronized (_lock) {
	        _items = new ArrayList<T>();
	        _logger.trace(correlationId, "Cleared %s", _typeName);
	        save(correlationId);	        
    	}
    }

}