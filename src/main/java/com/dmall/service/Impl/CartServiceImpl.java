package com.dmall.service.Impl;

import com.dmall.common.Const;
import com.dmall.common.ResponseCode;
import com.dmall.common.ServerResponse;
import com.dmall.dao.CartMapper;
import com.dmall.dao.ProductMapper;
import com.dmall.pojo.Cart;
import com.dmall.pojo.Product;
import com.dmall.service.ICartService;
import com.dmall.utils.BigDecimalUtil;
import com.dmall.utils.PropertiesUtil;
import com.dmall.vo.CartProductVo;
import com.dmall.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService{

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;

    @Override
    public ServerResponse<CartVo> addToCart(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
//            产品不在购物车，新增产品
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(cartItem);
        } else {
//            产品已经在购物车，数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.getCartList(userId);
    }

    @Override
    public ServerResponse<CartVo> getCartList(Integer userId) {
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> updateCartProductCount(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.getCartList(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteCartProducts(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId, productList);
        return this.getCartList(userId);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelectCartProduct(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUnCheckedProduct(userId, productId, checked);
        return this.getCartList(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductsCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductsCount(userId));
    }

    /**
     * 拼装 CartVo ，填入 cartProductVo
     * @param userId
     * @return
     */
    private CartVo getCartVo(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
//                第 1 次拼装 cartProductVo ， 属性：购物车Id、用户Id、产品Id
                CartProductVo cartProductVo = assembleCartProductVoByKeys(cartItem.getId(), userId, cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());

                if (product != null) {
//                    第 2 次拼装 cartProductVo。
//                    属性:productName、subTitle、productPrice、productStock、productMainImage、productStatus
                    cartProductVo = assembleCartProductVoByProductAndOrigin(product, cartProductVo);

//                    判断库存，获取最大购买数
                    int buyLimitCount = checkBuyLimit(product, cartItem, cartProductVo);
                    cartProductVo.setQuantity(buyLimitCount);

//                    计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked()); // 设置勾选状态
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) {
//                    如果已经勾选，加到购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }

//        拼装 CartVo
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private int checkBuyLimit(Product product, Cart cartItem, CartProductVo cartProductVo) {
//        库存够，照常购买。不够，买最大库存。
        if (product.getStock() >= cartItem.getQuantity()) {
            cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
            return cartItem.getQuantity();
        } else {
            cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);

            Cart cartForQuantity = new Cart();
            cartForQuantity.setId(cartItem.getId());
            cartForQuantity.setQuantity(product.getStock());
            cartMapper.updateByPrimaryKeySelective(cartForQuantity);

            return product.getStock();
        }
    }

    /**
     * 检查购物车商品是否全部勾选
     * @param userId
     * @return
     */
    private Boolean getAllCheckedStatus(Integer userId) {
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0 && userId != null;
    }

    /**
     * 重构代码块
     * 目的： 简化 this.getCartVo 方法中的属性 Setter
     * 结果： 略微改善
     * @param Id
     * @param userId
     * @param productId
     * @return
     */
    private CartProductVo assembleCartProductVoByKeys(Integer Id, Integer userId, Integer productId) {
        CartProductVo cartProductVo = new CartProductVo();
        cartProductVo.setId(Id);
        cartProductVo.setUserId(userId);
        cartProductVo.setProductId(productId);
        return cartProductVo;
    }

    private CartProductVo assembleCartProductVoByProductAndOrigin(Product product, CartProductVo origin) {
        CartProductVo cartProductVo = assembleCartProductVoByKeys(origin.getId(), origin.getUserId(), origin.getProductId());
        cartProductVo.setProductName(product.getName());
        cartProductVo.setProductSubTitle(product.getSubTitle());
        cartProductVo.setProductPrice(product.getPrice());
        cartProductVo.setProductStock(product.getStock());
        cartProductVo.setProductMainImage(product.getMainImage());
        cartProductVo.setProductStatus(product.getStatus());
        return cartProductVo;
    }

}
