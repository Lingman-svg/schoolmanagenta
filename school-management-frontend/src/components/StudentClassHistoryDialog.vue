<!--
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 02:32:06
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 02:37:14
 * @FilePath: \schoolmanagenta\school-management-frontend\src\components\StudentClassHistoryDialog.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <el-dialog
    :title="`${studentName || '学生'} - 班级变更历史`"
    :model-value="visible"
    width="1200px"
    @close="handleClose"
    :close-on-click-modal="false"
  >
    <el-table :data="historyList" border stripe v-loading="loading" height="400px">
      <el-table-column prop="id" label="记录ID" width="80" align="center" />
      <!-- <el-table-column prop="clazzId" label="班级ID" width="100" align="center" /> --> <!-- Optionally hide class ID -->
      <el-table-column prop="className" label="班级名称" min-width="150">
          <template #default="{ row }">
             {{ row.className || '未知班级' }} <!-- Display className directly -->
          </template>
      </el-table-column>
      <el-table-column prop="assignDate" label="分配时间" width="180" align="center">
        <template #default="{ row }">
          {{ formatDateTime(row.assignDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="removeDate" label="移除时间" width="180" align="center">
         <template #default="{ row }">
          {{ formatDateTime(row.removeDate) || '至今' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="记录创建时间" width="180" align="center">
         <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
       <!-- Add other relevant columns if needed -->
    </el-table>
    <template #footer>
      <el-button @click="handleClose">关 闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'; // Removed ref, watch
import dayjs from 'dayjs'; // For date formatting

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  studentName: {
      type: String,
      default: ''
  },
  historyList: {
    type: Array,
    default: () => []
  },
  loading: {
      type: Boolean,
      default: false
  }
  // Removed clazzList prop
});

const emit = defineEmits(['update:visible']);

const handleClose = () => {
  emit('update:visible', false);
};

// Formatting function for date/time
const formatDateTime = (dateTime) => {
    if (!dateTime) return '-';
    return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss');
};

// Removed formatClassName function

// Use props somewhere to satisfy linter, e.g., in a log or computed property if needed
// console.log('History Dialog Props:', props);

</script>

<style scoped>
/* Add styles if needed */
</style> 