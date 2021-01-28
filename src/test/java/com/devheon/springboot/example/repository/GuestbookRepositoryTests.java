package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Guestbook;
import com.devheon.springboot.example.entity.QGuestbook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class GuestbookRepositoryTests {

    @Autowired
    private GuestbookRepository guestbookRepository;

    //    @Test
    public void insertDummies() {
        IntStream.rangeClosed(1, 300).forEach(i -> {
            Guestbook guestbook = Guestbook.builder()
                    .title("Title..."+i)
                    .content("Content..."+i)
                    .writer("user"+(i%10))
                    .build();
            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    //    @Test
    public void updateTest() {
        /**
         * 수정시간 테스트
         * BaseEntity의 modDate는 최종 수정시간이 반영되므로
         * save() 메소드 호출 시 동작한다.
         */
        Optional<Guestbook> result = guestbookRepository.findById(300L);

        if(result.isPresent()) {
            Guestbook guestbook = result.get();

            guestbook.changeTitle("Changed Title...");
            guestbook.changeContent("Changed Content...");

            guestbookRepository.save(guestbook);
        }
    }

    /**
     * Querydsl 테스트
     * - 제목 / 내용 / 작성자 와 같이 단 하나의 항목으로 검색하는 경우
     * - 제목+내용 / 내용+작성자 / 제목+작성자 와 같이 2개의 항목으로 검색하는 경우
     * - 제목 + 내용 + 작성자와 같이 3개의 항목으로 검색하는 경우
     *
     * 만일 Guestbook 엔티티 클래스에 많은 멤버 변수들이 있다면 이러한 조합은 많아질 것이다.
     * 이런 상황을 대비하여 Querydsl을 이용한다.
     * 1. BooleanBuilder 생성
     * 2. 조건에 맞는 구문은 Querydsl에서 사용하는 Predicate 타입의 함수 생성
     * 3. BooleanBuilder에 작성된 Predicate를 추가하고 실행
     */

//    @Test
    public void testQuery1() {
        /**
         * 단일항목 검색 테스트
         */

        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        /**
         * 1. 가장 먼저 동적으로 처리하기 위해 Q도메인 클래스를 얻어온다.
         * 이를 이용하면 엔티티 클래스에 선언된 title, content같은 필드를 변수로 활용할 수 있다.
         */
        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = "1";

        /**
         * 2. BooleanBuilder는 where문에 들어가는 조건을 넣어주는 컨테이너
         */
        BooleanBuilder builder = new BooleanBuilder();

        /**
         * 3. 원하는 조건은 필드 값과 같이 결합하여 생성
         *    BooleanBuilder 안에 들어가는 값은 com.querydsl.core.types.Predicate 타입이어야 한다.
         */
        BooleanExpression expression = qGuestbook.title.contains(keyword);

        /**
         * 4. 만들어진 조건은 where문에 and나 or 키워드와 결합
         */
        builder.and(expression);

        /**
         * 5. BooleanBuilder는 GuestRepository에 추가된 QuerydslPredicateExcutor 인터페이스의 findAll() 사용 가능
         */
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }

    @Test
    public void testQuery2() {
        /**
         * 다중항목 검색 테스
         */

        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());
        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression exTitle = qGuestbook.title.contains(keyword);
        BooleanExpression exContent = qGuestbook.content.contains(keyword);

        BooleanExpression exAll = exTitle.and(exContent);

        builder.and(exAll);
        builder.and(qGuestbook.gno.gt(0L));

        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });

    }
}