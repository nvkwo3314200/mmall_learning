package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {
	private boolean allChecked;
	private BigDecimal cartTotalPrice;
	private String imgHost;
	
	private List<CartProductVo> cartProductVoList;

	public boolean isAllChecked() {
		return allChecked;
	}

	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}

	public BigDecimal getCartTotalPrice() {
		return cartTotalPrice;
	}

	public void setCartTotalPrice(BigDecimal cartTotalPrice) {
		this.cartTotalPrice = cartTotalPrice;
	}

	public List<CartProductVo> getCartProductVoList() {
		return cartProductVoList;
	}

	public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
		this.cartProductVoList = cartProductVoList;
	}

	public String getImgHost() {
		return imgHost;
	}

	public void setImgHost(String imgHost) {
		this.imgHost = imgHost;
	}
	
	
}
