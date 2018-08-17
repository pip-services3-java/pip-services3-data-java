package org.pipservices.data.memory;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.pipservices.commons.config.*;
import org.pipservices.commons.data.*;
import org.pipservices.commons.random.*;
import org.pipservices.commons.errors.*;
import org.pipservices.data.*;

public abstract class IdentifiableMemoryPersistence<T extends IIdentifiable<K>, K> 
	extends MemoryPersistence<T>
	implements IReconfigurable, IWriter<T, K>, IGetter<T, K>, ISetter<T> {
	
	private final static int _defaultMaxPageSize = 100;

    protected int _maxPageSize = _defaultMaxPageSize;

    protected IdentifiableMemoryPersistence(Class<T> type) {
    	this(type, null, null);
    }

    // Pass the item type since Jackson cannot recognize type from generics
    // This is related to Java type erasure issue
    protected IdentifiableMemoryPersistence(Class<T> type, ILoader<T> loader, ISaver<T> saver) {
        super(type, loader, saver);
    }

    public void configure(ConfigParams config) throws ConfigException {    	
        _maxPageSize = config.getAsIntegerWithDefault("max_page_size", _maxPageSize);
    }     
    
    protected DataPage<T> getPageByFilter(String correlationId, Predicate<T> filter, 
		PagingParams paging, Comparator<T> sort) {
    	
    	synchronized (_lock) {
	        Stream<T> items = this._items.stream();
	
	        // Apply filter
	        if (filter != null)
	            items = items.filter(filter);
	
	        // Extract a page
	        paging = paging != null ? paging : new PagingParams();
	        long skip = paging.getSkip(-1);
	        long take = paging.getTake(_maxPageSize);
	
	        Long total = null;
	        if (paging.hasTotal()) {
	        	List<T> selectedItems = items.collect(Collectors.toList());
	        	total = (long)selectedItems.size();
	        	items  = selectedItems.stream();
	        }
	        
	        if (skip > 0)
	            items = items.skip(skip);
	        items = items.limit(take);
	
	        // Apply sorting
	        if (sort != null)
	            items = items.sorted(sort);
	
	        List<T> data = items.collect(Collectors.toList());
	        
	        _logger.trace(correlationId, "Retrieved %d of %s", data.size(), _typeName);
	        
	        return new DataPage<T>(data, total);
    	}
    }

    protected <S> DataPage<S> getPageByFilter(String correlationId, Predicate<T> filter, 
		PagingParams paging, Comparator<T> sort, Function<T, S> select) {
    	
        DataPage<T> page = getPageByFilter(correlationId, filter, paging, sort);
        Long total = page.getTotal();
        List<S> items = page.getData().stream()
    		.map(select)
    		.collect(Collectors.toList());

        return new DataPage<S>(items, total);
    }

    protected List<T> getListByFilter(String correlationId, Predicate<T> filter, Comparator<T> sort) {
    	synchronized (_lock) {
	        Stream<T> items = this._items.stream();
	
	        // Apply filter
	        if (filter != null)
	            items = items.filter(filter);
	
	        // Apply sorting
	        if (sort != null)
	            items = items.sorted(sort);
	
	        List<T> data = items.collect(Collectors.toList());
	        
	        _logger.trace(correlationId, "Retrieved %d of %s", data.size(), _typeName);

	        return data;
    	}
    }

    protected <S> List<S> getListByFilter(String correlationId, Predicate<T> filter, 
		Comparator<T> sort, Function<T, S> select) {
    	
        List<S> items = getListByFilter(correlationId, filter, sort).stream()
    		.map(select)
    		.collect(Collectors.toList());
        return items;
    }

    protected T findOne(K id) {
        Optional<T> item = _items.stream()
    		.filter((v) -> v.getId().equals(id))
    		.findFirst();
        return item.isPresent() ? item.get() : null;
    }

    public T getOneById(String correlationId, K id) {
    	synchronized (_lock) {
    		T item = findOne(id);
    		if (item != null)
    			_logger.trace(correlationId, "Retrieved %s by %s", item, id);
    		else
    			_logger.trace(correlationId, "Cannot find %s by %s", _typeName, id);
			return item;
    	}
    }
    
    public List<T> getListByIds(String correlationId, K[] id) {    	
    	List<T> result = new ArrayList<T>();
    	for( K oneId : id ) {
    		T item = getOneById(correlationId, oneId);
    		if ( item != null )
    			result.add(item);
    	}
    	return result;
    }

    public T getOneRandom(String correlationId) {
    	synchronized (_lock) {
	        if (_items.size() == 0)
	            return null;
	
	        T item = _items.get(RandomInteger.nextInteger(_items.size()));
	        
	        if (item != null)
	        	_logger.trace(correlationId, "Retrieved a random %s", _typeName);
	        else
	        	_logger.trace(correlationId, "Nothing to return as random %s", _typeName);
	        		        
	        return item;
    	}
    }

    public T create(String correlationId, T item){
    	// Assign unique string key
    	if (item instanceof IStringIdentifiable && item.getId() == null)
    		((IStringIdentifiable)item).setId(IdGenerator.nextLong());
    	
    	synchronized (_lock) {
	    	_items.add(item);	
	    	
	    	_logger.trace(correlationId, "Created %s", item);
	    	
	        try {
				save(correlationId);
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        
        return item;
    }

    public T update(String correlationId, T newItem) {
    	synchronized (_lock) {
	    	T oldItem = findOne(newItem.getId());
	    	if (oldItem == null) return null;
	    	
	        int index = _items.indexOf(oldItem);
	        if (index < 0) return null;
	
	        _items.set(index, newItem);

	        _logger.trace(correlationId, "Updated %s", newItem);
	        
	        try {
				save(correlationId);
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return newItem;
    	}
    }

    public T set(String correlationId, T newItem) {
    	// Assign unique string key
    	if (newItem instanceof IStringIdentifiable && newItem.getId() == null)
    		((IStringIdentifiable)newItem).setId(IdGenerator.nextLong());    	

    	synchronized (_lock) {
	    	T oldItem = findOne(newItem.getId());
	    	
	    	if (oldItem == null)
	    		_items.add(newItem);
	    	else {
		        int index = _items.indexOf(oldItem);
		        if (index < 0) 
		        	_items.add(newItem);
		        else
		        	_items.set(index, newItem);
	    	}
	
	        _logger.trace(correlationId, "Set %s", newItem);

	    	try {
				save(correlationId);
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return newItem;
    	}
    }

    public T deleteById(String correlationId, K id) {
    	synchronized (_lock) {
	    	T item = findOne(id);
	    	if (item == null) return null;
	    	
	        int index = _items.indexOf(item);
	        if (index < 0) return null;
	
	        _items.remove(index);

	        _logger.trace(correlationId, "Deleted %s", item);
	        
	        try {
				save(correlationId);
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return item;
    	}
    }
    
    public void deleteByIds(String correlationId, K[] id) {    	
    	boolean deleted = false;
    	List<T> result = new ArrayList<T>();
    	deleted = result.size() > 0;
    	for( K oneId : id ) {
    		T item = deleteById(correlationId, oneId);
    		if ( item != null )
    			result.remove(item);
    	}
    	if(deleted)
			try {
				save(correlationId);
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
}