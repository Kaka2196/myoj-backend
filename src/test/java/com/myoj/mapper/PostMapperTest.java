package com.myoj.mapper;

import com.myoj.model.entity.Post;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子数据库操作测试
 *
 * @author yl
 * 
 */
@SpringBootTest
class PostMapperTest {

    @Resource
    private PostMapper postMapper;

    @Test
    void listPostWithDelete() {
        List<Post> postList = postMapper.listPostWithDelete(new Date());
        Assertions.assertNotNull(postList);
    }
    @Test
    void addPost(){
        Post post = new Post();
        post.setContent("Test1");
        post.setUserId(1l);
        post.setTitle("test");
        post.setTags("test");
        postMapper.insert(post);
    }
}