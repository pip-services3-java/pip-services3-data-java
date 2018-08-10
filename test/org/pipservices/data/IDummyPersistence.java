package org.pipservices.data;

import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

public interface IDummyPersistence extends IGetter<Dummy, String>, IWriter<Dummy, String> {
    DataPage<Dummy> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging) throws ApplicationException;
    Dummy getOneById(String correlationId, String dummyId) throws ApplicationException;
    Dummy create(String correlationId, Dummy dummy) throws ApplicationException;
    Dummy update(String correlationId, Dummy dummy) throws ApplicationException;
    Dummy deleteById(String correlationId, String dummyId) throws ApplicationException;
}
