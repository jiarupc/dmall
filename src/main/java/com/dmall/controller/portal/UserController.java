package com.dmall.controller.portal;

import com.dmall.common.Const;
import com.dmall.common.ResponseCode;
import com.dmall.common.ServerResponse;
import com.dmall.pojo.User;
import com.dmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
    @PostMapping("/login")
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }

        return response;
    }

    /**
     * 登出
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMessage("已退出登陆");
    }

    /**
     * 注册
     * @param user include : username、password、email、phone、question、answer
     * @return
     */
    @PostMapping("/register")
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 验证 邮箱 && 用户名
     * @param str
     * @param type
     * @return
     */
    @GetMapping("/valid")
    public ServerResponse<String> checkValid(@RequestParam("str") String str,
                                             @RequestParam("type") String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取 Session 数据
     * @param session
     * @return
     */
    @GetMapping("/session")
    public ServerResponse<User> getUserSession(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("未登陆，无法获取用户信息");
    }

    /**
     * 忘记密码问题
     * @param session
     * @return
     */
    @GetMapping("/forgot/question")
    public ServerResponse<String> getQuestion(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return iUserService.selectQuestion(currentUser.getUsername());
    }

    /**
     * 问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @PostMapping("/forgot/answer")
    public ServerResponse<String> getAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 重置密码
     * @param username
     * @param newPassword
     * @param forgotToken
     * @return
     */
    @PostMapping("/forgot/password")
    public ServerResponse<String> forgotResetPassword(String username, String newPassword, String forgotToken) {
        return iUserService.forgotResetPassword(username, newPassword, forgotToken);
    }

    /**
     * 修改密码
     * @param oldPassword
     * @param newPassword
     * @param session
     * @return
     */
    @PostMapping("/update/password")
    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iUserService.resetPassword(oldPassword, newPassword, user);
    }

    /**
     * 更新用户信息
     * @param user include : email、phone、question、answer
     * @param session
     * @return
     */
    @PostMapping("/update/information")
    public ServerResponse<User> updateInformation(User user, HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }

        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @GetMapping("/information")
    public ServerResponse<User> getUserInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}