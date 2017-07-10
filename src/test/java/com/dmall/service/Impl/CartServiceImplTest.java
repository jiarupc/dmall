package com.dmall.service.Impl;

import com.dmall.pojo.Product;
import com.dmall.vo.CartProductVo;
import org.junit.Test;

public class CartServiceImplTest {

    public CartProductVo assembleCartProductKeys(Integer Id, Integer userId, Integer productId) {
        CartProductVo cartProductVo = new CartProductVo();
        cartProductVo.setId(Id);
        cartProductVo.setUserId(userId);
        cartProductVo.setProductId(productId);
        return cartProductVo;
    }

    private CartProductVo assembleCartProductVoByProduct(Product product) {
        CartProductVo cartProductVo = new CartProductVo();
        cartProductVo.setProductName(product.getName());
        cartProductVo.setProductSubTitle(product.getSubTitle());
        cartProductVo.setProductMainImage(product.getMainImage());
        cartProductVo.setProductPrice(product.getPrice());
        cartProductVo.setProductStatus(product.getStatus());
        cartProductVo.setProductStock(product.getStock());
        return cartProductVo;
    }

    @Test
    public void testAssembleCartVo() {
        CartProductVo cartProductVo = assembleCartProductKeys(1, 1 ,1);
        Product product = new Product();
        cartProductVo = assembleCartProductVoByProduct(product);
        System.out.println(cartProductVo.toString());
    }
}
