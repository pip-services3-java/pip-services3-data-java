package org.pipservices.data.persistence;

import java.util.List;

import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;
import org.pipservices.data.IGetter;
import org.pipservices.data.IWriter;

public interface IDummyPersistence extends IGetter<Dummy, String>, IWriter<Dummy, String> {
	
    DataPage<Dummy> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging) throws ApplicationException;
    Dummy getOneById(String correlationId, String dummyId) throws ApplicationException;
    List<Dummy> getListByIds(String correlationId, String[] ids) throws ApplicationException;
    Dummy create(String correlationId, Dummy dummy) throws ApplicationException;
    Dummy update(String correlationId, Dummy dummy) throws ApplicationException;
    Dummy deleteById(String correlationId, String dummyId) throws ApplicationException;
    void deleteByIds(String correlationId, String[] ids) throws ApplicationException;
}
