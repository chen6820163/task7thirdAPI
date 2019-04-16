package com.jnshu.task4.service.impl;

import com.jnshu.task4.common.bean.User;
import com.jnshu.task4.common.utils.RedisUtil;
import com.jnshu.task4.dao.UserMapper;
import com.jnshu.task4.service.interfaces.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: task4
 * @description:
 * @author: Mr.Chen
 * @create: 2019-02-27 20:14
 * @contact:938738637@qq.com
 **/
@Service
public class UserServiceImpl implements IUserService {
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserMapper userMapper;
    @Override
    public Long insertUser(User user) {
        Long id = userMapper.insert(user);
        return id;
    }

    @Override
    public Boolean deleteUserById(Long id) {
        boolean flag = false;
        int i = userMapper.deleteByPrimaryKey(id);
        if (i != 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public Boolean updateStudent(User user) {
        logger.info("UserServiceImpl:"+user);
        boolean flag = false;
        int i = userMapper.updateByPrimaryKeySelective(user);
        if (i != 0) {
            flag = true;
            redisUtil.delete(user.getUsername());
        }
        return flag;
    }

    @Override
    public User queryUserByName(String userName) {
        //判断key是否存在
        if (!redisUtil.hasKey(userName)) {
            User user = userMapper.selectByName(userName);
            if (user==null) {
                //防止请求直接穿透到数据库
                redisUtil.set(userName,null,600);
                return null;
            }
            boolean b = redisUtil.set(userName, user);
            if (b==true) {
                logger.info("缓存写入成功");
                return user;
            } else {
                logger.info("缓存写入失败");
                return user;
            }
        }
        logger.info("缓存读取成功");
        return (User) redisUtil.get(userName);
//        User user = userMapper.selectByName(userName);
//        return user;
    }

    @Override
    public User queryUserById(Long id) {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    public List<User> queryAllUsers() {
        List<User> users = userMapper.queryAllUsers();
        return users;
    }
    //根据手机号查询用户信息
    @Override
    public User queryByPhone(String phone) {
        User user = userMapper.selectByPhone(phone);
        return user;
    }
}
