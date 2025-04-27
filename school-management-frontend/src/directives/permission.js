import { useUserStore } from '@/stores/user';

/**
 * 检查用户是否拥有指定权限
 * @param {string | string[]} value - 需要校验的权限标识 (单个字符串或字符串数组)
 * @returns {boolean}
 */
function checkPermission(value) {
    if (!value) {
        return false; // 如果没有指定权限要求，默认不允许
    }
    const userStore = useUserStore();
    const permissions = userStore.userInfo?.permissions || []; // 获取用户权限列表

    // 管理员拥有所有权限
    if (permissions.includes('*:*:*')) {
        return true;
    }

    const permissionValue = typeof value === 'string' ? [value] : value; // 统一处理为数组

    // 检查用户权限是否包含所需的权限
    return permissions.some(perm => permissionValue.includes(perm));
}

export default {
    // 指令挂载时
    mounted(el, binding) {
        const { value } = binding;
        const hasPerm = checkPermission(value);

        if (!hasPerm) {
            // 如果没有权限，移除元素或使其禁用 (这里选择移除)
            el.parentNode?.removeChild(el);
            // 或者禁用：
            // el.disabled = true;
            // el.style.pointerEvents = 'none'; // 防止点击
            // el.style.opacity = '0.5'; // 可视化提示
        }
    },
    // 权限可能会动态变化，例如切换用户后 (可选)
    updated(el, binding) {
        const { value } = binding;
        const hasPerm = checkPermission(value);
        // 如果元素之前被移除，需要处理重新插入的情况，逻辑较复杂
        // 简单处理：如果原本有权限，现在没有了，则移除
        // 如果原本没有权限，现在有了，可能需要特殊处理才能显示 (因为元素可能已被移除)
        // 暂时只处理从有权限变为无权限的情况
        if (!hasPerm && el.parentNode) { 
            el.parentNode.removeChild(el);
        }
        // 如果需要更完善的动态更新，可能需要其他方式或重新渲染组件
    }
}; 