<!--
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 21:38:17
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:05:33
 * @FilePath: \schoolmanagenta\school-management-frontend\src\views\AiChat.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<script setup>
import { ref, nextTick, onMounted, onUpdated } from 'vue';
import { ElMessage, ElScrollbar, ElInput, ElButton, ElCard } from 'element-plus';
import { Promotion } from '@element-plus/icons-vue';
import { sendAiMessage } from '@/api/ai';
import { marked } from 'marked'; // 导入 marked
import DOMPurify from 'dompurify'; // 导入 DOMPurify
import hljs from 'highlight.js'; // 导入 highlight.js
import 'highlight.js/styles/github-dark.css'; // 导入 highlight.js 的 CSS 主题

const userInput = ref(''); // 用户输入
const conversation = ref([]); // 聊天记录 { role: 'user' | 'assistant', content: string }[]
const isLoading = ref(false); // 是否正在等待 AI 响应
const chatAreaRef = ref(null); // 用于自动滚动
const chatContainerRef = ref(null); // Added for the new chat container
const scrollbarRef = ref(null); // Added for the scrollbar

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    const chatArea = chatAreaRef.value?.$el?.querySelector('.el-scrollbar__wrap');
    if (chatArea) {
      chatArea.scrollTop = chatArea.scrollHeight;
    }
  });
};

const handleSend = async () => {
  const message = userInput.value.trim();
  if (!message || isLoading.value) {
    return;
  }

  // 1. 将用户消息添加到对话记录
  conversation.value.push({ role: 'user', content: message });
  userInput.value = ''; // 清空输入框
  scrollToBottom();

  // 2. 设置加载状态并调用 API
  isLoading.value = true;
  try {
    const res = await sendAiMessage(message);
    // 假设后端 R.success(data) 返回的是 AI 响应字符串
    if (res && res.code === 200 && res.data) {
      conversation.value.push({ role: 'assistant', content: res.data });
    } else {
      // 处理后端返回的业务错误
      const errorMsg = res?.msg || 'AI 服务返回错误，但未提供具体信息';
      conversation.value.push({ role: 'assistant', content: `错误: ${errorMsg}` });
      ElMessage.error(errorMsg);
    }
  } catch (error) {
    // 处理网络或请求错误
    console.error("Error sending chat message:", error);
    const errorText = error.message || '与 AI 服务通信时发生网络错误';
    conversation.value.push({ role: 'assistant', content: `错误: ${errorText}` });
    ElMessage.error(errorText);
  } finally {
    isLoading.value = false;
    scrollToBottom();
  }
};

// Function to render Markdown and sanitize HTML
const renderMarkdown = (text) => {
  if (!text) return '';
  // Configure marked to use highlight.js
  marked.setOptions({
    highlight: function(code, lang) {
      const language = hljs.getLanguage(lang) ? lang : 'plaintext';
      return hljs.highlight(code, { language }).value;
    },
    langPrefix: 'hljs language-', // prefix for CSS classes
  });
  const rawHtml = marked.parse(text);
  return DOMPurify.sanitize(rawHtml);
};

// Function to add copy buttons to code blocks
const addCopyButtons = () => {
  const codeBlocks = document.querySelectorAll('.chat-container pre'); // 更精确的选择器
  codeBlocks.forEach((block) => {
    // Avoid adding button if it already exists
    if (block.querySelector('.copy-button')) {
      return;
    }

    const button = document.createElement('button');
    button.textContent = 'Copy';
    button.className = 'copy-button'; // Add class for styling

    // Add specific class to parent pre for positioning context if needed
    block.classList.add('code-block-wrapper');


    button.addEventListener('click', async () => {
      const code = block.querySelector('code');
      if (code) {
        try {
          await navigator.clipboard.writeText(code.innerText);
          button.textContent = 'Copied!';
          setTimeout(() => {
            button.textContent = 'Copy';
          }, 2000); // Reset button text after 2 seconds
           ElMessage.success('Code copied to clipboard!');
        } catch (err) {
          console.error('Failed to copy code: ', err);
          ElMessage.error('Failed to copy code.');
          button.textContent = 'Error';
           setTimeout(() => {
            button.textContent = 'Copy';
          }, 2000);
        }
      }
    });

    block.appendChild(button);
  });
};

// Call highlighting and add copy buttons after DOM updates
onUpdated(() => {
  nextTick(() => {
    // Highlight all code blocks again on update is less efficient but ensures dynamic content is handled
    // A more optimized approach might target only new messages
    // document.querySelectorAll('.chat-container pre code').forEach((block) => {
    //   hljs.highlightElement(block); // Marked already applies highlighting via setOptions
    // });
    addCopyButtons(); // Add copy buttons to new or existing code blocks
  });
});

// Initial highlight and button add on mount
onMounted(() => {
  nextTick(() => {
    // document.querySelectorAll('.chat-container pre code').forEach((block) => {
    //   hljs.highlightElement(block);
    // });
    addCopyButtons();
  });
  scrollbarRef.value?.setScrollTop(chatContainerRef.value?.scrollHeight ?? 0); // Scroll on mount if needed
});
</script>

