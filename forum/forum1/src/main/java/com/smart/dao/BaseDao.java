package com.smart.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DAO基类，其它DAO可以直接继承这个DAO，不但可以复用共用的方法，还可以获得泛型的好处。
 */

@Transactional(readOnly = true)
public class BaseDao<T>{
	private Class<T> entityClass;

	private HibernateTemplate hibernateTemplate;

	private SessionFactory sessionFactory;
	/**
	 * 通过反射获取子类确定的泛型类
	 */
	public BaseDao() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		entityClass = (Class) params[0];
	}



	/**
	 * 根据ID加载PO实例
	 * 
	 * @param id
	 * @return 返回相应的持久化PO实例
	 */
	public T load(Serializable id) {
		return (T) getHibernateTemplate().load(entityClass, id);
	}

	/**
	 * 根据ID获取PO实例
	 * 
	 * @param id
	 * @return 返回相应的持久化PO实例
	 */
	public T get(Serializable id) {
		Statistics statistics=sessionFactory.getStatistics();
		System.out.println("stats");
		System.out.println(statistics.getSecondLevelCachePutCount());
		System.out.println(statistics.getSecondLevelCacheHitCount());
		System.out.println(statistics.getSecondLevelCacheMissCount());
		return (T) getHibernateTemplate().get(entityClass, id);
	}

	/**
	 * 获取PO的所有对象
	 * 
	 * @return
	 */
	public List<T> loadAll() {
		return getHibernateTemplate().loadAll(entityClass);
	}
	
	/**
	 * 保存PO
	 * 
	 * @param entity
	 */

	@Transactional(readOnly = false)
	public void save(T entity) {
		getHibernateTemplate().save(entity);
	}

	/**
	 * 删除PO
	 * 
	 * @param entity
	 */
	public void remove(T entity) {
		getHibernateTemplate().delete(entity);
	}

	/**
	 * 删除tableNames数据
	 *
	 */
	public void removeAll(String tableName) {
		getSession().createSQLQuery("truncate TABLE " + tableName +"").executeUpdate();
	}

	/**
	 * 更改PO
	 * 
	 * @param entity
	 */

	@Transactional(readOnly =false)
	public void update(T entity) {
		getHibernateTemplate().update(entity);
	}

	/**
	 * 执行HQL查询
	 * 
	 * @param sql
	 * @return 查询结果
	 */
	public List find2(String hql) {
		return this.getHibernateTemplate().find(hql);
	}//上面的find方法可能过期了

	public List find(final String hql){
		List tmp = getHibernateTemplate().execute(new HibernateCallback<List>() {
			public List doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(hql);
				List list = query.list();
				return list;
			}
		});
				return tmp;
	}

	/**
	 * 执行带参的HQL查询
	 * 
	 * @param sql
	 * @param params
	 * @return 查询结果
	 */
	public List find2(String hql, Object... params) {
		return this.getHibernateTemplate().find(hql,params);
	}//find方法可能过期了

	public List find(final String hql, final Object... params){
		List tmp = getHibernateTemplate().execute(new HibernateCallback<List>() {
			public List doInHibernate(Session session) throws HibernateException {
				Query query=session.createQuery(hql);
				for (int i = 0;i < params.length;i++){
					query.setParameter(i,params[i]);
				}
				List list = query.list();
				return list;
			}
		});
		return tmp;
	}
    
	/**
	 * 对延迟加载的实体PO执行初始化
	 * @param entity
	 */
	public void initialize(Object entity) {
		this.getHibernateTemplate().initialize(entity);
	}
	
	
	/**
	 * 分页查询函数，使用hql.
	 *
	 * @param pageNo 页号,从1开始.
	 */
	public Page pagedQuery(String hql, int pageNo, int pageSize, final Object... values) {//这个包里还有一个page类
		Assert.hasText(hql);
		Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
		// Count查询
		final String countQueryString = " select count (*) " + removeSelect(removeOrders(hql));//removeSelect在下面
																						//removeOrders也在下面
																						//合起来的结果是移除select 和 order by？
		//下面写的
		//去除hql的select 子句，未考虑union的情况,用于pagedQuery.
		//去除hql的orderby 子句，用于pagedQuery.

		//List countlist = getHibernateTemplate().find(countQueryString, values);//find方法可能过期了
		List countlist = getHibernateTemplate().execute(new HibernateCallback<List>() {
			public List doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(countQueryString);
				for (int i = 0;i<values.length;i++){
					query.setParameter(i,values[i]);
				}
				return query.list();
			}
		});//自己做法是对的 看下面的public Query createQuery(String hql, Object... values) {

		long totalCount = (Long) countlist.get(0);//按名字想 总的计数

		if (totalCount < 1)
			return new Page();
		// 实际查询返回分页对象
		int startIndex = Page.getStartOfPage(pageNo, pageSize);//静态方法
		Query query = createQuery(hql, values);//调用自己的方法 看下面
		List list = query.setFirstResult(startIndex).setMaxResults(pageSize).list();
		//这边是分页查询
		//查询结果放到Page对象中

		return new Page(startIndex, totalCount, pageSize, list);//看起来Page对象 只是表示一页的数据
	}

	/**
	 * 创建Query对象. 对于需要first,max,fetchsize,cache,cacheRegion等诸多设置的函数,可以在返回Query后自行设置.
	 * 留意可以连续设置,如下：
	 * <pre>
	 * dao.getQuery(hql).setMaxResult(100).setCacheable(true).list();
	 * </pre>
	 * 调用方式如下：
	 * <pre>
	 *        dao.createQuery(hql)
	 *        dao.createQuery(hql,arg0);
	 *        dao.createQuery(hql,arg0,arg1);
	 *        dao.createQuery(hql,new Object[arg0,arg1,arg2])
	 * </pre>
	 *
	 * @param values 可变参数.
	 */

	@Transactional(readOnly = false)
	public Query createQuery(String hql, Object... values) {
		Assert.hasText(hql);
		Query query = getSession().createQuery(hql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}
	/**
	 * 去除hql的select 子句，未考虑union的情况,用于pagedQuery.
	 *
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private static String removeSelect(String hql) {//按名字想 移除Select
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql + " must has a keyword 'from'");
		return hql.substring(beginPos);
	}
	
	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
	 *
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private static String removeOrders(String hql) {//上面说了 去除hql的orderby 子句，用于pagedQuery.
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		//两个\\ 转义\ 用来表示\
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");//用空串去替换 orderby 子句
		}
		m.appendTail(sb);
		return sb.toString();
	}//上面是正则表达式相关

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	@Autowired
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
    public  Session getSession() {
        return hibernateTemplate.getSessionFactory().getCurrentSession();
    }

    @Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}