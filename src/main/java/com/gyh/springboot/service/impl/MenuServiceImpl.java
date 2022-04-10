package com.gyh.springboot.service.impl;

import com.gyh.springboot.entity.Menu;
import com.gyh.springboot.mapper.MenuMapper;
import com.gyh.springboot.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 甘延辉
 * @since 2022-04-10
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

}
