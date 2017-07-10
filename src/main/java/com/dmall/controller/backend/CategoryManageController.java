package com.dmall.controller.backend;

import com.dmall.common.Const;
import com.dmall.common.ResponseCode;
import com.dmall.common.ServerResponse;
import com.dmall.pojo.User;
import com.dmall.service.ICategoryService;
import com.dmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manager/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @PostMapping("/add/category")
    public ServerResponse addCategory(String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") Integer parentId,
                                      HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
//        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @PostMapping("/set/category")
    public ServerResponse setCategoryName(String categoryName, Integer categoryId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
//        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iCategoryService.updateCategoryName(categoryName, categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    @GetMapping("/children/category/{categoryId}")
    public ServerResponse getChildrenCategory(@PathVariable("categoryId") Integer categoryId,
                                              HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
//        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
//           查询 category 子节点，保持平级，不递归
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @GetMapping("/deep/category/{categoryId}")
    public ServerResponse getCategoryAndDeepChildrenCategory(@PathVariable("categoryId") Integer categoryId,
                                                             HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
//        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
//            查询当前节点 id 和 递归子节点 id
//            0 --> 1000 --> 10000
            return iCategoryService.selectCategoryAndDeepChildrenCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
