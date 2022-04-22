package com.gyh.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.springboot.controller.dto.UserDTO;
import com.gyh.springboot.controller.dto.UserPasswordDTO;
import com.gyh.springboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 甘延辉
 * @since 2022-03-29
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    Page<User> findPage(Page<User> page, String username, String email, String address);

}
