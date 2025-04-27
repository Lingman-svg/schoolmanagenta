package com.school.service.impl;

import cn.hutool.core.util.StrUtil; // 导入 Hutool StrUtil
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.Menu;
import com.school.entity.dto.MenuNode; // 需要创建
import com.school.entity.vo.TreeSelect; // 需要创建
import com.school.mapper.MenuMapper;
import com.school.mapper.RoleMenuMapper; // 需要创建
import com.school.service.MenuService;
import org.springframework.beans.BeanUtils; // 用于属性复制
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单权限表 服务实现类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    // 菜单类型常量
    public static final String TYPE_DIR = "M"; // 目录
    public static final String TYPE_MENU = "C"; // 菜单
    public static final String TYPE_BUTTON = "F"; // 按钮

    @Autowired
    private RoleMenuMapper roleMenuMapper; // 用于检查菜单是否被角色关联

    @Override
    public List<MenuNode> selectMenuTreeByUserId(Long userId) {
        // TODO: 待权限模块实现后完善
        return Collections.emptyList();
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        // TODO: 待权限模块实现后完善
        return Collections.emptySet();
    }

    @Override
    public List<Menu> selectMenuList(Menu menu, Long userId) {
         // 目前只实现查询所有菜单，不考虑用户和具体条件查询
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(menu.getMenuName()), Menu::getMenuName, menu.getMenuName());
        wrapper.eq(menu.getIsValid() != null, Menu::getIsValid, menu.getIsValid());
        // wrapper.eq(menu.getVisible() != null, Menu::getVisible, menu.getVisible()); // 可见性通常在前端处理或构建树时处理
        wrapper.orderByAsc(Menu::getParentId, Menu::getOrderNum);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<MenuNode> buildMenuTree(List<Menu> menus) {
         if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        List<Menu> menuTree = buildTreeStructure(menus);
        return menuTree.stream().map(this::convertToMenuNode).collect(Collectors.toList());
    }

    // 将 Menu 转换为 MenuNode
    private MenuNode convertToMenuNode(Menu menu) {
        MenuNode node = new MenuNode();
        BeanUtils.copyProperties(menu, node); // 复制基础属性
        node.setId(menu.getId()); // Explicitly set ID if BeanUtils misses it
        node.setLabel(menu.getMenuName());
        node.setVisible(String.valueOf(menu.getVisible()));
        node.setStatus(String.valueOf(menu.getIsValid()));
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            node.setChildren(menu.getChildren().stream()
                           .map(this::convertToMenuNode)
                           .collect(Collectors.toList()));
        }
        return node;
    }

     @Override
    public List<TreeSelect> buildMenuTreeSelect(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        List<Menu> menuTree = buildTreeStructure(menus);
        return menuTree.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    // 构建基础的父子结构树 (返回 List<Menu>，包含 children)
    private List<Menu> buildTreeStructure(List<Menu> menus) {
         if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        List<Menu> returnList = new ArrayList<>();
        // Explicitly define the Function for key extraction
        Function<Menu, Long> keyExtractor = Menu::getId;
        Map<Long, Menu> menuMap = menus.stream()
                                      .collect(Collectors.toMap(keyExtractor, Function.identity(), (v1, v2) -> v1));

        for (Menu menu : menus) {
            // 初始化 children 列表，防止 NullPointerException
            if (menu.getChildren() == null) {
                 menu.setChildren(new ArrayList<>());
            }
            // 查找父节点
            Menu parent = menuMap.get(menu.getParentId());
            // Use explicit getId() calls
            if (parent != null && !Objects.equals(menu.getId(), parent.getId())) { // Use Objects.equals for safe comparison
                 // 初始化父节点的 children 列表
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(menu);
            } else if (menu.getParentId() == 0L) { // 如果是根节点，直接添加到结果列表
                returnList.add(menu);
            }
        }
         // 对顶层节点排序
        returnList.sort(Comparator.comparingInt(Menu::getOrderNum));
        // 递归对子节点排序
        sortChildren(returnList);
        return returnList;
    }

    // 递归排序子节点
    private void sortChildren(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            return;
        }
        for (Menu menu : menus) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                menu.getChildren().sort(Comparator.comparingInt(Menu::getOrderNum));
                sortChildren(menu.getChildren());
            }
        }
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        // TODO: 待权限模块实现后完善
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public boolean insertMenu(Menu menu) {
         // 校验父节点是否存在 (如果 parentId 不是 0)
        if (menu.getParentId() != 0L && baseMapper.selectById(menu.getParentId()) == null) {
             throw new RuntimeException("父菜单不存在！"); // 或者自定义异常
        }
        if (!checkMenuNameUnique(menu)) {
            throw new RuntimeException("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在！");
        }
         if (StrUtil.isNotEmpty(menu.getPath()) && !TYPE_BUTTON.equals(menu.getMenuType()) && !checkPathUnique(menu)) {
            throw new RuntimeException("新增菜单'" + menu.getMenuName() + "'失败，路由地址已存在！");
        }
        return this.save(menu);
    }

    @Override
    @Transactional
    public boolean updateMenu(Menu menu) {
         if (menu.getId() == null) {
            throw new RuntimeException("更新失败，菜单ID不能为空！");
        }
        // 校验父节点是否存在 (如果 parentId 不是 0)
        if (menu.getParentId() != null && menu.getParentId() != 0L && baseMapper.selectById(menu.getParentId()) == null) {
             throw new RuntimeException("父菜单不存在！"); // 或者自定义异常
        }
        // 防止将父节点设置为自身或子节点
        if (menu.getId().equals(menu.getParentId())) {
            throw new RuntimeException("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能是自己！");
        }
        // TODO: 添加检查父节点是否为子节点的逻辑 (查询所有子孙节点ID，看是否包含 parentId)

        if (!checkMenuNameUnique(menu)) {
            throw new RuntimeException("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在！");
        }
        if (StrUtil.isNotEmpty(menu.getPath()) && !TYPE_BUTTON.equals(menu.getMenuType()) && !checkPathUnique(menu)) {
            throw new RuntimeException("修改菜单'" + menu.getMenuName() + "'失败，路由地址已存在！");
        }
        return this.updateById(menu);
    }

    @Override
    @Transactional
    public boolean deleteMenuById(Long menuId) {
        if (hasChildByMenuId(menuId)) {
            throw new RuntimeException("存在子菜单,不允许删除！");
        }
        if (checkMenuExistRole(menuId)) {
            throw new RuntimeException("菜单已分配,不允许删除！");
        }
        // 同时需要删除角色菜单关联表中的记录 (外键约束或手动删除)
        // roleMenuMapper.deleteRoleMenuByMenuId(menuId);
        return this.removeById(menuId);
    }

    @Override
    public boolean checkMenuNameUnique(Menu menu) {
         Long menuId = Optional.ofNullable(menu.getId()).orElse(-1L); // Safer way to handle null ID
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getMenuName, menu.getMenuName());
        wrapper.eq(menu.getParentId() != null, Menu::getParentId, menu.getParentId()); // 同一父目录下名称需唯一
        Menu info = baseMapper.selectOne(wrapper);
        return (info == null || Objects.equals(info.getId(), menuId)); // Use Objects.equals
    }

     @Override
    public boolean checkPathUnique(Menu menu) {
        Long menuId = Optional.ofNullable(menu.getId()).orElse(-1L);
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotEmpty(menu.getPath()), Menu::getPath, menu.getPath());
        Menu info = baseMapper.selectOne(wrapper);
        return (info == null || Objects.equals(info.getId(), menuId)); // Use Objects.equals
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, menuId);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        // return roleMenuMapper.checkMenuExistRole(menuId) > 0;
        // TODO: 待权限模块实现后完善
        return false; // 暂时认为不存在关联
    }
} 