package com.smart.dao;

import com.smart.domain.Board;
import com.smart.repository.BoardRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

//@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)

@RunWith(SpringRunner.class)
@DataJpaTest
public class BoardDaoTest {

    //@Autowired
    //BoardDao boardDao;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BoardRepository boardRepository;

    @Test
    @Transactional
    public void findBoard(){//测试前把application.properties中的ddl-auto改了
        Board board2 = new Board();
        board2.setBoardName("kk2");
        board2.setBoardDesc("kk");
        board2.setTopicNum(1);

        Board board = new Board();
        board.setBoardName("kk");
        board.setBoardDesc("kk");
        board.setTopicNum(1);

        Board board3 = new Board();
        board3.setBoardName("kk3");
        board3.setBoardDesc("kk3");
        board3.setTopicNum(2);

        //testEntityManager.merge(board2)
        //testEntityManager.persist(board2);//测试报错时看源码 使用getEntityManager().persist(entity)就好了;
        testEntityManager.getEntityManager().persist(board3);
        //testEntityManager.flush();

        //boardDao.save(board2);

        boardRepository.save(board);
        Board board4 = boardRepository.findOne(1);
        assertNotNull(board4);
        assertEquals("kk3",board4.getBoardName());
    }
    //*Repository接口里有新方法的要测试

    //dao层除了分页查询外 几乎是和Repository层一对一的

    //要测试某一层 就不要用这一层的方法

    //service层测试
    //springboot提供了一个@MockBean注解 分离测试
    //或者用现在dao层测试这种形式 @SpringBootTest带上其他注解
}

//几种选择
//1. 单独用@DataJpaTest
//2. @DataJpaTest加上@AutoConfigureTestXXX
// 看源码 @DataJpaTest本身就带有@AutoConfigureTestXXX注解
//再加上@AutoConfigureTestXXX注解 是为了重写@DataJpaTest带的配置
//3. @SpringBootTest加上@AutoConfigureTestXXX
//使用@SpringBootTest 使得能够注入boardDao之类的bean

