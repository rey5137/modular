package com.rey.modular.common.repository;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.querydsl.core.Tuple;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Slf4j
public class BaseRepositoryImpl <E, ID extends Serializable> extends SimpleJpaRepository<E, ID> implements BaseRepository<E, ID> {

    private final BlazeJPAQueryFactory jpaQueryFactory;

    public BaseRepositoryImpl(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        CriteriaBuilderFactory criteriaBuilderFactory = config.createCriteriaBuilderFactory(entityManager.getEntityManagerFactory());
        this.jpaQueryFactory = new BlazeJPAQueryFactory(entityManager, criteriaBuilderFactory);
    }

    @Override
    public <R> Page<R> findPage(QueryBuilder<R> queryBuilder, Integer pageSize, Integer pageNumber, PageQueryOption pageQueryOption) {
        BaseQuery<Tuple> baseQuery = queryBuilder.buildQuery(jpaQueryFactory, Tuple.class);
        Pageable pageable = Pageable.ofSize(pageSize)
                .withPage(pageNumber - 1);
        if(PageQueryOption.ONLY_COUNT.equals(pageQueryOption)) {
            Long total = count(queryBuilder);
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }
        var rows = baseQuery.getQuery().offset(pageable.getOffset())
                .limit(pageSize).stream()
                .map(tuple -> queryBuilder.buildResult(baseQuery, tuple))
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
            Long total = count(queryBuilder);
            return new PageImpl<>(rows, pageable, total);
        }
    }

    @Override
    public <R> Long count(QueryBuilder<R> queryBuilder) {
        BaseQuery<Long> baseQuery = queryBuilder.buildQuery(jpaQueryFactory, Long.class);
        return baseQuery.getQuery().fetchCount();
    }

    @Override
    public <R> List<R> findAll(QueryBuilder<R> queryBuilder) {
        BaseQuery<Tuple> baseQuery = queryBuilder.buildQuery(jpaQueryFactory, Tuple.class);
        return baseQuery.getQuery()
                .stream()
                .map(tuple -> queryBuilder.buildResult(baseQuery, tuple))
                .toList();
    }

}
