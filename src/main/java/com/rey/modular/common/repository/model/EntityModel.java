package com.rey.modular.common.repository.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @param <P> Primary key class
 * @param <M> Model class
 */
public abstract class EntityModel<P, M> {

    @Getter
    @Setter
    private List<Column<M, ?, ?>> populatedColumns;

    public abstract P getPrimaryKey();

}
