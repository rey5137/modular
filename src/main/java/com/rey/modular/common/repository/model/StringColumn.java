package com.rey.modular.common.repository.model;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import java.util.function.Function;

public abstract class StringColumn<M, T extends EntityPathBase<?>> extends Column<M, T, String> {

    public StringColumn(Table<?, T> table) {
        super(table);
    }

    @Override
    public StringPath getPath(ModelQuery modelQuery) {
        return (StringPath) super.getPath(modelQuery);
    }

    @Override
    protected abstract StringPath buildPath(T entity);

    public <M2> StringColumn<M2, T> withTable(Table<?, T> newTable, Function<M2, M> getModelFunc) {
        var originalColumnField = this;
        return new StringColumn<>(newTable) {

            @Override
            protected StringPath buildPath(T entity) {
                return originalColumnField.buildPath(entity);
            }

            @Override
            public void set(M2 model, String columnValue) {
                if(getModelFunc != null) {
                    originalColumnField.set(getModelFunc.apply(model), columnValue);
                }
            }
        };
    }
}
