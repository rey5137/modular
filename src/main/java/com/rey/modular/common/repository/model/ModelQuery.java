package com.rey.modular.common.repository.model;

import com.rey.modular.common.repository.BaseRepository.BaseQuery;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ModelQuery<T> extends BaseQuery<T> {

    private Map<Table, Object> entityCacheMap = new HashMap<>();

}
