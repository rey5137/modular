package com.rey.modular.common.repository;

import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.blazebit.persistence.querydsl.JPQLNextExpressions.literal;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ModelQueryBuilder<T extends EntityPathBase<?>, P, M extends EntityModel<P, M>> implements BaseRepository.QueryBuilder<M> {

    private final TableField<T, T> rootTableField;
    private final List<ColumnField<M, ?, ?>> columnFields;
    private final Supplier<M> modelSupplier;

    public ModelQueryBuilder(TableField<T, T> rootTableField, Collection<ColumnField<M, ?, ?>> columnFields, Supplier<M> modelSupplier) {
        this.rootTableField = rootTableField;
        this.columnFields = new ArrayList<>(columnFields);
        this.modelSupplier = modelSupplier;
    }

    @Override
    public BlazeJPAQuery<Tuple> buildQuery(BlazeJPAQueryFactory queryFactory, Boolean isCountQuery) {
        var query = queryFactory.from(rootTableField.getQEntity());
        Map<TableField, Boolean> tableFieldMap = columnFields.stream()
                .map(ColumnField::getTableField)
                .distinct()
                .collect(Collectors.toMap(t -> t, t -> t.equals(rootTableField)));
        if(columnFields.isEmpty() || isCountQuery) {
            query = query.select(literal(1));
        }
        else {
            Path[] selections = new Path[columnFields.size()];
            for (int i = 0; i < columnFields.size(); i++) {
                var tableField = columnFields.get(i).getTableField();
                if (!tableFieldMap.get(tableField)) {
                    query = tableField.join(query);
                    tableFieldMap.put(tableField, true);
                }
                selections[i] = columnFields.get(i).getPath();
            }
            query = query.select(selections);
        }

        List<Predicate> predicates = new ArrayList<>();
        populatePredicates(predicates);

        if(!predicates.isEmpty()) {
            BooleanBuilder builder = new BooleanBuilder();
            for(Predicate predicate : predicates){
                builder.and(predicate);
            }
            query = query.where(builder);
        }

        return decorateQuery(query);
    }

    @Override
    public M buildResult(Tuple tuple) {
        M result = modelSupplier.get();
        result.setPopulatedColumns(columnFields);
        for (ColumnField field : columnFields) {
            Object value = tuple.get(field.getPath());
            field.set(result, value);
        }
        return decorateResult(result, tuple);
    }

    protected void populatePredicates(List<Predicate> predicates) {}

    protected BlazeJPAQuery<Tuple> decorateQuery(BlazeJPAQuery query) {
        return query;
    }

    protected M decorateResult(M result, Tuple tuple) {
        return result;
    }

}
