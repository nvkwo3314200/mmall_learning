package com.mmall.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Cart;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

	List<Cart> getCartByUserId(Integer userId);

	int countNonCheckProductByUserId(Integer userId);

	Cart selectByProductIdAndUserId(@Param("userId") Integer userId, @Param("productId") Integer productId);

	int deleteByProductIdsAndUserId(@Param("userId") Integer userId, @Param("productIdSet") Set<Integer> productIdSet);

	int updateCartSelected(Cart cart);

	int getCartProductCountByUserId(Integer userId);

	int updateQuantity(Cart record);

	List<Cart> getCheckedCartByUserId(Integer userId);
}