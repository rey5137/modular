package com.rey.modular.common.repository.model;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Column<M, T extends EntityPathBase<?>, C> {

    private final Table<?, T> table;

    public Path<C> getPath(ModelQuery modelQuery) {
        return buildPath(table.getQEntity(modelQuery));
    }

    protected abstract Path<C> buildPath(T entity);

    public abstract void set(M model, C columnValue);

}
