package com.rey.modular.common.repository.model;

import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.rey.modular.common.repository.BaseQueryService.BaseQuery;
import com.rey.modular.common.repository.BaseQueryService.DeferredPagingQueryBuilder;
import com.rey.modular.common.repository.BaseQueryService.QueryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.blazebit.persistence.querydsl.JPQLNextExpressions.literal;
import static com.querydsl.core.types.ExpressionUtils.eq;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ModelQueryBuilder<T extends EntityPathBase<?>, P, M extends EntityModel<P, M>> implements QueryBuilder<Tuple, M>, DeferredPagingQueryBuilder<Tuple, M> {

    private final Table<T, T> rootTable;
    private final List<Column<M, ?, ?>> columns;
    private final Supplier<M> modelSupplier;
    private final List<OrderColumn<M, ?, ?>> orderColumns;
    private final List<Column<M, ?, ?>> primaryKeyColumns;
    private final Function<Long, Boolean> shouldQueryPrimaryKeyFunc;

    public ModelQueryBuilder(Table<T, T> rootTable, Collection<Column<M, ?, ?>> columns, Supplier<M> modelSupplier) {
        this(rootTable, List.of(), columns, List.of(), modelSupplier, offset -> false);
    }

    public ModelQueryBuilder(Table<T, T> rootTable,
                             Collection<Column<M, ?, ?>> primaryKeyColumns,
                             Collection<Column<M, ?, ?>> columns,
                             Collection<OrderColumn<M, ?, ?>> orderColumns,
                             Supplier<M> modelSupplier,
                             Function<Long, Boolean> shouldQueryPrimaryKeyFunc) {
        this.rootTable = rootTable;
        this.primaryKeyColumns = new ArrayList<>(primaryKeyColumns);
        this.columns = new ArrayList<>(columns);
        this.orderColumns = new ArrayList<>(orderColumns);
        this.modelSupplier = modelSupplier;
        this.shouldQueryPrimaryKeyFunc = shouldQueryPrimaryKeyFunc;
    }

    @Override
    public boolean shouldQueryPrimaryKeyFirst(Long offset) {
        return !primaryKeyColumns.isEmpty()
                && !isSameCollection(columns, primaryKeyColumns)
                && shouldQueryPrimaryKeyFunc.apply(offset);
    }

    private boolean isSameCollection(Collection c1, Collection c2) {
        return c1.size() == c2.size() && c1.containsAll(c2);
    }

    @Override
    public BaseQuery<Tuple> buildPrimaryKeyQuery(BlazeJPAQueryFactory queryFactory) {
        var modelQuery = new ModelQuery();
        modelQuery.setQuery(queryFactory.from(rootTable.getQEntity(modelQuery)));
        modelQuery.setResultClass(Tuple.class);
        modelQuery.getQuery().select(buildSelections(modelQuery, primaryKeyColumns))
                .where(buildPredicates(modelQuery))
                .orderBy(buildOrders(modelQuery));
        decorateQuery(modelQuery);
        return modelQuery;
    }

    @Override
    public List<Comparable> buildPrimaryKey(BaseQuery<Tuple> query, Tuple row) {
        var modelQuery = (ModelQuery) query;
        List<Comparable> primaryKey = new ArrayList<>();
        for (Column field : primaryKeyColumns) {
            primaryKey.add((Comparable) row.get(field.getPath(modelQuery)));
        }
        return primaryKey;
    }

    @Override
    public BaseQuery<Tuple> buildQuery(BlazeJPAQueryFactory queryFactory, List<List<Comparable>> primaryKeyList) {
        var modelQuery = new ModelQuery();
        modelQuery.setQuery(queryFactory.from(rootTable.getQEntity(modelQuery)));
        modelQuery.setResultClass(Tuple.class);
        Expression[] expressions = buildSelections(modelQuery, primaryKeyColumns);
        BooleanBuilder builder = new BooleanBuilder();
        for (List<Comparable> primaryKey : primaryKeyList) {
            Predicate[] predicates = new Predicate[primaryKey.size()];
            for (int i = 0; i < expressions.length; i++) {
                predicates[i] = eq(expressions[i], literal(primaryKey.get(i)));
            }
            builder.or(ExpressionUtils.allOf(predicates));
        }
        modelQuery.getQuery().select(buildSelections(modelQuery, columns))
                .where(builder)
                .orderBy(buildOrders(modelQuery));
        decorateQuery(modelQuery);
        return modelQuery;
    }

    @Override
    public BaseQuery<Tuple> buildQuery(BlazeJPAQueryFactory queryFactory) {
        var modelQuery = new ModelQuery();
        modelQuery.setQuery(queryFactory.from(rootTable.getQEntity(modelQuery)));
        modelQuery.setResultClass(Tuple.class);
        modelQuery.getQuery().select(buildSelections(modelQuery, columns))
                .where(buildPredicates(modelQuery))
                .orderBy(buildOrders(modelQuery));
        decorateQuery(modelQuery);
        return modelQuery;
    }

    @Override
    public BaseQuery<Long> buildCountQuery(BlazeJPAQueryFactory queryFactory) {
        var modelQuery = new ModelQuery();
        modelQuery.setQuery(queryFactory.from(rootTable.getQEntity(modelQuery)));
        modelQuery.setResultClass(Long.class);
        modelQuery.getQuery().select(buildSelections(modelQuery, List.of()))
                .where(buildPredicates(modelQuery));
        decorateQuery(modelQuery);
        return modelQuery;
    }

    @Override
    public M buildResult(BaseQuery<Tuple> baseQuery, Tuple row) {
        var modelQuery = (ModelQuery) baseQuery;
        M result = modelSupplier.get();
        result.setPopulatedColumns(columns);
        for (Column field : columns) {
            Object value = row.get(field.getPath(modelQuery));
            field.set(result, value);
        }
        return decorateResult(result, row);
    }

    private Expression[] buildSelections(ModelQuery modelQuery, List<Column<M, ?, ?>> columns) {
        if (columns.isEmpty()) {
            return new Expression[]{literal(1)};
        } else {
            Expression[] selections = new Expression[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                selections[i] = columns.get(i).getPath(modelQuery);
            }
            return selections;
        }
    }

    private Predicate buildPredicates(ModelQuery modelQuery) {
        List<Predicate> predicates = new ArrayList<>();
        populatePredicates(predicates, modelQuery);
        BooleanBuilder builder = new BooleanBuilder();
        for (Predicate predicate : predicates) {
            builder.and(predicate);
        }
        return builder;
    }

    private OrderSpecifier[] buildOrders(ModelQuery modelQuery) {
        OrderSpecifier[] orders = new OrderSpecifier[orderColumns.size()];
        for (int i = 0; i < orderColumns.size(); i++) {
            var orderColumn = orderColumns.get(i);
            orders[i] = new OrderSpecifier(
                    orderColumn.ascend() ? Order.ASC : Order.DESC,
                    orderColumn.column().getPath(modelQuery)
            );
        }
        return orders;
    }

    protected void populatePredicates(List<Predicate> predicates, ModelQuery modelQuery) {
    }

    protected void decorateQuery(ModelQuery modelQuery) {
    }

    protected M decorateResult(M result, Tuple tuple) {
        return result;
    }

}
