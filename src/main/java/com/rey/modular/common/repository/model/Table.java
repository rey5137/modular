package com.rey.modular.common.repository.model;

import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.querydsl.core.JoinType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "rawtypes"})
@Getter
@Setter
@AllArgsConstructor
public abstract class Table<P extends EntityPathBase<?>, T extends EntityPathBase<?>> {

    private final Table<?, P> parent;

    public T getQEntity(ModelQuery modelQuery) {
        if(modelQuery.getEntityCacheMap().containsKey(this)) {
            return (T) modelQuery.getEntityCacheMap().get(this);
        }
        T entity = buildQEntity(parent == null ? null : parent.getQEntity(modelQuery), modelQuery);
        modelQuery.getEntityCacheMap().put(this, entity);
        return entity;
    }

    protected abstract T buildQEntity(P parentEntity, ModelQuery modelQuery);

    public <M> StringColumn<M, T> stringCol(Function<T, StringPath> pathFunc, BiConsumer<M, String> setFunc) {
        return new StringColumn<>(this) {
            @Override
            protected StringPath buildPath(T entity) {
                return pathFunc.apply(entity);
            }

            @Override
            public void set(M model, String columnValue) {
                if(setFunc != null) {
                    setFunc.accept(model, columnValue);
                }
            }
        };
    }

    public <M> IntegerColumn<M, T> integerCol(Function<T, NumberPath<Integer>> pathFunc, BiConsumer<M, Integer> setFunc) {
        return new IntegerColumn<>(this) {
            @Override
            protected NumberPath<Integer> buildPath(T entity) {
                return pathFunc.apply(entity);
            }

            @Override
            public void set(M model, Integer columnValue) {
                if(setFunc != null) {
                    setFunc.accept(model, columnValue);
                }
            }
        };
    }

    public Table<P, T> withParent(Table<?, P> newParent) {
        var originalTableField = this;
        return new Table<>(newParent) {

            @Override
            protected T buildQEntity(P parentEntity, ModelQuery modelQuery) {
                return originalTableField.buildQEntity(parentEntity, modelQuery);
            }

        };
    }

    public <T2 extends EntityPathBase<?>> Table<T, T2> leftJoin(Supplier<T2> entitySupplier, BiFunction<T, T2, Predicate> joinPredicateFunc) {
        return join(JoinType.LEFTJOIN, entitySupplier, joinPredicateFunc);
    }

    public <T2 extends EntityPathBase<?>> Table<T, T2> rightJoin(Supplier<T2> entitySupplier, BiFunction<T, T2, Predicate> joinPredicateFunc) {
        return join(JoinType.RIGHTJOIN, entitySupplier, joinPredicateFunc);
    }

    public <T2 extends EntityPathBase<?>> Table<T, T2> innerJoin(Supplier<T2> entitySupplier, BiFunction<T, T2, Predicate> joinPredicateFunc) {
        return join(JoinType.INNERJOIN, entitySupplier, joinPredicateFunc);
    }

    public <T2 extends EntityPathBase<?>> Table<T, T2> fullJoin(Supplier<T2> entitySupplier, BiFunction<T, T2, Predicate> joinPredicateFunc) {
        return join(JoinType.FULLJOIN, entitySupplier, joinPredicateFunc);
    }

    private  <T2 extends EntityPathBase<?>> Table<T, T2> join(JoinType joinType, Supplier<T2> entitySupplier, BiFunction<T, T2, Predicate> joinPredicateFunc) {
        return new Table<>(this) {

            @Override
            protected T2 buildQEntity(T parentEntity, ModelQuery modelQuery) {
                T2 entity = entitySupplier.get();
                BlazeJPAQuery newQuery = modelQuery.getQuery();
                if(JoinType.FULLJOIN.equals(joinType)) {
                    newQuery = (BlazeJPAQuery) newQuery.fullJoin(entity);
                }
                else if(JoinType.LEFTJOIN.equals(joinType)) {
                    newQuery = (BlazeJPAQuery) newQuery.leftJoin(entity);
                }
                else if(JoinType.RIGHTJOIN.equals(joinType)) {
                    newQuery = (BlazeJPAQuery) newQuery.rightJoin(entity);
                }
                else if(JoinType.INNERJOIN.equals(joinType)) {
                    newQuery = (BlazeJPAQuery) newQuery.innerJoin(entity);
                }
                modelQuery.setQuery((BlazeJPAQuery) newQuery.on(joinPredicateFunc.apply(parentEntity, entity)));
                return entity;
            }

        };
    }

    public static <T extends EntityPathBase<?>> Table<T, T> root(Supplier<T> entitySupplier) {
        return new Table<>(null) {

            @Override
            protected T buildQEntity(T parentEntity, ModelQuery modelQuery) {
                return entitySupplier.get();
            }

        };
    }


}
