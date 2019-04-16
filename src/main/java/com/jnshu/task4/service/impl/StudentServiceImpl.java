package com.jnshu.task4.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jnshu.task4.common.bean.Student;
import com.jnshu.task4.common.utils.RedisUtil;
import com.jnshu.task4.dao.StudentMapper;
import com.jnshu.task4.service.interfaces.IStudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: task4
 * @description:
 * @author: Mr.Chen
 * @create: 2019-02-20 22:50
 * @contact:938738637@qq.com
 **/
@Service
public class StudentServiceImpl implements IStudentService {
    private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    StudentMapper studentMapper;
    @Override
    public Student selectCareerById(Integer id) {
        Student student = studentMapper.queryByPrimaryKey(id);
        return student;
    }

    @Override
    public List<Student> selectAll() {
        if (redisUtil.get("students")==null) {
            List<Student> students = studentMapper.queryAllStudent();
            boolean b = redisUtil.set("students", students);
            if (b==true) {
                logger.info("缓存写入成功");
                return (List<Student>) redisUtil.get("students");
            } else {
                logger.info("缓存写入失败");
                return students;
            }
        }
        logger.info("缓存读取成功");
        return (List<Student>) redisUtil.get("students");
    }

    @Override
    public Integer countByStatus() {
        Integer i = studentMapper.countByStatus();
        return i;
    }

    @Override
    public Integer countAll() {
        Integer i = studentMapper.countAll();
        return i;
    }

    @Override
    public int saveStudent(Student student) throws Exception {
        if (student==null) {
            logger.error("传入学生为空，添加学生信息失败");
            throw new Exception("传入学生为空");
        }
        int i = studentMapper.insert(student);
        return i;
    }

    @Override
    public boolean deleteStudentById(Integer id) throws Exception {
        if (id == null) {
            logger.error("传入学生为空，添加学生信息失败");
            throw new Exception("传入id失败");
        }
        int i = studentMapper.deleteByPrimaryKey(id);
        if (1==i) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStudent(Student student) throws Exception {
        if (student==null) {
            throw new Exception("修改学生信息失败");
        }
        int i = studentMapper.updateByPrimaryKeySelective(student);
        if (1==i) {
            return true;
        }
        return false;
    }

    @Override
    public Student queryByPrimaryKey(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("传入id失败");
        }
        Student student = studentMapper.queryByPrimaryKey(id);
        if (student == null) {
            throw new Exception("查询id为"+id+"的学生信息失败");
        }
        return student;
    }

    @Override
    public PageInfo<Student> queryStudentsByName(String name, int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum,pageSize);
        List<Student> students = studentMapper.queryStudentsByName(name);
        if(students.size()==0) {
            return null;
        }
        return PageInfo.of(students);
    }

    @Override
    public List<Student> queryAllStudent() throws Exception {
        List<Student> students = studentMapper.queryAllStudent();
        if (students == null) {
            throw new Exception("学生表为空");
        }
        return students;
    }

    @Override
    public PageInfo<Student> queryAllStudentsWithPage(int pageNum, int pageSize) throws Exception {
        PageHelper.startPage(pageNum,pageSize);
        List<Student> students = studentMapper.queryAllStudent();
        if (students == null) {
            throw new Exception("学生表为空");
        }
        return PageInfo.of(students);
    }

}
