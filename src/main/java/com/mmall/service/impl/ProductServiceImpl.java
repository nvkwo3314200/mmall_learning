package com.mmall.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Constant;
import com.mmall.common.ResponseService;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductVo;

@Service("iProductService")
public class ProductServiceImpl implements IProductService{
	
	@Autowired
	ProductMapper productMapper;
	
	@Autowired
	ICategoryService iCategoryService;
	
	@Autowired
	CategoryMapper categoryMapper;
	
	private static String imgHost = PropertiesUtil.getProperty("img.host");

	@Override
	public ResponseService<PageInfo<ProductVo>> list(Integer pageNum, Integer pageSize) {
		List<ProductVo> voList = Lists.newArrayList();
		PageHelper.startPage(pageNum, pageSize);
		List<Product> list = productMapper.list();
		for(Product product : list) {
			voList.add(assembleProductVo(product));
		}
		PageInfo<ProductVo> page = new PageInfo<ProductVo>(voList);
		return ResponseService.createSuccessResponse(page);
	}

	private ProductVo assembleProductVo(Product product) {
		ProductVo productVo = new ProductVo();
		productVo.setId(product.getId());
		productVo.setCategoryId(product.getCategoryId());
		productVo.setName(product.getName());
		productVo.setMainImage(product.getMainImage());
		productVo.setStatus(product.getStatus());
		productVo.setPrice(product.getPrice());
		productVo.setSubtitle(product.getSubtitle());
		productVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
		return productVo;
	}

	@Override
	public ResponseService<PageInfo<ProductVo>> search(Integer pageNum, Integer pageSize, String productName,
			Integer categoryId, String orderBy, Integer status) {
		List<ProductVo> voList = Lists.newArrayList();
		List<Integer> categoryIdList = null;
		ResponseService<List<Integer>> response = iCategoryService.getChildrenCategoryIds(categoryId);
		if(response.isSuccess()) categoryIdList = response.getData();
		
		if(StringUtils.isNotBlank(productName)) {
			productName = new StringBuffer().append("%").append(productName).append("%").toString();
		}
		
		PageHelper.startPage(pageNum, pageSize);
		if(Constant.PRODUCT_ORDER_BY_SET.contains(orderBy)) {
			String[] orderBys = orderBy.split("_");
			PageHelper.orderBy(orderBys[0] + " " + orderBys[1]);
		}
		List<Product> list = productMapper.selectByProductNameAndCategoryId(StringUtils.isBlank(productName)? null : productName, 
				CollectionUtils.isEmpty(categoryIdList)? null : categoryIdList, status);
		for(Product product : list) {
			voList.add(assembleProductVo(product));
		}
		PageInfo<ProductVo> page = new PageInfo<ProductVo>(voList);
		return ResponseService.createSuccessResponse(page);
	}

	@Override
	public ResponseService<ProductDetailVo> getDetail(Integer productId) {
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null) {
			return ResponseService.createErrorResponseMessage("没有对应产品");
		}
		ProductDetailVo vo = assembleProductDetailVo(product);
		return ResponseService.createSuccessResponse(vo);
	}

	private ProductDetailVo assembleProductDetailVo(Product product) {
		ProductDetailVo productDetailVo = new ProductDetailVo();
		productDetailVo.setId(product.getId());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setName(product.getName());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setSubImages(product.getSubImages());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setStock(product.getStock());
		productDetailVo.setImageHost(imgHost);
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if(category != null) productDetailVo.setParentCategoryId(category.getParentId());
		return productDetailVo;
	}

	@Override
	public ResponseService<String> updateSaleStatus(Integer productId, Integer status) {
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		int count = productMapper.updateByPrimaryKeySelective(product);
		if(count > 0) {
			return ResponseService.createSuccessResponseMessage("修改产品状态成功");
		}
		return ResponseService.createSuccessResponseMessage("修改产品状态失败");
	}

	@Override
	public ResponseService<String> save(Product product) {
		ResponseService<String> response =  validate(product);
		if(response.isSuccess()) {
			if(StringUtils.isNotBlank(product.getSubImages())) {
				String mainImage = product.getSubImages().split(",")[0];
				product.setMainImage(mainImage);
			}
			if(product.getId() == null) {
				int count = productMapper.insert(product);
				if(count > 0) return ResponseService.createSuccessResponseMessage("新增产品成功");
			} else {
				int count = productMapper.updateByPrimaryKeySelective(product);
				if(count > 0) return ResponseService.createSuccessResponseMessage("更新产品成功");
			}
			return ResponseService.createErrorResponseMessage("更新产品失败");
		}
		return response;
	}

	private ResponseService<String> validate(Product product) {
		if(StringUtils.isBlank(product.getName())) {
			return ResponseService.createErrorResponseMessage("产品名称不能为空");
		}
		if(product.getCategoryId() == null) {
			return ResponseService.createErrorResponseMessage("产品 分类不能为空");
		}
		return ResponseService.createSuccessResponse();
	}
	
}
