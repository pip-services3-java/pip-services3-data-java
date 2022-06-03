package org.pipservices3.data.persistence;

import java.util.Date;
import java.util.List;

import org.pipservices3.commons.data.*;

import com.fasterxml.jackson.annotation.*;

public class Dummy implements IStringIdentifiable {
    public Dummy() {
    }

    public Dummy(String _id, String _key, String _content, Date _createTime, InnerDummy _innerDummy,
                 DummyType dummyType, List<InnerDummy> _innerDummies) {
        super();
        this._id = _id;
        this._key = _key;
        this._content = _content;
        this._createTime = _createTime;
        this._innerDummy = _innerDummy;
        this.dummyType = dummyType;
        this._innerDummies = _innerDummies;
    }


    @JsonProperty("_id")
    private String _id;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    @Override
    public String withGeneratedId() {
        _id = IdGenerator.nextLong();
        return _id;
    }

    @JsonProperty("key")
    private String _key;

    public String getKey() {
        return _key;
    }

    public void setKey(String key) {
        this._key = key;
    }

    @JsonProperty("content")
    private String _content;

    public String getContent() {
        return _content;
    }

    public void setContent(String content) {
        this._content = content;
    }

    @JsonProperty("create_time")
    private Date _createTime;

    public Date getCreateTime() {
        return _createTime;
    }

    public void setCreateTime(Date createTime) {
        this._createTime = createTime;
    }


//	@BsonProperty("create_time")
//	private ZonedDateTime _createTime;
//	public ZonedDateTime getCreateTime() { return _createTime; }
//	public void setCreateTime(ZonedDateTime createTime) { this._createTime = createTime; }

    @JsonProperty("inner_dummy")
    private InnerDummy _innerDummy;

    public InnerDummy getInnerDummy() {
        return _innerDummy;
    }

    public void setInnerDummy(InnerDummy innerDummy) {
        this._innerDummy = innerDummy;
    }

    @JsonProperty("dummy_type")
    private DummyType dummyType;

    public DummyType getDummyType() {
        return dummyType;
    }

    public void setDummyType(DummyType dummyType) {
        this.dummyType = dummyType;
    }

    @JsonProperty("inner_dummies")
    private List<InnerDummy> _innerDummies;

    public List<InnerDummy> getInnerDummies() {
        return _innerDummies;
    }

    public void setInnerDummies(List<InnerDummy> innerDummies) {
        this._innerDummies = innerDummies;
    }

}

