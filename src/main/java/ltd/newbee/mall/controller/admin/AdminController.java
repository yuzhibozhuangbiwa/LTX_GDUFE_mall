/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.controller.admin;

import cn.hutool.captcha.ShearCaptcha;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 */

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminUserService adminUserService;

    @GetMapping("/login")
    public String login() { return "admin/login";}

    @GetMapping("/profile")
    public String profile(HttpServletRequest request){

        Integer id = (int) request.getSession().getAttribute("Id");
        System.out.println(id);
        AdminUser userDetailById = adminUserService.getUserDetailById(id);
        if(userDetailById==null) {
            return "admin/login";
        }


            request.setAttribute("path", "profile");
            request.setAttribute("nickName", userDetailById.getNickName());
            request.setAttribute("loginUserName",userDetailById.getLoginUserName());
            return "admin/profile";
    }

    @PostMapping("/profile/password")
    @ResponseBody
    public String editpassword(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                               @RequestParam("newPassword") String newPassword){
        if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)) {
            return "参数不能为空";
        }
        Integer id = (int)request.getSession().getAttribute("Id");
        if(adminUserService.updatePassword(id,originalPassword,newPassword)){
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return ServiceResultEnum.SUCCESS.getResult();
        }else {
            return "修改失败";
        }
    }

    @PostMapping("/profile/name")
    @ResponseBody
    public String editname(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
                           @RequestParam("nickName") String nickName){
        if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
            return "参数不能为空";
        }

        Integer id = (int) request.getSession().getAttribute("Id");

        if (adminUserService.updateName(id,loginUserName,nickName)){

            return ServiceResultEnum.SUCCESS.getResult();
        }else {
            return "修改失败";
        }
    }

    @GetMapping({"", "/", "/index", "/index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("path", "index");
        return "admin/index";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }
    @GetMapping({"/test"})
    public String test() {
        return "admin/test";
    }


    @PostMapping("/login")
    public String postlogin(@RequestParam String userName,@RequestParam String password,
                            @RequestParam("verifyCode") String verifyCode,
                            HttpSession session){
        if (StringUtils.isEmpty(verifyCode)) {
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            session.setAttribute("errorMsg", "用户名或密码不能为空");
            return "admin/login";
        }
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("verifyCode");
        if (shearCaptcha == null || !shearCaptcha.verify(verifyCode)) {
            session.setAttribute("errorMsg", "验证码错误");
            return "admin/login";
        }

        AdminUser login = adminUserService.login(userName, password);
        if(login !=null) {
            Integer Id = login.getAdminUserId();
            session.setAttribute("Id",Id);
            session.setAttribute("loginUser", login.getNickName());
            session.setAttribute("loginUserId", login.getAdminUserId());
            return "redirect:/admin/index";
        }else {
            session.setAttribute("errorMsg","登录失败");
            return "admin/login";
        }
    }




}
