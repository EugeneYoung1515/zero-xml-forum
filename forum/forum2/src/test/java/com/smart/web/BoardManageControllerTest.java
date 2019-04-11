package com.smart.web;


import com.smart.cons.CommonConstant;
import com.smart.dao.Page;
import com.smart.domain.Board;
import com.smart.domain.Topic;
import com.smart.service.ForumService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
//@WebMvcTest(BoardManageController.class)
@AutoConfigureMockMvc(addFilters = false)//没有这个注解属性 会说ServletContext not found 也就是shiro的问题
public class BoardManageControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ForumService forumService;//ForumService这个类上加了@Transaction注解 不把这个注解注释掉 上面就要用@SpringBootTest

    @Test
    public void listBoardTopics() throws Exception{
        Board board = new Board();
        board.setBoardDesc("kk");
        board.setBoardName("kk");
        given(forumService.getBoardById(1)).willReturn(board);
        Topic topic = new Topic();
        topic.setTopicTitle("test");
        List list = new ArrayList<Topic>();
        list.add(topic);
        Page page = new Page(1L,1L,3,list);
        given(forumService.getPagedTopics(1,1, CommonConstant.PAGE_SIZE)).willReturn(page);
        mvc.perform(get("/boards/1").accept(MediaType.TEXT_HTML_VALUE)).andExpect(status().isOk()).andExpect(model().attribute("pagedTopic",page));
        //model().attribute("pagedTopic",page) 能起效果 把page换成new page()就会报错
    }
}
