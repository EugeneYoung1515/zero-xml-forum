package com.smart.serviceinterfaces;

import com.smart.dao.Page;
import com.smart.domain.Board;
import com.smart.domain.Post;
import com.smart.domain.Topic;

import java.util.List;

public interface ForumServiceInterface {
    public void addTopic(Topic topic);
    public void removeTopic(int topicId);
    public void addPost(Post post);
    public void addBoard(Board board);
    public void removeBoard(int boardId);
    public void makeDigestTopic(int topicId);
    public List<Board> getAllBoards();
    public Page getPagedTopics(int boardId, int pageNo, int pageSize);
    public Page getPagedPosts(int topicId,int pageNo,int pageSize);
    public Board getBoardById(int boardId);
    public Topic getTopicByTopicId(int topicId);
}
