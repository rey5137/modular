package com.rey.modular.common.repository;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
public abstract class ColumnField<M, T extends EntityPathBase<?>, C> {

    private final TableField<?, T> tableField;

    public abstract Path<C> getPath();

    public abstract void set(M model, C columnValue);

    public <M2> ColumnField<M2, T, C> withTable(TableField<?, T> newTableField, Function<M2, M> getModelFunc) {
        var originalColumnField = this;
        return new ColumnField<>(newTableField) {

            @Override
            public Path<C> getPath() {
                return originalColumnField.getPath();
            }

            @Override
            public void set(M2 model, C columnValue) {
                if(getModelFunc != null) {
                    originalColumnField.set(getModelFunc.apply(model), columnValue);
                }
            }
        };
    }
}
