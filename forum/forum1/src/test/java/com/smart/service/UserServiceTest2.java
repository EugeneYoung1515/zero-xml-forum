package com.smart.service;


import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.smart.domain.Board;
import com.smart.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

//另一个UserService是分离测试
//这里是集成测试

@TestExecutionListeners({
        TransactionDbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class})
public class UserServiceTest2 extends BaseService{

    @Autowired
    private ForumService forumService;

    @Test
    @DatabaseSetup("classpath:full.xml")
    @ExpectedDatabase(value = "classpath:full2.xml",assertionMode = DatabaseAssertionMode.NON_STRICT)
    @Transactional
    public void register(){
        Board board = new Board();
        board.setTopicNum(1);
        board.setBoardName("kk");
        board.setBoardDesc("kk");
        forumService.addBoard(board);
    }
}
