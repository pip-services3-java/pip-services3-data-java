package org.pipservices.data.persistence;

import java.io.*;
import java.util.*;

import org.pipservices.commons.config.*;
import org.pipservices.commons.errors.*;
import org.pipservices.components.log.*;
import org.pipservices.data.*;

import com.fasterxml.jackson.databind.*;

/**
 * Persistence component that loads and saves data from/to flat file.
 * <p>
 * It is used by {@link FilePersistence}, but can be useful on its own.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>path:          path to the file where data is stored
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * JsonFilePersister persister = new JsonFilePersister(MyData.class, "./data/data.json");
 * 
 * ArrayList<String> list = new ArrayList<String>() {{
 *    add("A");
 *    add("B");
 *    add("C");
 * }};
 * persister.save("123", list);
 * ...
 * persister.load("123", items);
 * System.out.println(items); // Result: ["A", "B", "C"]  
 * }
 * </pre>
 */
public class JsonFilePersister<T> implements ILoader<T>, ISaver<T>, IConfigurable {
	protected ObjectMapper _mapper = new ObjectMapper();
	protected Class<T> _type;
	protected JavaType _typeRef;
	protected String _path;
	protected CompositeLogger _logger = new CompositeLogger();

	// Pass the item type since Jackson cannot recognize type from generics
	// This is related to Java type erasure issue

	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type the class type.
	 */
	public JsonFilePersister(Class<T> type) {
		this(type, null);
	}

	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type the class type.
	 * @param path (optional) a path to the file where data is stored.
	 */
	public JsonFilePersister(Class<T> type, String path) {
		_type = type;
		_typeRef = _mapper.getTypeFactory().constructCollectionType(List.class, _type);
		_path = path;
	}

	/**
	 * Gets the file path where data is stored.
	 * 
	 * @return the file path where data is stored.
	 */
	public String getPath() {
		return _path;
	}

	/**
	 * Sets the file path where data is stored.
	 * 
	 * @param value the file path where data is stored.
	 */
	public void setPath(String value) {
		_path = value;
	}

	/**
	 * Configures component by passing configuration parameters.
	 * 
	 * @param config configuration parameters to be set.
	 * @throws ConfigException when configuration is wrong.
	 */
	public void configure(ConfigParams config) throws ConfigException {
		if (config == null || !config.containsKey("path"))
			throw new ConfigException(null, "NO_PATH", "Data file path is not set");

		_path = config.getAsString("path");
	}

//    public void setReferences(IReferences references)
//    {
//        _logger.setReferences(references);
//    }

	/**
	 * Loads data items from external JSON file.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @return loaded items.
	 * @throws ApplicationException when error occured.
	 */
	public List<T> load(String correlationId) throws ApplicationException {
		File file = new File(_path);

		// If doesn't exist then consider empty data
		if (!file.exists())
			return new ArrayList<T>();
		try {
			return _mapper.readValue(file, _typeRef);
		} catch (Exception ex) {
			throw new FileException(correlationId, "READ_FAILED", "Failed to read data file: " + ex).withCause(ex);
		}
	}

	/**
	 * Saves given data items to external JSON file.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param entities      list if data items to save
	 * @throws ApplicationException when error occured.
	 */
	public void save(String correlationId, List<T> entities) throws ApplicationException {
		File file = new File(_path);

		try {
			_mapper.writeValue(file, entities);
		} catch (Exception ex) {
			throw new FileException(correlationId, "WRITE_FAILED", "Failed to write data file: " + ex).withCause(ex);
		}
	}

}