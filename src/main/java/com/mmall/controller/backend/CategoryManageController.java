package com.mmall.controller.backend;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;

@RestController
@RequestMapping("manage/category")
public class CategoryManageController {
	
	@Autowired
	IUserService iUserService;
	
	@Autowired
	ICategoryService iCategoryService;
	
	
	@RequestMapping(value="add_category.do", method=RequestMethod.POST)
	public ResponseService<String> addCategory(HttpSession session,@RequestParam(value="parentId", defaultValue="0")Integer parentId, 
			String categoryName) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		ResponseService<String> response = iUserService.checkUserPower(user);
		if(response.isSuccess()) {
			return iCategoryService.addCategory(parentId, categoryName);
		}
		
		return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(), "用户未登录,请登录");
	}
	
	@RequestMapping(value="set_category_name.do", method=RequestMethod.POST)
	public ResponseService<String> setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		ResponseService<String> response = iUserService.checkUserPower(user);
		if(response.isSuccess()) {
			return iCategoryService.setCategoryName(categoryId, categoryName);
		}
		return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(), "用户未登录,请登录");
	}
	
	@RequestMapping(value="get_category.do", method=RequestMethod.POST)
	public ResponseService<List<Category>> getChildrenParallelCategory(HttpSession session, @RequestParam(value="parentId", defaultValue="0")Integer parentId) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		ResponseService<String> response = iUserService.checkUserPower(user);
		if(response.isSuccess()) {
			return iCategoryService.getChildrenParallelCategory(parentId);
		}
		return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(), "用户未登录,请登录");
	}
	
	@RequestMapping(value="get_deep_category.do", method=RequestMethod.POST)
	public ResponseService<List<Integer>> getChildrenCategoryIds(HttpSession session, @RequestParam(value="parentId", defaultValue="0")Integer parentId) {
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		ResponseService<String> response = iUserService.checkUserPower(user);
		if(response.isSuccess()) {
			return iCategoryService.getChildrenCategoryIds(parentId);
		}
		return ResponseService.createErrorResponseCodeMessage(ResponseCode.NON_LOGIN.getCode(), "用户未登录,请登录");
	}
}
