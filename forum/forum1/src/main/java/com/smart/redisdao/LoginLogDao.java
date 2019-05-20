package com.smart.redisdao;

import com.smart.domain.LoginLog;
import org.springframework.stereotype.Repository;

@Repository("redisLoginLogDao")
public class LoginLogDao extends BaseDao<LoginLog> {
}
