package com.gyh.springboot.service;

import com.gyh.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 甘延辉
 * @since 2022-04-10
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
