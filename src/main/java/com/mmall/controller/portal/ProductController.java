package com.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mmall.common.Constant;
import com.mmall.common.ResponseService;
import com.mmall.service.IProductService;

@RestController
@RequestMapping("product")
public class ProductController {
	@Autowired
	IProductService iProductService;
	
	@RequestMapping(value="list.do", method=RequestMethod.POST)
	public ResponseService<?> list(@RequestParam(value="pageNum" ,defaultValue="1")Integer pageNum, 
			@RequestParam(value="pageSize" ,defaultValue="10")Integer pageSize,
			@RequestParam(value="productName", required = false)String productName,
			@RequestParam(value="categoryId", required = false)Integer categoryId,
			@RequestParam(value="orderBy", defaultValue="name_asc")String orderBy) {
		return iProductService.search(pageNum, pageSize, productName, categoryId, orderBy, Constant.SaleStatus.ON_SALE.getCode());
	}
	
	@RequestMapping(value="detail.do", method=RequestMethod.POST)
	public ResponseService<?> detail(Integer productId) {
		return iProductService.getDetail(productId);
	}
}
