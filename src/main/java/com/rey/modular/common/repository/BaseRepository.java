package com.rey.modular.common.repository;

import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.stream.Stream;

@NoRepositoryBean
public interface BaseRepository<E, ID extends Serializable> extends JpaRepository<E, ID> {

    <R> Page<R> findPage(QueryBuilder<R> queryBuilder, Integer pageSize, Integer pageNumber, PageQueryOption pageQueryOption);

    <R> Long count(QueryBuilder<R> queryBuilder);

    enum PageQueryOption {

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

    interface QueryBuilder<R> {

        BlazeJPAQuery<Tuple> buildQuery(BlazeJPAQueryFactory queryFactory, Boolean isCountQuery);

        R buildResult(Tuple tuple);
    }

}
