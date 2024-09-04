package com.rey.modular.common.repository.model;

import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.rey.modular.common.repository.BaseRepository;
import com.rey.modular.common.repository.BaseRepository.BaseQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.blazebit.persistence.querydsl.JPQLNextExpressions.literal;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ModelQueryBuilder<T extends EntityPathBase<?>, P, M extends EntityModel<P, M>> implements BaseRepository.QueryBuilder<M> {

    private final Table<T, T> rootTable;
    private final List<Column<M, ?, ?>> columns;
    private final Supplier<M> modelSupplier;

    public ModelQueryBuilder(Table<T, T> rootTable, Collection<Column<M, ?, ?>> columns, Supplier<M> modelSupplier) {
        this.rootTable = rootTable;
        this.columns = new ArrayList<>(columns);
        this.modelSupplier = modelSupplier;
    }

    @Override
    public <R> BaseQuery<R> buildQuery(BlazeJPAQueryFactory queryFactory, Class<R> resultClass) {
        var modelQuery = new ModelQuery();
        modelQuery.setQuery(queryFactory.from(rootTable.getQEntity(modelQuery)));

        if(columns.isEmpty() || Long.class.equals(resultClass)) {
            modelQuery.getQuery().select(literal(1));
        }
        else {
            Path[] selections = new Path[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                selections[i] = columns.get(i).getPath(modelQuery);
            }
            modelQuery.getQuery().select(selections);
        }

        List<Predicate> predicates = new ArrayList<>();
        populatePredicates(predicates, modelQuery);

        if(!predicates.isEmpty()) {
            BooleanBuilder builder = new BooleanBuilder();
            for(Predicate predicate : predicates){
                builder.and(predicate);
            }
            modelQuery.getQuery().where(builder);
        }

        decorateQuery(modelQuery);
        return modelQuery;
    }

    @Override
    public M buildResult(BaseQuery baseQuery, Tuple tuple) {
        var modelQuery = (ModelQuery) baseQuery;
        M result = modelSupplier.get();
        result.setPopulatedColumns(columns);
        for (Column field : columns) {
            Object value = tuple.get(field.getPath(modelQuery));
            field.set(result, value);
        }
        return decorateResult(result, tuple);
    }

    protected void populatePredicates(List<Predicate> predicates, ModelQuery modelQuery) {}

    protected void decorateQuery(ModelQuery modelQuery) {}

    protected M decorateResult(M result, Tuple tuple) {
        return result;
    }

}
