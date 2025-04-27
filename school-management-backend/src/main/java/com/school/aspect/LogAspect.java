package com.school.aspect;

import com.alibaba.fastjson2.JSON;
import com.school.annotation.Log;
import com.school.security.service.LoginUser;
import com.school.entity.SysLog;
import com.school.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * 操作日志记录处理切面
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final SysLogService sysLogService;

    // ThreadLocal 用于存储操作开始时间
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    // 定义切点，拦截所有带有 @Log 注解的方法
    @Pointcut("@annotation(com.school.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 在切点之前执行
     */
    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        START_TIME.set(System.currentTimeMillis());
    }

    /**
     * 在切点之后正常返回时执行
     * @param joinPoint 切点
     * @param jsonResult 返回结果
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 在切点抛出异常后执行
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    /**
     * 处理日志记录
     * @param joinPoint 切点
     * @param e 异常信息
     * @param jsonResult 返回结果
     */
    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获取 @Log 注解
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            // 获取当前用户
            LoginUser loginUser = getLoginUser();

            // *========数据库日志=========*
            SysLog operLog = new SysLog();
            operLog.setStatus(0); // 0正常
            // 请求的地址
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ip = getIpAddress(request); // 获取 IP 地址
            operLog.setOperIp(ip);
            operLog.setOperUrl(request.getRequestURI());
            if (loginUser != null) {
                operLog.setOperName(loginUser.getUsername());
            }

            if (e != null) {
                operLog.setStatus(1); // 1异常
                operLog.setErrorMsg(e.getMessage().substring(0, Math.min(e.getMessage().length(), 2000)));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(request.getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
            // 设置消耗时间
            operLog.setCostTime(System.currentTimeMillis() - START_TIME.get());
            // 设置操作时间
            operLog.setOperTime(LocalDateTime.now());
            // 异步保存数据库
            sysLogService.saveLogAsync(operLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==", exp);
            log.error("异常信息:{}", exp.getMessage());
        } finally {
            START_TIME.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志注解
     * @param operLog 操作日志实体
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysLog operLog, Object jsonResult) throws Exception {
        // 设置action动作
        operLog.setBusinessType(log.businessType());
        // 设置标题
        operLog.setTitle(log.title());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog);
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && jsonResult != null) {
            operLog.setJsonResult(JSON.toJSONString(jsonResult));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志实体
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysLog operLog) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperParam(params.substring(0, Math.min(params.length(), 2000)));
        } else {
             HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
             Map<String, String[]> paramMap = request.getParameterMap();
             if (!paramMap.isEmpty()) {
                 operLog.setOperParam(JSON.toJSONString(paramMap).substring(0, Math.min(JSON.toJSONString(paramMap).length(), 2000)));
             }
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object o : paramsArray) {
                if (o != null && !isFilterObject(o)) {
                    try {
                        Object jsonObj = JSON.toJSON(o);
                        params.append(jsonObj.toString()).append(" ");
                    } catch (Exception e) {
                        log.warn("参数转换JSON失败: {}", e.getMessage());
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        }
        if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

    /**
     * 获取当前登录用户信息
     */
    private LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        } else if (authentication != null) {
            log.debug("Principal is not an instance of LoginUser: {}", authentication.getPrincipal().getClass());
            // Handle cases where principal might be String (e.g., anonymousUser)
             if ("anonymousUser".equals(authentication.getPrincipal())) {
                 return null; // Or return a specific anonymous LoginUser if needed
             }
        }
        return null;
    }

     /**
      * 获取IP地址
      * @param request HttpServletRequest
      * @return IP 地址
      */
     public static String getIpAddress(HttpServletRequest request) {
         String ip = request.getHeader("x-forwarded-for");
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("Proxy-Client-IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("WL-Proxy-Client-IP");
         }
          if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("HTTP_CLIENT_IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("HTTP_X_FORWARDED_FOR");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getRemoteAddr();
         }
         // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
         if(ip != null && ip.length() > 15){ // "***.***.***.***".length() = 15
             if(ip.indexOf(",") > 0){ 
                 ip = ip.substring(0, ip.indexOf(","));
             }
         }
         // 本地访问 localhost/127.0.0.1 转换
         if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
             // 根据网卡取本机配置的IP
             // 此处可以加入获取本机IP的逻辑，但通常在开发或测试环境，直接记录 127.0.0.1 即可
             ip = "127.0.0.1";
         }
         return ip;
     }

} 