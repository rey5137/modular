package com.rey.modular.common.repository;

import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.querydsl.core.JoinType;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.JPAQueryBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
public abstract class TableField<P extends EntityPathBase<?>, T extends EntityPathBase<?>> {

    private final TableField<?, P> parent;
    private final JoinType joinType;
    private final T qEntity;

    public BlazeJPAQuery join(BlazeJPAQuery query) {
        JPAQueryBase result = null;
        if(JoinType.FULLJOIN.equals(joinType)) {
            result = query.fullJoin(qEntity);
        }
        else if(JoinType.LEFTJOIN.equals(joinType)) {
            result = query.leftJoin(qEntity);
        }
        else if(JoinType.RIGHTJOIN.equals(joinType)) {
            result = query.rightJoin(qEntity);
        }
        else if(JoinType.INNERJOIN.equals(joinType)) {
            result = query.innerJoin(qEntity);
        }
        return (BlazeJPAQuery) result.on(buildJoinPredicate(parent.getQEntity(), qEntity));
    }

    protected abstract Predicate buildJoinPredicate(P parentQEntity, T qEntity);

    public <M, C> ColumnField<M, T, C> column(Function<T, Path<C>> pathFunc, BiConsumer<M, C> setFunc) {
        var tableField = this;
        return new ColumnField<>(tableField) {

            @Override
            public Path<C> getPath() {
                return pathFunc.apply(tableField.getQEntity());
            }

            @Override
            public void set(M model, C columnValue) {
                if(setFunc != null) {
                    setFunc.accept(model, columnValue);
                }
            }
        };
    }

    public TableField<P, T> withParent(TableField<?, P> newParent) {
        var originalTableField = this;
        return new TableField<>(newParent, joinType, qEntity) {

            @Override
            protected Predicate buildJoinPredicate(P parentQEntity, T qEntity) {
                return originalTableField.buildJoinPredicate(parentQEntity, qEntity);
            }

        };
    }

    public <T2 extends EntityPathBase<?>> TableField<T, T2> joinTable(JoinType joinType, T2 qEntity, BiFunction<T, T2, Predicate> predicateFunction) {
        return new TableField<>(this, joinType, qEntity) {
            @Override
            protected Predicate buildJoinPredicate(T parentQEntity, T2 qEntity) {
                if(predicateFunction != null) {
                    return predicateFunction.apply(parentQEntity, qEntity);
                }
                return null;
            }
        };
    }

    public static <T extends EntityPathBase<?>> TableField<T, T> root(T qEntity) {
        return new TableField<>(null, null, qEntity) {
            @Override
            protected Predicate buildJoinPredicate(T parentQEntity, T qEntity) {
                return null;
            }
        };
    }


}
