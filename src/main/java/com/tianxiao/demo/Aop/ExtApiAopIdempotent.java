package com.tianxiao.demo.Aop;

import com.tianxiao.demo.annotate.ExtApiIdempotent;
import com.tianxiao.demo.annotate.ExtApiToken;
import com.tianxiao.demo.utils.ConstantUtils;
import com.tianxiao.demo.utils.RedisTokenUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Aspect
@Component
public class ExtApiAopIdempotent {
	@Autowired
	private RedisTokenUtils redisTokenUtils;

	@Pointcut("execution(public * com.tianxiao.demo.comtroller.*.*(..))")
	public void rlAop() {
	}

	// 前置通知转发Token参数
	@Before("rlAop()")
	public void before(JoinPoint point) {
		MethodSignature signature = (MethodSignature) point.getSignature();
		ExtApiToken extApiToken = signature.getMethod().getDeclaredAnnotation(ExtApiToken.class);
		if (extApiToken != null) {
			extApiToken();
		}
	}

	// 环绕通知验证参数
	@Around("rlAop()")
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		//判断方法上是否有幂等性注解
		MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
		ExtApiIdempotent extApiIdempotent = signature.getMethod().getDeclaredAnnotation(ExtApiIdempotent.class); //获取这个注解
		if (extApiIdempotent != null) {
			return extApiIdempotent(proceedingJoinPoint, signature);
		}
		// 放行
		Object proceed = proceedingJoinPoint.proceed();
		return proceed;
	}

	// 验证Token
	public Object extApiIdempotent(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature)
			throws Throwable {
		ExtApiIdempotent extApiIdempotent = signature.getMethod().getDeclaredAnnotation(ExtApiIdempotent.class);
		if (extApiIdempotent == null) {
			// 直接执行程序
			Object proceed = proceedingJoinPoint.proceed();
			return proceed;
		}
		// 代码步骤：
		// 1.获取令牌 存放在请求头中
		HttpServletRequest request = getRequest();
		String valueType = extApiIdempotent.value();
		if (StringUtils.isEmpty(valueType)) {
			response("参数错误!");
			return null;
		}
		String token = null;
		if (valueType.equals(ConstantUtils.EXTAPIHEAD)) {
			token = request.getHeader("token");
		} else {
			token = request.getParameter("token");
		}
		if (StringUtils.isEmpty(token)) {
			response("参数错误!");
			return null;
		}
		if (!redisTokenUtils.findToken(token)) {
			response("请勿重复提交!");
			return null;
		}
		Object proceed = proceedingJoinPoint.proceed();
		return proceed;
	}

	public void extApiToken() {
		String token = redisTokenUtils.getToken();
		getRequest().setAttribute("token", token);

	}
//在aop里面获取request对象
	public HttpServletRequest getRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		return request;
	}

	public void response(String msg) throws IOException {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = attributes.getResponse();
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		try {
			writer.println(msg);
		} catch (Exception e) {

		} finally {
			writer.close();
		}

	}

}
