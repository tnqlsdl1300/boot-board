package com.subin.board.springboot.web;

import com.subin.board.springboot.domain.posts.Posts;
import com.subin.board.springboot.domain.posts.PostsRepository;
import com.subin.board.springboot.web.dto.PostsSaveRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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

}
