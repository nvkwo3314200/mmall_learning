package com.mmall.service;

import java.util.List;

import com.mmall.common.ResponseService;
import com.mmall.pojo.Category;

public interface ICategoryService {

	ResponseService<String> addCategory(Integer parentId, String categoryName);

	ResponseService<String> setCategoryName(Integer categoryId, String categoryName);

	ResponseService<List<Category>> getChildrenParallelCategory(Integer parentId);

	ResponseService<List<Integer>> getChildrenCategoryIds(Integer parentId);

}
