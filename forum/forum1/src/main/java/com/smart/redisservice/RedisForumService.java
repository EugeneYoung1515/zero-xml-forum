package com.smart.redisservice;

import com.smart.dao.Page;
import com.smart.domain.*;
import com.smart.redisdao.BoardDao;
import com.smart.redisdao.PostDao;
import com.smart.redisdao.TopicDao;
import com.smart.redisdao.UserDao;
import com.smart.serviceinterfaces.ForumServiceInterface;
import com.smart.utils.RequestHolderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.smart.cons.CommonConstant.USER_CONTEXT;

@Service("redisForumService")
public class RedisForumService implements ForumServiceInterface {

    private com.smart.redisdao.TopicDao redisTopicDao;
    private com.smart.redisdao.UserDao redisUserDao;
    private com.smart.redisdao.BoardDao redisBoardDao;
    private com.smart.redisdao.PostDao redisPostDao;

    @Override
    public void addTopic(Topic topic) {
        Board board = redisBoardDao.get(topic.getBoardId());
        board.setTopicNum(board.getTopicNum()+1);
        redisBoardDao.update(board);

        //System.out.println(topic.getUser()+"qwerty");

        redisTopicDao.save(topic);

        topic.getMainPost().setTopic(topic);
        MainPost post =topic.getMainPost();
        post.setCreateTime(new Date());
        post.setUser(topic.getUser());
        post.setPostTitle(topic.getTopicTitle());
        post.setBoardId(topic.getBoardId());
        redisPostDao.save(topic.getMainPost());
        //上面有一个问题 关于类型的

        User user = topic.getUser();
        user.setCredit(user.getCredit() - 10);
        redisUserDao.update(user);//@
        //这里没问题
        //user是从session中拿到的 赋给topic的
        //更新了session里的user includeTop.jsp里的显示的user也跟着更新
        //之后把数据库里的也跟着更新了

        //一对一 多对一中的一方要放在前面更新

        //但是有一个问题
        //就是要把 topic先save 才有id 之后post里的topic才有id
        //但是topic先save 就topic就没有关于post的信息
    }

    @Override
    public void removeTopic(int topicId) {
        Topic topic = redisTopicDao.get(topicId);

        Board board = redisBoardDao.get(topic.getBoardId());
        board.setTopicNum(board.getTopicNum() - 1);
        redisBoardDao.update(board);

        //User user = topic.getUser();
        User user = redisUserDao.get(topic.getUser().getUserId());
        user.setCredit(user.getCredit() - 50);
        redisUserDao.update(user);//@

        //这里有问题 数据库里user更新了 会话中的没有更新
        //可以移除别人发的话题 移除自己发的话题 会话中user就要更新

        updateSessionUser(user);

        redisTopicDao.remove(topic);
        redisPostDao.deleteTopicPosts(topicId);
    }

    @Override
    public void addPost(Post post) {

        User user = post.getUser();
        user.setCredit(user.getCredit() - 5);
        redisUserDao.update(user);//@
        //user是从session中拿到的 再赋给post

        Topic topic = redisTopicDao.get(post.getTopic().getTopicId());
        topic.setReplies(topic.getReplies() + 1);
        topic.setLastPost(new Date());

        //System.out.println("user: "+topic.getUser());
        //System.out.println("userId: "+topic.getUser().getUserId());

        redisTopicDao.update(topic);//@

        post.setTopic(topic);
        redisPostDao.save(post);
    }

    @Override
    public void addBoard(Board board) {
        redisBoardDao.save(board);
    }

    @Override
    public void removeBoard(int boardId) {
        Board board = redisBoardDao.get(boardId);
        redisBoardDao.remove(board);
    }

    @Override
    public void makeDigestTopic(int topicId) {
        Topic topic = redisTopicDao.get(topicId);
        topic.setDigest(Topic.DIGEST_TOPIC);

        User user = redisUserDao.get(topic.getUser().getUserId());
        user.setCredit(user.getCredit() + 100);
        redisUserDao.update(user);//@

        //这里有问题 数据库里user更新了 会话中的没有更新
        updateSessionUser(user);

        topic.setUser(user);
        redisTopicDao.update(topic);
    }

    private void updateSessionUser(User user){
        User sessionUser = (User)RequestHolderUtil.getSession().getAttribute(USER_CONTEXT);
        if(sessionUser.getUserId()==user.getUserId()){
            RequestHolderUtil.getSession().setAttribute(USER_CONTEXT,user);
        }
    }

    @Override
    public List<Board> getAllBoards() {
        return redisBoardDao.loadAll();
    }

    @Override
    public Page getPagedTopics(int boardId, int pageNo, int pageSize) {
        Page page = redisTopicDao.getPagedTopics(boardId,pageNo,pageSize);
        List<Topic> list= page.getData();
        for(Topic topic:list){
            topic.setUser(redisUserDao.get(topic.getUser().getUserId()));
        }
        return page;
    }

    @Override
    public Page getPagedPosts(int topicId, int pageNo, int pageSize) {
        Page page = redisPostDao.getPagedPosts(topicId,pageNo,pageSize);
        List<Post> list = page.getData();
        for(Post post:list){
            post.setUser(redisUserDao.get(post.getUser().getUserId()));
        }
        System.out.println(list.get(0).getTopic().getUser());
        return page;
    }

    @Override
    public Board getBoardById(int boardId) {
        return redisBoardDao.get(boardId);
    }

    @Override
    public Topic getTopicByTopicId(int topicId) {
        return redisTopicDao.get(topicId);
    }

    @Autowired
    @Qualifier("redisTopicDao")
    public void setRedisTopicDao(TopicDao redisTopicDao) {
        this.redisTopicDao = redisTopicDao;
    }

    @Autowired
    @Qualifier("redisUserDao")
    public void setRedisUserDao(UserDao redisUserDao) {
        this.redisUserDao = redisUserDao;
    }

    @Autowired
    @Qualifier("redisBoardDao")
    public void setRedisBoardDao(BoardDao redisBoardDao) {
        this.redisBoardDao = redisBoardDao;
    }

    @Autowired
    @Qualifier("redisPostDao")
    public void setRedisPostDao(PostDao redisPostDao) {
        this.redisPostDao = redisPostDao;
    }
}
