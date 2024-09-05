package com.rey.modular.common.repository.model;

import com.querydsl.core.types.dsl.EntityPathBase;

public record OrderColumn<M, T extends EntityPathBase<?>, C>(
        Column<M, T, C> column,
        boolean ascend,
        boolean nullFirst
) {
}