<template>
  <el-card class="chat-card">
    <div class="chat-container" ref="chatContainerRef">
      <el-scrollbar ref="scrollbarRef" always>
        <div v-for="(msg, index) in conversation" :key="index" class="message-row" :class="{'user-message-row': msg.role === 'user', 'assistant-message-row': msg.role === 'assistant'}">
          <div class="message-bubble" :class="{'user-bubble': msg.role === 'user', 'assistant-bubble': msg.role === 'assistant'}">
            <!-- Render message content as sanitized HTML -->
            <div v-if="msg.content" v-html="renderMarkdown(msg.content)"></div>
             <!-- Loading indicator for assistant message -->
            <div v-if="msg.role === 'assistant' && isLoading && index === conversation.length - 1" class="loading-indicator">
               <span>Thinking...</span>
               <div class="dot-flashing"></div>
            </div>
          </div>
        </div>
      </el-scrollbar>
    </div>
    <div class="input-area">
      <el-input
        v-model="userInput"
        placeholder="Type your message..."
        @keyup.enter="handleSend"
        clearable
        :disabled="isLoading"
         size="large"
         class="input-field"
      >
        <template #append>
          <el-button type="primary" @click="handleSend" :disabled="isLoading || !userInput.trim()" :icon="Promotion"></el-button>
        </template>
      </el-input>
    </div>
  </el-card>
</template>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px); /* Adjust based on your layout's header/footer height */
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
   background-color: #f9f9f9;
}

.chat-area {
  flex-grow: 1;
  padding: 15px;
  background-color: #ffffff;
  overflow-y: auto; /* Ensure scrollbar appears when needed */
}

/* Style the scrollbar wrap if using ElScrollbar */
:deep(.el-scrollbar__wrap) {
  overflow-x: hidden; /* Hide horizontal scrollbar */
}


.message-row {
  display: flex;
  margin-bottom: 15px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.assistant {
  justify-content: flex-start;
}

.message-bubble {
  padding: 10px 15px;
  border-radius: 15px;
  max-width: 70%;
  word-wrap: break-word; /* Wrap long words */
   line-height: 1.5;
}

.message-bubble.user {
  background-color: #409EFF;
  color: white;
  border-bottom-right-radius: 5px;
}

.message-bubble.assistant {
  background-color: #f0f2f5;
  color: #303133;
   border-bottom-left-radius: 5px;
}

.message-bubble.loading span {
    font-style: italic;
    color: #909399;
}

.input-area {
  margin-top: 15px; /* Add some space above the input */
  display: flex;
  align-items: center;
}

.input-field {
 flex-grow: 1; /* Allow input to take available space */
 margin-right: 10px; /* Space between input and button */
}

/* Ensure el-button in append slot looks right */
:deep(.el-input-group__append .el-button) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

/* Styles for code blocks generated by marked and highlight.js */
:deep(.message-bubble pre) { /* Use :deep() to style content generated by v-html */
  background-color: #2d2d2d; /* Dark background for code */
  color: #f8f8f2; /* Light text color */
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto; /* Allow horizontal scrolling for long code lines */
  position: relative; /* Needed for absolute positioning of the copy button */
  margin: 10px 0; /* Add some margin */
}

:deep(.message-bubble pre code.hljs) { /* Style the code itself */
  display: block;
  font-family: 'Fira Code', 'Courier New', Courier, monospace; /* Use a monospaced font */
  font-size: 0.9em;
  line-height: 1.5;
  white-space: pre; /* Preserve whitespace and line breaks */
}

/* Style for the copy button */
.code-block-wrapper { /* Wrapper class added to pre */
  position: relative;
}


:deep(.copy-button) {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #555;
  color: #fff;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.8em;
  opacity: 0; /* Initially hidden */
  transition: opacity 0.3s ease, background-color 0.3s ease;
}

/* Show button on hover over the code block */
:deep(.code-block-wrapper:hover .copy-button) {
  opacity: 1;
}

:deep(.copy-button:hover) {
  background-color: #777;
}

:deep(.copy-button:active) {
  background-color: #444;
}

/* Loading Indicator styles */
.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center; /* Center items */
  padding: 10px 0;
  color: #888; /* Lighter color */
}

.loading-indicator span {
  margin-right: 10px; /* Space between text and animation */
}

/* Dot Flashing Animation */
.dot-flashing {
  position: relative;
  width: 10px;
  height: 10px;
  border-radius: 5px;
  background-color: #9880ff; /* Use a theme color or default */
  color: #9880ff;
  animation: dot-flashing 1s infinite linear alternate;
  animation-delay: 0.5s;
  display: inline-block; /* Make it inline with text */
  vertical-align: middle; /* Align vertically */
}

.dot-flashing::before, .dot-flashing::after {
  content: '';
  display: inline-block;
  position: absolute;
  top: 0;
}

.dot-flashing::before {
  left: -15px;
  width: 10px;
  height: 10px;
  border-radius: 5px;
  background-color: #9880ff;
  color: #9880ff;
  animation: dot-flashing 1s infinite alternate;
  animation-delay: 0s;
}

.dot-flashing::after {
  left: 15px;
  width: 10px;
  height: 10px;
  border-radius: 5px;
  background-color: #9880ff;
  color: #9880ff;
  animation: dot-flashing 1s infinite alternate;
  animation-delay: 1s;
}

@keyframes dot-flashing {
  0% {
    background-color: #9880ff;
  }
  50%, 100% {
    background-color: rgba(152, 128, 255, 0.2); /* Lighter/transparent version */
  }
}

</style> 