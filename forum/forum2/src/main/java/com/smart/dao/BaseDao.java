package com.smart.dao;

import com.smart.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * DAO基类，其它DAO可以直接继承这个DAO，不但可以复用共用的方法，还可以获得泛型的好处。
 */

//@Transactional//(readOnly = true)
public class BaseDao<T>{
	private Class<T> entityClass;

	@Autowired
	private BaseRepository<T> baseRepository;
	/**
	 * 通过反射获取子类确定的泛型类
	 */
	public BaseDao() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		entityClass = (Class) params[0];
	}

	/**
	 * 根据ID获取PO实例
	 * 
	 * @param id
	 * @return 返回相应的持久化PO实例
	 */
	public T get(Serializable id){
		return baseRepository.findOne((Integer)id);
	}

	/**
	 * 获取PO的所有对象
	 * 
	 * @return
	 */

	public List<T> loadAll(){
		return baseRepository.findAll();
	}
	
	/**
	 * 保存PO
	 * 
	 * @param entity
	 */

	//@Transactional//(readOnly = false)
	public void save(T entity) {
		baseRepository.save(entity);
	}

	/**
	 * 删除PO
	 * 
	 * @param entity
	 */

	public void remove(T entity){
		baseRepository.delete(entity);
	}

	/**
	 * 更改PO
	 * 
	 * @param entity
	 */

	@Transactional//(readOnly =false)
	public void update(T entity){//也是save
		baseRepository.save(entity);
	}

	public Page returnNewPage(List<T> list,List<T> list2,int pageNo, int pageSize){
		int listCount = list.size();
		System.out.println("listCount"+listCount);
		if ( listCount< 1){
			return new Page();
		}else {
			int startIndex = Page.getStartOfPage(pageNo, pageSize);
			return new Page(startIndex, listCount, pageSize, list2);
		}
	}
}