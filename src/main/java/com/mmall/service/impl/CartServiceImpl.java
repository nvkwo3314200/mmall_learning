package com.mmall.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.Constant;
import com.mmall.common.ResponseService;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.PriceUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;

@Service("iCartService")
public class CartServiceImpl implements ICartService{
	private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
	
	@Autowired
	CartMapper cartMapper;
	
	@Autowired
	ProductMapper productMapper;
	

	private CartVo getCartVo(Integer userId) {
		CartVo cartVo = new CartVo();
		ArrayList<CartProductVo> cartProductVoList = Lists.newArrayList();
		List<Cart> cartList = cartMapper.getCartByUserId(userId);
		if(CollectionUtils.isEmpty(cartList)) {
			return null;
		}
		for(Cart cart : cartList) {
			CartProductVo cartProductVo = new CartProductVo();
			cartProductVo.setId(cart.getId());
			cartProductVo.setProductId(cart.getProductId());
			cartProductVo.setUserId(cart.getUserId());
			cartProductVo.setProductChecked(cart.getChecked());
			
			Product product = productMapper.selectByPrimaryKey(cart.getProductId());
			if(product != null) {
				int quantity = 0;
				if(product.getStock() > cart.getQuantity()) {
					cartProductVo.setLimitQuantity(Constant.CartProPerties.LIMIT_NUM_SUCCESS);
					quantity = cart.getQuantity();
				} else {
					Cart updateCart = new Cart();
					updateCart.setId(cart.getId());
					quantity = product.getStock();
					updateCart.setQuantity(quantity);
					int count = cartMapper.updateByPrimaryKeySelective(updateCart);
					if(count > 0) {
						cartProductVo.setLimitQuantity(Constant.CartProPerties.LIMIT_NUM_FAIL);
					} else {
						// TODO 对产品更新失败的处理
					}
				}
				cartProductVo.setQuantity(quantity);
				cartProductVo.setProductName(product.getName());
				cartProductVo.setProductMainImage(product.getMainImage());
				cartProductVo.setProductPrice(product.getPrice());
				cartProductVo.setProductStatus(product.getStatus());
				cartProductVo.setProductStock(product.getStock());
				cartProductVo.setProductSubtitle(product.getSubtitle());
				cartProductVo.setProductTotalPrice(PriceUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
			} else {
				// TODO 对产品为空的处理
			}
			cartProductVoList.add(cartProductVo);
		}
		cartVo.setCartProductVoList(cartProductVoList);
		cartVo.setAllChecked(getAllCheckedProduct(userId));
		cartVo.setCartTotalPrice(countCartTotalPrice(userId, cartProductVoList) );
		cartVo.setImgHost(PropertiesUtil.getProperty("img.host"));
		return cartVo;
	}

	private BigDecimal countCartTotalPrice(Integer userId, ArrayList<CartProductVo> cartProductVoList) {
		BigDecimal totalPrice = new BigDecimal("0");
		for(CartProductVo item : cartProductVoList) {
			if(item.getProductChecked() == Constant.CartProPerties.CHECKED) {
				totalPrice = PriceUtil.add(totalPrice.doubleValue(), item.getProductTotalPrice().doubleValue());
			}
		}
		return totalPrice;
	}

	private boolean getAllCheckedProduct(Integer userId) {
		return cartMapper.countNonCheckProductByUserId(userId) == 0;
	}
	
	@Override
	public ResponseService<CartVo> add(Integer userId, Integer productId, Integer count) {
		Cart cart = cartMapper.selectByProductIdAndUserId(userId, productId);
		if(cart == null) {
			cart = new Cart();
			cart.setUserId(userId);
			cart.setProductId(productId);
			cart.setQuantity(count);
			cart.setChecked(Constant.CartProPerties.CHECKED);
			int insertCount = cartMapper.insertSelective(cart);
			if(insertCount == 0) {
				return ResponseService.createErrorResponseMessage("新增购物车失败");
			}
		} else {
			cart.setQuantity(count);
			int updateCount = cartMapper.updateQuantity(cart);
			if(updateCount == 0) {
				return ResponseService.createErrorResponseMessage("更新购物车失败");
			}
		}
		return this.listCart(userId);
	}

	@Override
	public ResponseService<CartVo> update(Integer userId, Integer productId, Integer count) {
		Cart cart = cartMapper.selectByProductIdAndUserId(userId, productId);
		if(cart == null) {
			return ResponseService.createErrorResponseMessage("没有对应的购物车产品");
		}
		cart.setQuantity(count);
		int updateCount = cartMapper.updateQuantity(cart);
		if(updateCount == 0) {
			return ResponseService.createErrorResponseMessage("更新购物车失败");
		}
		return this.listCart(userId);
	}

	@Override
	public ResponseService<CartVo> del(Integer userId, String productIds) {
		List<String> productIdList = Splitter.on(",").splitToList(productIds);
		Set<Integer> productIdSet = Sets.newHashSet();
		for(String item : productIdList) {
			productIdSet.add(Integer.valueOf(item));
		}
		cartMapper.deleteByProductIdsAndUserId(userId, productIdSet);
		return this.listCart(userId);
	}

	@Override
	public ResponseService<CartVo> selectOrUnselect(Integer userId, Integer productId, Integer checked) {
		Cart cart = new Cart();
		cart.setUserId(userId);
		cart.setProductId(productId);
		cart.setChecked(checked);
		int count = cartMapper.updateCartSelected(cart);
		if(count == 0) {
			logger.info("操作失败");
		}
		return this.listCart(userId);
	}

	@Override
	public ResponseService<Integer> getCartProductCount(Integer userId) {
		int count = cartMapper.getCartProductCountByUserId(userId);
		return ResponseService.createSuccessResponse(count);
	}

	@Override
	public ResponseService<CartVo> listCart(Integer userId) {
		CartVo vo = getCartVo(userId);
		return ResponseService.createSuccessResponse(vo);
	}

}
