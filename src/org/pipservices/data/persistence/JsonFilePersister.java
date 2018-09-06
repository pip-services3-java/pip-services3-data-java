package org.pipservices.data.persistence;

import java.io.*;
import java.util.*;

import org.pipservices.commons.config.*;
import org.pipservices.commons.errors.*;
import org.pipservices.components.log.CompositeLogger;
import org.pipservices.commons.refer.IReferences;
import org.pipservices.data.*;

import com.fasterxml.jackson.databind.*;

public class JsonFilePersister<T> implements ILoader<T>, ISaver<T>, IConfigurable {
    protected ObjectMapper _mapper = new ObjectMapper();
    protected Class<T> _type;
    protected JavaType _typeRef;
    protected String _path;
    private CompositeLogger _logger = new CompositeLogger();

    // Pass the item type since Jackson cannot recognize type from generics
    // This is related to Java type erasure issue
    public JsonFilePersister(Class<T> type) {
    	this(type, null);
    }

    public JsonFilePersister(Class<T> type, String path) {
    	_type = type;
        _typeRef = _mapper.getTypeFactory().constructCollectionType(List.class, _type);
        _path = path;
    }
    
    public String getPath() { return _path; }
    public void setPath(String value) { _path = value; }
    
    public void configure(ConfigParams config) throws ConfigException {    	
        if (config == null || !config.containsKey("path"))
            throw new ConfigException(null, "NO_PATH", "Data file path is not set");        

        _path = config.getAsString("path");
    }
    
//    public void setReferences(IReferences references)
//    {
//        _logger.setReferences(references);
//    }

    public List<T> load(String correlationId) throws ApplicationException {
        File file = new File(_path);

        // If doesn't exist then consider empty data
        if (!file.exists())
            return new ArrayList<T>();            
        try {
            return _mapper.readValue(file, _typeRef);
        } catch (Exception ex) {
        	throw new FileException(correlationId, "READ_FAILED", "Failed to read data file: " + ex)
        		.withCause(ex);
        }
    }

    public void save(String correlationId, List<T> entities) throws ApplicationException {
    	File file = new File(_path);
    	
        try {
            _mapper.writeValue(file, entities);
        } catch (Exception ex) {
        	throw new FileException(correlationId, "WRITE_FAILED", "Failed to write data file: " + ex)
        		.withCause(ex);
        }
    }

}