package com.tianxiao.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RedisTokenUtils {
	private long timeout = 60 * 60;
	@Autowired
	private BaseRedisService baseRedisService;

	// 将token存入在redis
	public String getToken() {
		String token = "token" + System.currentTimeMillis();
		baseRedisService.setString(token, token, timeout);
		return token;
	}

	public boolean findToken(String tokenKey) {
		String token = (String) baseRedisService.getString(tokenKey);
		if (StringUtils.isEmpty(token)) {
			return false;
		}
		// token 获取成功后 删除对应tokenMapstoken
		baseRedisService.delKey(token);
		return true;
	}

}
