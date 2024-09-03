package com.rey.modular.common.repository;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
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
        BlazeJPAQuery<Tuple> query = queryBuilder.buildQuery(jpaQueryFactory, false);
        Pageable pageable = Pageable.ofSize(pageSize)
                .withPage(pageNumber - 1);
        if(PageQueryOption.ONLY_COUNT.equals(pageQueryOption)) {
            Long total = count(queryBuilder);
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }
        var rows = query.offset(pageable.getOffset())
                .limit(pageSize).stream()
                .map(queryBuilder::buildResult)
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
        BlazeJPAQuery<Tuple> query = queryBuilder.buildQuery(jpaQueryFactory, true);
        return query.fetchCount();
    }

}
