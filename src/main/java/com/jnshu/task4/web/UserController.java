package com.jnshu.task4.web;

import com.alibaba.fastjson.JSONObject;
import com.jnshu.task4.common.bean.User;
import com.jnshu.task4.common.utils.*;
import com.jnshu.task4.common.utils.storage.QiniuStorage;
import com.jnshu.task4.service.interfaces.IUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @program: task5
 * @description:用户的注册，登录与注销
 * @author: Mr.Chen
 * @create: 2019-02-27 20:36
 * @contact:938738637@qq.com
 **/
@Controller
public class UserController {
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired
    IUserService userService;

    //检查用户名合法性
    @RequestMapping(value = "u/checkUserName/{username}")
    @ResponseBody
    public Object checkUserName(@PathVariable String username){
            User user = userService.queryUserByName(username);
            logger.info(user);
            if(user == null) {
                return ResponseResult.ok();
            }
            return ResponseResult.build(500, "fail");
    }

    //  -----------登录------------------
    @RequestMapping("a/u/gologin")
    public String goLogin(){
        return "login";
    }
    //  -----------注册------------------
    @RequestMapping("a/u/goreg")
    public String goReg(){
        return "reg";
    }


    //@Description: 用户注册,根据邮箱
    @PostMapping("a/u/reg/email")
    public String reg(User user, Model model) {
        String username = user.getUsername();
        String password = user.getPassword();
        String message = null;

        // 判定输入用户名或者密码是否为空,若其中一项为空，则返回注册页面
        if(username.isEmpty() || password.isEmpty()){
            logger.info("用户名或密码为空");
            message = "用户名或者密码不能为空";
            model.addAttribute("msg",message);
            return "reg";
        }
        User user1 = userService.queryUserByName(username);
        // 判定输入的用户名是否已存在
        if (user1!=null) {
            logger.info("用户名"+username+"已存在");
            message = "用户名已存在";
            model.addAttribute("msg",message);
            return "reg";
        }
        //将uuid作为salt
        String salt = UUIDUtil.getUUID();
        user.setUuid(salt);
        String pwd = MD5Util.md5(salt + password);
        user.setPassword(pwd);
        long l = System.currentTimeMillis();
        user.setCreateAt(l);
        user.setUpdateAt(l);
        userService.insertUser(user);
        logger.info("注册成功");
        logger.info(user);
        return "redirect:/a/u/gologin";
    }


