package com.dmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.dmall.common.Const;
import com.dmall.common.ResponseCode;
import com.dmall.common.ServerResponse;
import com.dmall.pojo.User;
import com.dmall.service.IOrderService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @GetMapping("/create")
    public ServerResponse createOrder(Integer shippingId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    @GetMapping("/list")
    public ServerResponse getOrderList(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                       HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }

    @GetMapping("/cart/product")
    public ServerResponse getOrderCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @GetMapping("/detail")
    public ServerResponse getOrderDetail(Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @GetMapping("/cancel")
    public ServerResponse cancelOrder(Long orderNo,HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        return iOrderService.cancelOrder(user.getId(), orderNo);
    }

    @GetMapping("/alipay/pay")
    public ServerResponse pay(Long orderNo, HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登录");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(), orderNo, path);
    }

    @PostMapping("/alipay/callback")
    public Object alipayCallback(HttpServletRequest request) {
        System.out.println("--------------------------------------------------------------------");
        Map<String, String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";

            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        logger.info(
                "支付宝回调，sign:{}, trade_status:{}, 参数:{}",
                params.get("sign"), params.get("trade_status"), params.toString());

        params.remove("sign_type");
        try {
            boolean alipayRsaCheckedV2 = AlipaySignature.rsaCheckV2(
                    params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());

            if (!alipayRsaCheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求！！！验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常", e);
        }

        ServerResponse serverResponse = iOrderService.alipayCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }

        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @GetMapping("/alipay/pay/status")
    public ServerResponse<Boolean> queryOrderPayStatus(Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}