package org.pipservices3.data.persistence;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InnerDummy {

	public InnerDummy() {}
	
	public InnerDummy(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	@JsonProperty("id")
	private String id;
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	@JsonProperty("name")
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@JsonProperty("description")
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	@JsonProperty("inner_inner_dummies")
	private List<InnerDummy> innerInnerDummies;
	public List<InnerDummy> getInnerInnerDummies() { return innerInnerDummies; }
	public void setInnerInnerDummies(List<InnerDummy> innerInnerDummies) { this.innerInnerDummies = innerInnerDummies; }
}
