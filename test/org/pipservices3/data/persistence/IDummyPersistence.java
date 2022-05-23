package org.pipservices3.data.persistence;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.pipservices3.commons.data.*;
import org.pipservices3.commons.errors.*;
import org.pipservices3.data.IGetter;
import org.pipservices3.data.IPartialUpdater;
import org.pipservices3.data.IWriter;

public interface IDummyPersistence extends IGetter<Dummy, String>, IWriter<Dummy, String>, IPartialUpdater<Dummy, String> {
	
    DataPage<Dummy> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging) throws ApplicationException;
    int getCountByFilter(String correlationId, FilterParams filter);
    DataPage<Dummy> getSortedPage(String correlationId, Comparator<Dummy> sort);
    List<Dummy> getSortedList(String correlationId, Comparator<Dummy> sort) throws ApplicationException;
    Dummy getOneById(String correlationId, String dummyId) throws ApplicationException;
    List<Dummy> getListByIds(String correlationId, String[] ids) throws ApplicationException;
    List<Dummy> getListByIds(String correlationId, List<String> ids) throws ApplicationException;
    Dummy create(String correlationId, Dummy dummy) throws ApplicationException;
    Dummy update(String correlationId, Dummy dummy) throws ApplicationException;

    Dummy deleteById(String correlationId, String dummyId) throws ApplicationException;
    void deleteByIds(String correlationId, String[] ids) throws ApplicationException;
}
