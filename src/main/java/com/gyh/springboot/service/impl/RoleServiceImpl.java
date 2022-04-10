package com.gyh.springboot.service.impl;

import com.gyh.springboot.entity.Role;
import com.gyh.springboot.mapper.RoleMapper;
import com.gyh.springboot.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 甘延辉
 * @since 2022-04-08
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