    //token认证
    @PostMapping("a/u/login")
    public String login(String username, String password, HttpSession session, Model model, HttpServletResponse response) {
        String message = null;
        // 判定输入用户名或者密码是否为空，若其中一项为空，则返回登录页面
        if(username.isEmpty() || password.isEmpty()){
            logger.info("用户名或者密码为空");
            message = "用户名或者密码不能为空";
            model.addAttribute("msg",message);
            return "login";
        }
        User user = userService.queryUserByName(username);
        String salt = user.getUuid();
        String pwd = MD5Util.md5(salt + password);
        if (user == null || !pwd.equals(user.getPassword())) {
            logger.info("用户名或密码错误");
            message = "用户名或密码错误,请重新输入";
            return "login";
        }
        try {
            logger.info("登陆成功");
            JwtUtils jwtUtils = new JwtUtils();
            // 1小时后 过期
            long maxTime = 30*60L*1000L;
            String token = jwtUtils.getToken(username, maxTime);
            Cookie cookie = new Cookie("token", token);
            logger.info("生成token认证:"+token);
            // 过期时间：1小时
            cookie.setMaxAge(60*60);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 30分钟过期
        session.setMaxInactiveInterval(30*60);
        session.setAttribute("username",username);
        return "redirect:/a/u/home";
    }
    //退出登录
    @GetMapping("/u/loginOut")
    public String loginOut( HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        // 删除session
        session.invalidate();
        Cookie[] cookies = request.getCookies();
        // setMaxAge(0)------删除Cookie失效----------默认 setMaxAge(-1) ,关闭浏览器删除Cookie
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName()) || "JSESSIONID".equals(cookie.getName())) {
                //cookie.setValue("");
                cookie.setValue(null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        logger.info("退出登录");
        return "redirect:/a/u/home";
    }

    //获取手机验证码
    @PostMapping(value = "/a/u/sendPhoneCode")
    @ResponseBody
    public Object sendPhoneCode( String phone,HttpSession session) {
        logger.info("手机号:"+phone);
        //后台验证手机号
        if (phone.isEmpty()) {
            logger.info("手机号为空");
            return ResponseResult.build(500,"手机号为空");
        }
        JSONObject json = new JSONObject();
        //随机字符串
        String s = NumberUtil.randomNo(6);
        logger.info(s);
        //发送验证码,message为true或false
        String message = MessageUtil.sendMessage(phone, s);
//        String message = "true";
        logger.info(message);
        //将验证码存到session中,同时存入创建时间
        //以json存放，这里使用的是阿里的fastjson
        json.put("verifyCode",s);
        json.put("createTime",System.currentTimeMillis());
        System.out.println(json.toString());
        session.setAttribute("verifyCode",json);
        //ajax根究后台传过来的message判断验证码是否发送成功
        return ResponseResult.build(200,message);
    }

    //根据手机注册
    @PostMapping("a/u/regPhone")
    @ResponseBody
    public Object regByPhone(User user, String code, HttpServletRequest request) {
        System.out.println(user);
        JSONObject json = (JSONObject) request.getSession().getAttribute("verifyCode");
        String phone = user.getPhone();
        String password = user.getPassword();
        String username = user.getUsername();
        // 后端判定输入用户名或者密码和验证码是否为空,若其中一项为空，则返回注册页面
        //后端验证
        if(username.isEmpty() || password.isEmpty()){
            logger.info("用户信息为空");
            return ResponseResult.build(401,"用户名或密码为空");
        }
        if (code.isEmpty()){
            logger.info("验证码为空");
            return ResponseResult.build(402,"验证码为空");
        }
        // 判定输入的用户名是否已被注册
        User user1 = userService.queryUserByName(username);
        if (user1!=null) {
            logger.info("该用户已被注册");
            return ResponseResult.build(403,"该用户已被注册");
        }
        System.out.println(code);
        System.out.println(json.getString("verifyCode"));
        System.out.println("验证码校验"+json.getString("verifyCode").equals(code));
        //检查验证码是否正确
        if(json.getString("verifyCode").equals(code)==false){
            return ResponseResult.build(404,"验证码错误");
        }
        if((System.currentTimeMillis() - json.getLong("createTime")) > 1000 * 60 * 1){
            return ResponseResult.build(405,"验证码过期");
        }
        //将uuid作为salt
        String salt = UUIDUtil.getUUID();
        user.setUuid(salt);
        String pwd = MD5Util.md5(salt + password);
        user.setPassword(pwd);
        long l = System.currentTimeMillis();
        user.setCreateAt(l);
        user.setUpdateAt(l);
        userService.insertUser(user);
        logger.info("注册成功");
        logger.info(user);
        return ResponseResult.build(200,"注册成功");
    }

    //发送邮箱验证码
    @PostMapping(value = "/a/u/sendEmailCode")
    @ResponseBody
    public Object sendEmailCode( String email,HttpSession session,Long time) throws IOException {
        logger.info("邮箱:"+email);
        //验证验证码是否在60s之内
        JSONObject json = (JSONObject) session.getAttribute("verifyCodeEmail");
        logger.info(json);
        if (json!=null) {
            if (time-json.getLong("createTime")>60*1000) {
                logger.info("验证码有效期已过,可重新发送");
//                String s = NumberUtil.randomNo(6);
                String s = "123456";
//                String message = SendCloud.sendCommon(email, s);
                String message = "true";
                json.put("verifyCode",s);
                json.put("createTime",System.currentTimeMillis());
                session.setAttribute("verifyCodeEmail",json);
                return ResponseResult.build(200,message);
            }
            logger.info("一分钟后可重新发送验证码");
            return ResponseResult.build(400,"一分钟后可重新发送验证码");
        }
        //后台验证邮箱
        if (email.isEmpty()) {
            logger.info("邮箱为空");
            return ResponseResult.build(500,"邮箱为空");
        }
        json = new JSONObject();
        //随机字符串
//        String s = NumberUtil.randomNo(6);
        String s = "123456";
        logger.info(s);
//        String message = SendCloud.sendCommon(email, s);
        String message = "true";
        logger.info(message);
        //将验证码存到session中,同时存入创建时间
        //以json存放，这里使用的是阿里的fastjson
        json.put("verifyCode",s);
        json.put("createTime",System.currentTimeMillis());
        System.out.println(json.toString());
        session.setAttribute("verifyCodeEmail",json);
        //ajax根究后台传过来的message判断验证码是否发送成功
        return ResponseResult.build(200,message);
    }

    //根据邮箱注册
    @PostMapping("a/u/regEmail")
    @ResponseBody
    public Object regByEmail(User user, String code, HttpServletRequest request) {
        logger.info("验证码:"+code);
        JSONObject json = (JSONObject) request.getSession().getAttribute("verifyCodeEmail");
        if (json==null) {
            logger.info("json为空");
            return ResponseResult.build(402,"验证码为空");
        }
        String email = user.getEmail();
        String password = user.getPassword();
        String username = user.getUsername();
        // 后端判定输入用户名或者密码和验证码是否为空,若其中一项为空，则返回注册页面
        //后端验证
        if(username.isEmpty() || password.isEmpty()){
            logger.info("用户信息为空");
            return ResponseResult.build(401,"用户名或密码为空");
        }
        if (email.isEmpty()) {
            logger.info("邮箱为空");
            return ResponseResult.build(406,"邮箱为空");
        }
        if (code.isEmpty()){
            logger.info("验证码为空");
            return ResponseResult.build(402,"验证码为空");
        }
        //检查验证码是否正确
        if(json.getString("verifyCode").equals(code)==false){
            return ResponseResult.build(404,"验证码错误");
        }
        if((System.currentTimeMillis() - json.getLong("createTime")) > 1000 * 60 * 1){
            return ResponseResult.build(405,"验证码过期");
        }
        // 判定输入的用户名是否已被注册
        User user1 = userService.queryUserByName(username);
        System.out.println("shasdsa"+user1);
        if (user1!=null) {
            logger.info("该用户已被注册");
            return ResponseResult.build(403,"该用户已被注册");
        }
        //将uuid作为salt
        String salt = UUIDUtil.getUUID();
        logger.info(salt);
        user.setUuid(salt);
        String pwd = MD5Util.md5(salt + password);
        user.setPassword(pwd);
        long l = System.currentTimeMillis();
        user.setCreateAt(l);
        user.setUpdateAt(l);
        userService.insertUser(user);
        logger.info("注册成功");
        logger.info(user);
        return ResponseResult.build(200,"注册成功");
    }

    //用户个人信息
    @RequestMapping("u/userInfo/{username}")
    public String userInfo(@PathVariable String username,Model model) {
//        String username = (String) session.getAttribute("username");
        if (username==null) {
            return "404";
        }
        User user = userService.queryUserByName(username);
//        logger.info(user);
        if (null!=user&& StringUtils.isNotEmpty(user.getHead_img())) {
            user.setHead_img(QiniuStorage.getUrl(user.getHead_img()));
        }
        model.addAttribute("user",user);
        return "userInfo";
    }
    @RequestMapping(value = "u/upload")
    public String updateUser(User user, @RequestParam("pictureImg") MultipartFile pictureImg, Model model) {
        try {
            System.out.println(pictureImg);
            if (null != pictureImg && pictureImg.getBytes().length > 0) {
                String key = QiniuStorage.uploadImage(pictureImg.getBytes());
                logger.info("key:"+key);
                user.setHead_img(key);
            }
            Boolean b = userService.updateStudent(user);
            User user1 = userService.queryUserByName(user.getUsername());
            user1.setHead_img(QiniuStorage.getUrl(user.getHead_img()));
            model.addAttribute("user",user1);
            if (b) {
                return "userInfo";
            }
            return "404";
        } catch (IOException e) {
            e.printStackTrace();
            return "404";
        }
    }
    //更换头像

}
