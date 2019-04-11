package com.smart.repository;

import com.smart.domain.User;

import java.util.List;

public interface UserRepository extends BaseRepository<User> {
    User findOneByUserName(String name);
    List<User> findByUserName(String name);//这里可能要改成模糊匹配 like

}
