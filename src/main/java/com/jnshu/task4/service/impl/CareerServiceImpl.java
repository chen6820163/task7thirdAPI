package com.jnshu.task4.service.impl;

import com.jnshu.task4.common.bean.Career;
import com.jnshu.task4.dao.CareerMapper;
import com.jnshu.task4.service.interfaces.ICareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: task4
 * @description:
 * @author: Mr.Chen
 * @create: 2019-02-20 17:59
 * @contact:938738637@qq.com
 **/
@Service
public class CareerServiceImpl implements ICareerService {
    @Autowired
    CareerMapper careerMapper;
    @Override
    public Career selectCareerById(Integer id) {
        Career career = careerMapper.selectByPrimaryKey(id);
        return career;
    }

    @Override
    public List<Career> selectALl() {
        List<Career> careers = careerMapper.selectAll();
        return careers;
    }
}
