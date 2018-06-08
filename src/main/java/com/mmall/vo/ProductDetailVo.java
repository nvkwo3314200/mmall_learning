package com.mmall.vo;

public class ProductDetailVo extends BaseProductVo {
	private Integer parentCategoryId;
	private String imageHost;
	private String subImages;
	private String detail;
	private Integer stock;
	public Integer getParentCategoryId() {
		return parentCategoryId;
	}
	public void setParentCategoryId(Integer parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
	public String getImageHost() {
		return imageHost;
	}
	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}
	public String getSubImages() {
		return subImages;
	}
	public void setSubImages(String subImages) {
		this.subImages = subImages;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
}

