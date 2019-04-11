package com.smart.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.smart.config.DaoConfig;
import com.smart.domain.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoConfig.class)
@TestExecutionListeners({
        TransactionDbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class})
//@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
public class BoardDaoTest{

    @Autowired
    private BoardDao boardDao;

    /**
     * 创建一个新的论坛版块,并更新
     *
     * @param
     */


    @Test
    @DatabaseSetup("classpath:full.xml")
    @ExpectedDatabase(value = "classpath:full2.xml",assertionMode = DatabaseAssertionMode.NON_STRICT)
    @Transactional
    public void addBoard() throws Exception {
        Board board = new Board();
        board.setBoardName("kk");
        board.setBoardDesc("kk");
        board.setTopicNum(1);
            boardDao.save(board);
    }
    //这里有个地方不够好 就是重新跑上面的测试方法之前 需要把full2.xml的id改了 因为回滚操作不会把 postgresql里 专门放自增主键的的哪个序列里的最新id也回滚了
    //自己用命令行试了 确实是这样 就是在一个事务中往表插入数据 之后回滚 放自增主键的序列里的数字也不会回滚


    /**
     * 删除一个版块
     *
     * @param
     */
    /*
    @Test
    public void removeBoard() {
        Board board = boardDao.get(66);
        boardDao.remove(board);
    }
*/
    @Test
    @DatabaseSetup("classpath:full.xml")
    @Transactional
    public void getBoard() {
        Board board = boardDao.load(22);
        assertNotNull(board);
        assertEquals(board.getBoardName(), "test");
    }
}
