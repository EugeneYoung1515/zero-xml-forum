package com.smart.redisdao;

import com.smart.domain.Board;
import org.springframework.stereotype.Repository;

@Repository("redisBoardDao")
public class BoardDao extends BaseDao<Board> {
}
