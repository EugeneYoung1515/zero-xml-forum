
package com.smart.web;

import com.smart.domain.Board;
import com.smart.domain.User;
import com.smart.service.ForumService;
import com.smart.service.UserService;
import com.smart.serviceinterfaces.ForumServiceInterface;
import com.smart.serviceinterfaces.UserServiceInterface;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 *   论坛管理，这部分功能由论坛管理员操作，包括：创建论坛版块、指定论坛版块管理员、
 * 用户锁定/解锁。
 */
@Controller
public class ForumManageController extends BaseController {
	private ForumServiceInterface forumService;

	private UserServiceInterface userService;

	@Autowired
	@Qualifier("redisForumService")
	public void setForumService(ForumServiceInterface forumService) {
		this.forumService = forumService;
	}

	@Autowired
	@Qualifier("redisUserService")
	public void setUserService(UserServiceInterface userService) {
		this.userService = userService;
	}

	/**
	 * 列出所有的论坛模块
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView listAllBoards() {
		ModelAndView view =new ModelAndView();
		List<Board> boards = forumService.getAllBoards();
		view.addObject("boards", boards);
		view.setViewName("/listAllBoards");
		return view;
	}

	/**
	 *  添加一个主题帖
	 * @return
	 */

	@RequiresPermissions("sys:man")
	@RequestMapping(value = "/addBoardPage", method = RequestMethod.GET)
	//和这里没有匹配
	public String addBoardPage() {
		return "/addBoard";
	}

	/**
	 * 添加一个主题帖
	 * @param board
	 * @return
	 */

	@RequiresPermissions("sys:man")
	@RequestMapping(value = "/boards", method = RequestMethod.PUT)//原来jsp addboard中 加了一行 使得请求方法变成put
	//这里要从post改成put

	//Request method 'PUT' not supported
	//访问一个url http方法不对 会说这个方法不支持

	//几个问题 jsp和java类
	//http 方法不一致
	//大小写不一致
	//http请求参数名不一致
	public String addBoard(Board board) {
		forumService.addBoard(board);
		return "/addBoardSuccess";
	}

	/**
	 * 指定论坛管理员的页面
	 * @return
	 */

	/*
	@RequestMapping(value = "/forum/setBoardManagerPage", method = RequestMethod.GET)
	public ModelAndView setBoardManagerPage() {
		ModelAndView view =new ModelAndView();
		List<Board> boards = forumService.getAllBoards();
		List<User> users = userService.getAllUsers();
		view.addObject("boards", boards);
		view.addObject("users", users);
		view.setViewName("/setBoardManager");
		return view;
	}
	*/

	@RequiresPermissions("sys:man")
	@RequestMapping(value = "/setBoardManagerPage", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> setBoardManagerPage() {
		List<Board> boards = forumService.getAllBoards();
		List<User> users = userService.getAllUsers();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("boards",boards);
		map.put("users",users);
		return map;
	}

	/**
     * 设置版块管理
     * @return
     */

	@RequiresPermissions("sys:man")
	@RequestMapping(value = "/users/managers", method = RequestMethod.PATCH)
	public ModelAndView setBoardManager(@RequestParam("userName") String userName
			,@RequestParam("boardId") String boardId) {
		ModelAndView view =new ModelAndView();
		User user = userService.getUserByUserName(userName);
		if (user == null) {
			view.addObject("errorMsg", "用户名(" + userName
					+ ")不存在");
			view.setViewName("/fail");
		} else {
			Board board = forumService.getBoardById(Integer.parseInt(boardId));
			user.getManBoards().add(board);
			userService.update(user);
			view.setViewName("/success");
		}
		return view;
	}

	/**
	 * 用户锁定及解锁管理页面
	 * @return
	 */
	/*
	@RequestMapping(value = "/forum/userLockManagePage", method = RequestMethod.GET)
	public ModelAndView userLockManagePage() {
		ModelAndView view =new ModelAndView();
		List<User> users = userService.getAllUsers();
		view.setViewName("/userLockManage");
		view.addObject("users", users);
		return view;
	}
	 */

	@RequiresPermissions("sys:man")
	@RequestMapping(value = "/userLockManagePage", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> userLockManagePage() {
		List<User> users = userService.getAllUsers();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("users",users);
		return map;
	}

	/**
	 * 用户锁定及解锁设定
	 * @return
	 */

	@RequiresPermissions("sys:man")
	@RequestMapping(value = "/users/locks", method = RequestMethod.PATCH)
	public ModelAndView userLockManage(@RequestParam("userName") String userName
			,@RequestParam("locked") String locked) {
		ModelAndView view =new ModelAndView();
        User user = userService.getUserByUserName(userName);
		if (user == null) {
			view.addObject("errorMsg", "用户名(" + userName
					+ ")不存在");
			view.setViewName("/fail");
		} else {
			user.setLocked(Integer.parseInt(locked));
			userService.update(user);
			view.setViewName("/success");
		}
		return view;
	}
}
