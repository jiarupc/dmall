package com.dmall.controller.portal;

import com.dmall.common.Const;
import com.dmall.common.ResponseCode;
import com.dmall.common.ServerResponse;
import com.dmall.pojo.Cart;
import com.dmall.pojo.User;
import com.dmall.service.ICartService;
import com.dmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @GetMapping("/add")
    public ServerResponse<CartVo> addToCart(@RequestParam("productId") Integer productId,
                                            @RequestParam("count") Integer count,
                                            HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登录");
        }

        return iCartService.addToCart(user.getId(), productId, count);
    }

    @GetMapping("/list")
    public ServerResponse<CartVo> getCartList(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        return iCartService.getCartList(user.getId());
    }

    @GetMapping("/update")
    public ServerResponse<CartVo> updateCart(@RequestParam("productId") Integer productId,
                                             @RequestParam("count") Integer count,
                                             HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        return iCartService.updateCartProductCount(user.getId(), productId, count);
    }

    @GetMapping("/delete")
    public ServerResponse<CartVo> deleteCartProducts(String productIds,
                                                     HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        return iCartService.deleteCartProducts(user.getId(), productIds);
    }

    /**
     * 前端简易功能块 ：
     * 1、全选 2、全反选 3、单选 4、单反选 5、获取商品数量
     * @param session
     * @return
     */
    @GetMapping("/select/all")
    public ServerResponse<CartVo> selectAllCartProducts(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        return iCartService.selectOrUnSelectCartProduct(user.getId(), null, Const.Cart.CHECKED);
    }

    @GetMapping("/unselect/all")
    public ServerResponse<CartVo> unSelectAllCartProducts(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        return iCartService.selectOrUnSelectCartProduct(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    @GetMapping("/select")
    public ServerResponse<CartVo> selectCartProduct(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        return iCartService.selectOrUnSelectCartProduct(user.getId(), productId, Const.Cart.CHECKED);
    }

    @GetMapping("/unselect")
    public ServerResponse<CartVo> unSelectCartProduct(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        return iCartService.selectOrUnSelectCartProduct(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @GetMapping("/product/count")
    public ServerResponse<Integer> getCartProductsCount(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        return iCartService.getCartProductsCount(user.getId());
    }
}