package com.gyh.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.springboot.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 甘延辉
 * @since 2022-04-18
 */
public interface ICourseService extends IService<Course> {

    Page<Course> findPage(Page<Course> page, String name);

    void setStudentCourse(Integer courseId, Integer studentId);
}
