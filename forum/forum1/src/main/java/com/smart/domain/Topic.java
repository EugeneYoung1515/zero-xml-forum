package com.smart.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.smart.redis.CustomDateDeserialize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Table(name = "t_topic")
public class Topic extends BaseDomain {
	/**
	 * 精华主题帖子
	 */
	public static final int DIGEST_TOPIC = 1;
	/**
	 * 普通的主题帖子
	 */
	public static final int NOT_DIGEST_TOPIC = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "topic_id")
	private int topicId;

	@Column(name = "topic_title")
	private String topicTitle;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "board_id")
	private int boardId;

	@Transient
	@JsonIgnore//这个是加了Redis 要用Json的Map POJO互转时加的
	private MainPost mainPost = new MainPost();

	@Column(name = "last_post")
	//@JsonDeserialize(using = CustomDateDeserialize.class)
	private Date lastPost = new Date();

	@Column(name = "create_time")
	//@JsonDeserialize(using = CustomDateDeserialize.class)
	private Date createTime = new Date();

	@Column(name = "topic_views")
	private int views;

	@Column(name = "topic_replies")
	private int replies;

	private int digest = NOT_DIGEST_TOPIC;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getDigest() {
		return digest;
	}

	public void setDigest(int digest) {
		this.digest = digest;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public Date getLastPost() {
		return lastPost;
	}

	public void setLastPost(Date lastPost) {
		this.lastPost = lastPost;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public MainPost getMainPost() {
		return mainPost;
	}

	public void setMainPost(MainPost mainPost) {
		this.mainPost = mainPost;
	}

	// public Set<MainPost> getMainPosts()
	// {
	// return mainPosts;
	// }
	//
	// public void setMainPosts(Set<MainPost> mainPosts)
	// {
	// this.mainPosts = mainPosts;
	// }

}
