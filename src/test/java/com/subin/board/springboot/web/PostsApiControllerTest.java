package com.subin.board.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subin.board.springboot.domain.posts.Posts;
import com.subin.board.springboot.domain.posts.PostsRepository;
import com.subin.board.springboot.web.dto.PostsResponseDto;
import com.subin.board.springboot.web.dto.PostsSaveRequestDto;
import com.subin.board.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
- @SpringBootTest
    - 기존의 @WebMvcTest의 경우 JPA 기능이 작동하지 않음 그렇기 때문에 JPA를 지원하는 @SpringBootTest와 TestRestTemplate를 사용
    - 랜덤 포트 실행법
        - 호스트가 사용하지 않는 랜덤 포트를 사용하겠다는 의미
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    /*
    - TestRestTemplate
        - TestRestTemplate 이라는 클래스를 사용해서 기존 RestTemplate 클래스의 사용방식처럼 테스트를 할 수 있다.
        - restTemplate.postForEntity(url, 메서드의 매개변수, 메서드의 리턴타입)
            - 생성을 진행할때 사용하는 메서드
     */
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_조회한다() throws  Exception{

        // 테이블 생성 및 값 넣어주는 용
        Posts_등록된다();

        String url = "http://localhost:" + port + "/api/v1/posts/1";

        /*
        - getForObject() 사용
        PostsResponseDto responseDto = restTemplate.getForObject(url, PostsResponseDto.class, 1);
        assertThat(responseDto.getId()).isEqualTo(1);
        */

        // exchange() 사용
        // exchange(url, 메서드 종류(get,post,put,delete), 매개변수에 엔티티가 있는지..?, 리턴 타입)
        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, PostsResponseDto.class);
        
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // JSON 변환(ResponseEntity의 내용물을 보기 위해 사용)
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(responseEntity);

        System.out.println(">>>>>> " + jsonString);
    }

    @Test
    public void Posts_등록된다() throws  Exception{

        // given
        String title = "title";
        String content = "content";

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        // when
        // restTemplate.postForEntity(url, 메서드의 매개변수, 메서드의 리턴타입);
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);

    }

    @Test
    public void Posts_수정된다() throws Exception{

        // 테이블 생성 및 게시글 한개 생성
        Posts savePosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        // update
        Long updateId = savePosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";
        String expectedAuthor = "author2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .author(expectedAuthor)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        // HttpEntity<객체타입> requestEntity = new HttpEntity<>(객체);
        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        // 게시글 수정
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
        assertThat(all.get(0).getAuthor()).isEqualTo(expectedAuthor);

        // 게시글 삭제
        url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        //restTemplate.delete(url);
        restTemplate.exchange(url, HttpMethod.DELETE, responseEntity, void.class);

    }


}
