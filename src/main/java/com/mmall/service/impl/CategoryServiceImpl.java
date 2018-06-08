package com.mmall.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ResponseService;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
	private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired
	private CategoryMapper categoryMapper;

	@Override
	public ResponseService<String> addCategory(Integer parentId, String categoryName) {
		if(StringUtils.isBlank(categoryName)) {
			return ResponseService.createErrorResponseMessage("品类名称不能为空");
		}
		Category category = new Category();
		category.setParentId(parentId);
		category.setName(categoryName);
		category.setStatus(true);
		int count = categoryMapper.insertSelective(category);
		if(count > 0) {
			return ResponseService.createSuccessResponseMessage("添加品类成功");
		}
		return ResponseService.createErrorResponseMessage("添加品类失败");
	}

	@Override
	public ResponseService<String> setCategoryName(Integer categoryId, String categoryName) {
		if(StringUtils.isBlank(categoryName)) {
			return ResponseService.createErrorResponseMessage("品类名称不能为空");
		}
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);
		int count = categoryMapper.updateByPrimaryKeySelective(category);
		if(count > 0) {
			return ResponseService.createSuccessResponseMessage("更新品类名字成功");
		}
		return ResponseService.createErrorResponseMessage("更新品类名字失败");
	}

	@Override
	public ResponseService<List<Category>> getChildrenParallelCategory(Integer parentId) {
		List<Category> categoryList = categoryMapper.getChildrenParallelCategory(parentId);
		if(CollectionUtils.isEmpty(categoryList)) {
			logger.info("品类集合为空");
		}
		return ResponseService.createSuccessResponse(categoryList);
	}

	@Override
	public ResponseService<List<Integer>> getChildrenCategoryIds(Integer parentId) {
		Set<Category> set = Sets.newHashSet();
		Category self = categoryMapper.selectByPrimaryKey(parentId);
		if(self != null) {
			// add self
			set.add(self);
		}
		// add children
		addChildren(set, parentId);
		List<Integer> categoryList = Lists.newArrayList();
		for(Category category : set) {
			categoryList.add(category.getId());
		}
		return ResponseService.createSuccessResponse(categoryList);
	}
	
	private void addChildren(Set<Category> categorySet, Integer parentId) {
		List<Category> categoryList = categoryMapper.getChildrenParallelCategory(parentId);
		categorySet.addAll(categoryList);
		for(Category item : categoryList) {
			addChildren(categorySet, item.getId());
		}
	}

}
