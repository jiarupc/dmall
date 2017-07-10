package com.dmall.controller.backend;

import com.dmall.common.Const;
import com.dmall.common.ResponseCode;
import com.dmall.common.ServerResponse;
import com.dmall.pojo.User;
import com.dmall.service.IOrderService;
import com.dmall.service.IUserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manager/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @GetMapping("/list")
    public ServerResponse<PageInfo> getOrderList(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                 HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageOrderList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @GetMapping("/detail")
    public ServerResponse getOrderDetail(Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageOrderDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @GetMapping("/search")
    public ServerResponse searchOrder(Long orderNo,
                                      @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                      HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSearchOrder(orderNo, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @GetMapping("/send/goods")
    public ServerResponse sendOrderGoods(Long orderNo, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSendOrderGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}