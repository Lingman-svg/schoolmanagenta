<template>
  <div class="login-container">
    <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">

      <div class="title-container">
        <h3 class="title">小学管理系统登录</h3>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <el-icon><User /></el-icon> 
        </span>
        <el-input
          ref="usernameRef"
          v-model="loginForm.username"
          placeholder="用户名"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>

      <el-tooltip v-model:visible="capsTooltipVisible" content="大写锁定已开启" placement="right" manual>
          <el-form-item prop="password">
              <span class="svg-container">
                 <el-icon><Lock /></el-icon> 
              </span>
              <el-input
                  :key="passwordType"
                  ref="passwordRef"
                  v-model="loginForm.password"
                  :type="passwordType"
                  placeholder="密码"
                  name="password"
                  tabindex="2"
                  auto-complete="on"
                  @keyup="checkCapslock"
                  @blur="capsTooltipVisible = false"
                  @keyup.enter="handleLogin"
              />
              <span class="show-pwd" @click="showPwd">
                 <el-icon><component :is="passwordType === 'password' ? 'View' : 'Hide'" /></el-icon>
              </span>
          </el-form-item>
      </el-tooltip>

      <!-- TODO: 添加验证码功能 -->
      <!-- 
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input v-model="loginForm.code" auto-complete="off" placeholder="验证码" style="width: 63%" @keyup.enter="handleLogin">
           <template #prefix><el-icon class="el-input__icon"><CircleCheck /></el-icon></template>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item> 
      -->

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.prevent="handleLogin">登 录</el-button>

      <!-- TODO: 添加其他链接，如忘记密码、注册 -->
      <!-- 
      <div style="position:relative">
        <router-link to="/register" class="link-type">注册</router-link>
      </div> 
      -->

    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
// 引入图标 (需要安装 @element-plus/icons-vue)
import { User, Lock, View, Hide, CircleCheck } from '@element-plus/icons-vue';

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

// --- Refs for form elements ---
const loginFormRef = ref(null);
const usernameRef = ref(null);
const passwordRef = ref(null);

// --- Reactive State ---
const loginForm = reactive({
  username: 'admin', // 默认填充 admin，方便测试
  password: 'admin123', // 默认填充密码，方便测试
  // code: '', // 验证码
  // uuid: '' // 验证码 UUID
});

const loginRules = reactive({
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  // code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
});

const loading = ref(false);
const passwordType = ref('password');
const capsTooltipVisible = ref(false);
const redirect = ref(undefined);
// const captchaEnabled = ref(true); // 是否开启验证码
// const codeUrl = ref(""); // 验证码图片 URL

// --- Watchers ---
watch(route, (newRoute) => {
  redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

// --- Methods ---
function checkCapslock(e) {
  const { key } = e;
  capsTooltipVisible.value = key && key.length === 1 && (key >= 'A' && key <= 'Z');
}

function showPwd() {
  passwordType.value = passwordType.value === 'password' ? '' : 'password';
  // 确保切换后 input 仍然聚焦 (如果需要)
  passwordRef.value?.focus(); 
}

// TODO: 获取验证码逻辑
// function getCode() { ... }

function handleLogin() {
  loginFormRef.value?.validate(valid => {
    if (valid) {
      loading.value = true;
      userStore.login(loginForm)
        .then(() => {
          ElMessage.success('登录成功');
          router.push({ path: redirect.value || '/' }); // 登录成功后跳转
        })
        .catch((error) => {
          // 登录 action 中 reject 的错误信息
          ElMessage.error(error || '登录失败，请检查用户名和密码');
        })
        .finally(() => {
          loading.value = false;
          // TODO: 如果登录失败且开启了验证码，刷新验证码
          // if (captchaEnabled.value) { getCode(); }
        });
    } else {
      console.log('error submit!!');
      return false;
    }
  });
}

// --- Lifecycle Hooks ---
onMounted(() => {
  // TODO: 获取验证码
  // getCode();
});

</script>

<style lang="scss" scoped>
$bg:#2d3a4b;
$light_gray:#fff;
$cursor: #fff;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: #889aa4;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: #889aa4;
    cursor: pointer;
    user-select: none;
  }

  .login-code {
    width: 33%;
    height: 47px; // 与 el-input 高度一致
    float: right;

    img {
      cursor: pointer;
      vertical-align: middle;
      height: 100%;
    }
  }

  .el-input {
      display: inline-block;
      height: 47px;
      width: 85%; // 调整宽度以适应图标容器

      :deep(input) { // 样式穿透
          background: transparent;
          border: 0px;
          -webkit-appearance: none;
          border-radius: 0px;
          padding: 12px 5px 12px 15px;
          color: $light_gray;
          height: 47px;
          caret-color: $cursor;

          &:-webkit-autofill {
              box-shadow: 0 0 0px 1000px $bg inset !important;
              -webkit-text-fill-color: $cursor !important;
          }
      }
  }

  .el-form-item {
      border: 1px solid rgba(255, 255, 255, 0.1);
      background: rgba(0, 0, 0, 0.1);
      border-radius: 5px;
      color: #454545;
  }
}
</style> 