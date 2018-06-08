package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseService;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductVo;

public interface IProductService {

	public ResponseService<PageInfo<ProductVo>> list(Integer pageNum, Integer pageSize);

	public ResponseService<PageInfo<ProductVo>> search(Integer pageNum, Integer pageSize, String productName, 
			Integer categoryId, String orderBy, Integer status);

	public ResponseService<ProductDetailVo> getDetail(Integer productId);

	public ResponseService<String> updateSaleStatus(Integer productId, Integer status);

	public ResponseService<String> save(Product product);
	
}
