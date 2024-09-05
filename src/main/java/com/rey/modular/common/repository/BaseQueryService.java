package com.rey.modular.common.repository;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.querydsl.core.Tuple;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class BaseQueryService {

    private final BlazeJPAQueryFactory jpaQueryFactory;

    public BaseQueryService(EntityManagerFactory entityManagerFactory) {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        CriteriaBuilderFactory criteriaBuilderFactory = config.createCriteriaBuilderFactory(entityManagerFactory);
        this.jpaQueryFactory = new BlazeJPAQueryFactory(entityManagerFactory.createEntityManager(), criteriaBuilderFactory);
    }

    public <T, R> Page<R> findPage(PagingQueryBuilder<T, R> queryBuilder, Integer pageSize, Integer pageNumber, PageQueryOption pageQueryOption) {
        BaseQuery<T> baseQuery = queryBuilder.buildQuery(jpaQueryFactory);
        Pageable pageable = Pageable.ofSize(pageSize)
                .withPage(pageNumber - 1);
        if(PageQueryOption.ONLY_COUNT.equals(pageQueryOption)) {
            long total = queryBuilder.buildCountQuery(jpaQueryFactory).getQuery().fetchCount();
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }
        var rows = baseQuery.getQuery().offset(pageable.getOffset())
                .limit(pageSize).stream()
                .map(row -> queryBuilder.buildResult(baseQuery, row))
                .toList();
        if(PageQueryOption.NO_COUNT.equals(pageQueryOption)) {
            return new PageImpl<>(rows) {
                @Override
                public int getTotalPages() {
                    return -1;
                }

                @Override
                public long getTotalElements() {
                    return -1;
                }
            };
        }
        if(rows.isEmpty() && pageable.getOffset() == 0) {
            return new PageImpl<>(rows, pageable, 0L);
        }
        else if(!rows.isEmpty() && rows.size() < pageable.getPageSize()) {
            long total = pageable.getOffset() + rows.size();
            return new PageImpl<>(rows, pageable, total);
        }
        else {
            long total = queryBuilder.buildCountQuery(jpaQueryFactory).getQuery().fetchCount();
            return new PageImpl<>(rows, pageable, total);
        }
    }

    public <T, R> Long count(QueryBuilder<T, R> queryBuilder) {
        BaseQuery<T> baseQuery = queryBuilder.buildQuery(jpaQueryFactory);
        return baseQuery.getQuery().fetchCount();
    }

    public <T, R> List<R> findAll(QueryBuilder<T, R> queryBuilder) {
        BaseQuery<T> baseQuery = queryBuilder.buildQuery(jpaQueryFactory);
        return baseQuery.getQuery()
                .stream()
                .map(row -> queryBuilder.buildResult(baseQuery, row))
                .toList();
    }

    public enum PageQueryOption {

        PAGE("page"),
        NO_COUNT("no_count"),
        ONLY_COUNT("only_count");

        private String name;

        PageQueryOption(String name) {
            this.name = name;
        }

        public static PageQueryOption of(String name) {
            return Stream.of(PageQueryOption.values())
                    .filter(value -> value.name.equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }
    }

    public interface QueryBuilder<T, R> {

        BaseQuery<T> buildQuery(BlazeJPAQueryFactory queryFactory);

        R buildResult(BaseQuery<T> query, T row);
    }

    public interface PagingQueryBuilder<T, R> extends QueryBuilder<T, R> {

        BaseQuery<Long> buildCountQuery(BlazeJPAQueryFactory queryFactory);

    }

    public static class SimpleQueryBuilder<R> implements QueryBuilder<R, R> {

        Function<BlazeJPAQueryFactory, BaseQuery<R>> queryFunction;
        public SimpleQueryBuilder(Function<BlazeJPAQueryFactory, BaseQuery<R>> queryFunction) {
            this.queryFunction = queryFunction;
        }

        @Override
        public BaseQuery<R> buildQuery(BlazeJPAQueryFactory queryFactory) {
            return queryFunction.apply(queryFactory);
        }

        @Override
        public R buildResult(BaseQuery<R> query, R row) {
            return row;
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BaseQuery<T> {
        private BlazeJPAQuery<T> query;
        private Class<T> resultClass;
    }

}
