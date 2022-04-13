package com.gyh.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gyh.springboot.entity.Menu;
import com.gyh.springboot.entity.Role;
import com.gyh.springboot.entity.RoleMenu;
import com.gyh.springboot.mapper.RoleMapper;
import com.gyh.springboot.mapper.RoleMenuMapper;
import com.gyh.springboot.service.IMenuService;
import com.gyh.springboot.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Transactional
    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
//        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("role_id", roleId);
//        roleMenuMapper.delete(queryWrapper);

        //先删除当前角色id所有的绑定关系
        roleMenuMapper.deleteByRoleId(roleId);

        //再把前端传过来的菜单id数组绑定到当前的这个角色id上去
        List<Integer> menuIdsCopy = CollUtil.newArrayList(menuIds);//最后实际上插入了menuIdsCopy中所有的数据
        for(Integer menuId : menuIds){
            Menu menu = menuService.getById(menuId);
            if(menu.getPid() != null && !menuIdsCopy.contains(menu.getPid())){// 二级菜单 && 传过来的menuId数组里面没有它的父级id
                //那么我们就得补上这个父级id
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menu.getPid());
                roleMenuMapper.insert(roleMenu);
                menuIdsCopy.add(menu.getPid());//把插入的父级id记录下来，防止重复插入
            }

            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }

    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {
        return roleMenuMapper.selectByRoleId(roleId);
    }
}
