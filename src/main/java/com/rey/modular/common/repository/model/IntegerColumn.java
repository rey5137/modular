package com.rey.modular.common.repository.model;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;

import java.util.function.Function;

public abstract class IntegerColumn<M, T extends EntityPathBase<?>> extends Column<M, T, Integer> {

    public IntegerColumn(Table<?, T> table) {
        super(table);
    }

    @Override
    public NumberPath<Integer> getPath(ModelQuery modelQuery) {
        return (NumberPath<Integer>) super.getPath(modelQuery);
    }

    @Override
    protected abstract NumberPath<Integer> buildPath(T entity);

    public <M2> IntegerColumn<M2, T> withTable(Table<?, T> newTable, Function<M2, M> getModelFunc) {
        var originalColumnField = this;
        return new IntegerColumn<>(newTable) {

            @Override
            protected NumberPath<Integer> buildPath(T entity) {
                return originalColumnField.buildPath(entity);
            }

            @Override
            public void set(M2 model, Integer columnValue) {
                if(getModelFunc != null) {
                    originalColumnField.set(getModelFunc.apply(model), columnValue);
                }
            }
        };
    }

}
