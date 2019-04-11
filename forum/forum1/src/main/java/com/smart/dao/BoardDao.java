package com.smart.dao;

import com.smart.domain.Board;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Iterator;

@Repository
public class BoardDao extends BaseDao<Board>{
	//private HibernateTemplate hibernateTemplate;
	private static final String GET_BOARD_NUM = "select count(f.boardId) from Board f";

	public long getBoardNum2() {
		Iterator iter = getHibernateTemplate().iterate(GET_BOARD_NUM);
        return ((Long)iter.next());
	}
	public long getBoardNum(){
		int tmp = getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			public Integer doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(GET_BOARD_NUM);
				Object object = query.list().iterator().next();
				long l = (Long)object;
				return (int)l;
			}
		});
		return tmp;
	}
	//@Autowired
	//public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		//this.hibernateTemplate = hibernateTemplate;
	//}
}
