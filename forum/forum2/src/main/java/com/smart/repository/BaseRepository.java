package com.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T,Integer> {
    //public T findOne(Integer id);继承了不用在重写//声明
}
//少了分页查询
