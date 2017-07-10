package com.dmall.service;

import com.dmall.common.ServerResponse;
import com.dmall.vo.CartVo;

public interface ICartService {

    ServerResponse<CartVo> addToCart(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> getCartList(Integer userId);

    ServerResponse<CartVo> updateCartProductCount(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteCartProducts(Integer userId, String productIds);

    ServerResponse<CartVo> selectOrUnSelectCartProduct(Integer userId, Integer productId, Integer checked);

    ServerResponse<Integer> getCartProductsCount(Integer userId);
}
