package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.MovieDTO;
import com.devheon.springboot.example.dto.PageRequestDTO;
import com.devheon.springboot.example.dto.PageResultDTO;
import com.devheon.springboot.example.entity.Movie;
import com.devheon.springboot.example.entity.MovieImage;
import com.devheon.springboot.example.repository.MovieImageRepository;
import com.devheon.springboot.example.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieImageRepository imageRepository;

    @Transactional
    @Override
    public Long register(MovieDTO movieDTO) {
        Map<String, Object> entityMap = dtoToEntity(movieDTO);
        Movie movie = (Movie) entityMap.get("movie");
        List<MovieImage> movieImageList = (List<MovieImage>) entityMap.get("imgList");

        movieRepository.save(movie);
        movieImageList.forEach(movieImage -> {
            imageRepository.save(movieImage);
        });

        return movie.getMno();
    }

    @Override
    public PageResultDTO<MovieDTO, Object[]> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("mno").descending());
        Page<Object[]> result = movieRepository.getListPage(pageable);
        Function<Object[], MovieDTO> fn = (arr -> entitiesToDTO(
                (Movie) arr[0],
                (List<MovieImage>) (Arrays.asList((MovieImage) arr[1])),
                (Double) arr[2],
                (Long) arr[3]
        )
        );
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public MovieDTO getMovie(Long mno) {
        /**
         * MovieRepository에서 가져오는 Movie, MovieImageList, AVG, reviewCnt를 가공한다.
         */
        List<Object[]> result = movieRepository.getMovieWithAll(mno);

        /* Movie 엔터티는 가장 앞에 존재하며 모든 row가 동일하므로 첫번째를 가져온다. */
        Movie movie = (Movie) result.get(0)[0];

        /* 영화 이미지 개수만큼 MovieImage 객체 필요 */
        List<MovieImage> movieImageList = new ArrayList<>();

        result.forEach(arr -> {
            MovieImage movieImage = (MovieImage) arr[1];
            movieImageList.add(movieImage);
        });

        /* 평균 평점 - 모든 row가 동일한 값 */
        Double avg = (Double) result.get(0)[2];
        /* 리뷰 개수 - 모든 row가 동일한 값 */
        Long reviewCnt = (Long) result.get(0)[3];

        return entitiesToDTO(movie, movieImageList, avg, reviewCnt);
    }


}