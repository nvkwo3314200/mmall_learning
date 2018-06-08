package com.mmall.controller.backend;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.mmall.common.ResponseService;
import com.mmall.pojo.Product;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;

@RestController
@RequestMapping("manage/product")
public class ProductManageController {
	
	@Autowired
	IProductService iProductService;
	
	@Autowired
	IUserService IUserService;
	
	@Autowired
	IFileService iFileService;
	
	private static final String imgHost = PropertiesUtil.getProperty("img.host");
	
	@RequestMapping(value="list.do", method=RequestMethod.POST)
	public ResponseService<?> list(HttpSession session, 
			@RequestParam(value="pageNum" ,defaultValue="1")Integer pageNum, 
			@RequestParam(value="pageSize" ,defaultValue="10")Integer pageSize) {
		ResponseService<String> response = IUserService.checkUserPower(session);
		if(!response.isSuccess()) return response;
		return iProductService.list(pageNum, pageSize);
	}
	
	@RequestMapping(value="search.do", method=RequestMethod.POST)
	public ResponseService<?> search(HttpSession session, 
			@RequestParam(value="pageNum" ,defaultValue="1")Integer pageNum, 
			@RequestParam(value="pageSize" ,defaultValue="10")Integer pageSize,
			@RequestParam(value="productName", required = false)String productName,
			@RequestParam(value="categoryId", required = false)Integer categoryId,
			@RequestParam(value="orderBy", defaultValue="name_asc")String orderBy) {
		ResponseService<String> response = IUserService.checkUserPower(session);
		if(!response.isSuccess()) return response;
		return iProductService.search(pageNum, pageSize, productName, categoryId, orderBy, null);
	}
	
	
	@RequestMapping(value="upload.do", method=RequestMethod.POST)
	public ResponseService<?> upload(HttpSession session, HttpServletRequest request, @RequestParam(value="upload_file")MultipartFile file) {
		ResponseService<String> response = IUserService.checkUserPower(session);
		if(!response.isSuccess()) return response;
		String filePath = request.getServletContext().getRealPath("img");
		String targetFileName = iFileService.upload(file, filePath);
		Map<String, String> map = Maps.newHashMap();
		map.put("uri", targetFileName);
		map.put("url", imgHost + targetFileName);
		if(targetFileName != null) {
			return ResponseService.createSuccessResponse(map);
		}
		return ResponseService.createErrorResponseMessage("上传文件失败");
	}
	
	// 按前端editor的规范来做
	@RequestMapping(value="richtext_img_upload.do", method=RequestMethod.POST)
	public Map<String, Object> richtextImgUpload(HttpSession session, HttpServletRequest request, @RequestParam(value="upload_file")MultipartFile file) {
		ResponseService<String> response = IUserService.checkUserPower(session);
		if(!response.isSuccess()) {
			return createResponseMap(false, "上传失败", null);
		}
		String filePath = request.getServletContext().getRealPath("img");
		String targetFileName = iFileService.upload(file, filePath);
		if(targetFileName != null) {
			return createResponseMap(true, "上传成功", imgHost + targetFileName);
		}
		return createResponseMap(false, "上传失败", null);
	}
	
	private Map<String, Object> createResponseMap(boolean success, String msg, String filePath) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("success", success);
		map.put("msg", msg);
		map.put("file_path", filePath);
		return map;
	}
	
	@RequestMapping(value="detail.do", method=RequestMethod.POST)
	public ResponseService<?> detail(HttpSession session, Integer productId) {
		ResponseService<String> response = IUserService.checkUserPower(session);
		if(!response.isSuccess()) return response;
		return iProductService.getDetail(productId);
	}
	
	@RequestMapping(value="set_sale_status.do", method=RequestMethod.POST)
	public ResponseService<?> setSaleStatus(HttpSession session, Integer productId, Integer status) {
		ResponseService<String> response = IUserService.checkUserPower(session);
		if(!response.isSuccess()) return response;
		return iProductService.updateSaleStatus(productId, status);
	}
	
	
	@RequestMapping(value="save.do", method=RequestMethod.POST)
	public ResponseService<?> save(HttpSession session, Product product) {
		ResponseService<String> response = IUserService.checkUserPower(session);
		if(!response.isSuccess()) return response;
		return iProductService.save(product);
	}
	
}
