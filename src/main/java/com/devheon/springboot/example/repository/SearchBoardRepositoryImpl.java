package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Board;
import com.devheon.springboot.example.entity.QBoard;
import com.devheon.springboot.example.entity.QMember;
import com.devheon.springboot.example.entity.QReply;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport implements SearchBoardRepository {
    /**
     * Spring Data JPA의 Repository를 확장하는 법
     * - 쿼리 메소드나 @Query 등으로 처리할 수 없는 기능은 별도 인터페이스로 설계
     * - 별도 인터페이스에 대한 구현 클래스 작성.
     *   이 때 QuerydslRepositorySupport라는 클래스를 부모 클래스로 사용
     * - 구현 클래스에 인터페이스 기능을 Q도메인 클래스와 JPQLQuery를 이용하여 구현
     *
     * 구현 클래스의 이름은 반드시 '인터페이스이름+Impl'로 작성
     *
     * 이를 BoardRepository에서 상속한다.
     */

    public SearchBoardRepositoryImpl() {
        /**
         * 도메인 클래스를 지정해야함
         */
        super(Board.class);
    }

    @Override
    public Board search1() {
        log.info("search1...");

        QBoard board = QBoard.board;

        /**
         * Reply와 join
         */
        QReply reply = QReply.reply;
        QMember member = QMember.member;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.leftJoin(member).on(board.writer.eq(member));
        jpqlQuery.leftJoin(reply).on(reply.board.eq(board));

        JPQLQuery<Tuple> tuple = jpqlQuery.select(board, member.email, reply.count());
        tuple.groupBy(board);

        log.info("------------------------");
        log.info(tuple);
        log.info("------------------------");

        List<Tuple> result = tuple.fetch();

        log.info(result);

        return null;
    }

    @Override
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {
        log.info("searchPage...");

        /**
         * 검색 조건의 처리 BooleanExpression
         * - type : 제목(t), 내용(c), 작성자(w)
         */

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        QMember member = QMember.member;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.leftJoin(member).on(board.writer.eq(member));
        jpqlQuery.leftJoin(reply).on(reply.board.eq(board));

        /**
         * SELECT b, w, count(r) FROM Board b
         *      LEFT JOIN b.writer w LEFT JOIN Reply r ON r.board = b
         */
        JPQLQuery<Tuple> tuple = jpqlQuery.select(board, member, reply.count());
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = board.bno.gt(0L);
        booleanBuilder.and(expression);

        if(type != null) {
            String[] typeArr = type.split("");
            BooleanBuilder conditionBuilder = new BooleanBuilder();
            for(String t : typeArr) {
                switch (t) {
                    case "t":
                        conditionBuilder.or(board.title.contains(keyword));
                        break;
                    case "w":
                        conditionBuilder.or(member.email.contains(keyword));
                        break;
                    case "c":
                        conditionBuilder.or(board.content.contains(keyword));
                        break;
                }
            }

            booleanBuilder.and(conditionBuilder);
        }

        tuple.where(booleanBuilder);
        /**
         * Pageable의 Sort 객체는 JPQLQuery의 orderBy() 파라미터로 전달해야 하지만
         * JPQL에서는 Sort 객체를 지원하지 않으므로 orderBy()의 경우 OrderSpecifier<T extends Comparable>을 파라미터로 처리
         * Sort는 내부적으로 여러 Sort객체를 연결할 수 있으므로 forEach()를 이용하여 처리
         * OrderSpecifier에는 정렬이 필요하므로 Sort 객체의 정렬 관련 정보를 Order 타입으로 처리하고,
         * Sort 객체의 속성(bno, title)등은 PathBuilder를 이용하여 처리.
         * PathBuilder를 생성할 때 문자열로 된 이름은 JPQLQuery를 생성할 때 이용하는 변수명과 동일해야 한다.
         */
        /* order by */
        Sort sort = pageable.getSort();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();
            System.out.println("prop - " + prop);
            PathBuilder orderByExpression = new PathBuilder(Board.class, "board");
            tuple.orderBy(new OrderSpecifier<>(direction, orderByExpression.get(prop)));
        });

        tuple.groupBy(board);

        /* page 처리 */
        tuple.offset(pageable.getOffset());
        tuple.limit(pageable.getPageSize());

        List<Tuple> result = tuple.fetch();

        log.info(result);

        long count = tuple.fetchCount();

        log.info("COUNT : " + count);

        return new PageImpl<Object[]>(
                result.stream().map(t -> t.toArray()).collect(Collectors.toList()), pageable, count
        );

    }
}
