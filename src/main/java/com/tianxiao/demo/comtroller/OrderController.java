package com.tianxiao.demo.comtroller;

import com.tianxiao.demo.annotate.ExtApiIdempotent;
import com.tianxiao.demo.entity.OrderEntity;
import com.tianxiao.demo.mapper.OrderMapper;
import com.tianxiao.demo.utils.ConstantUtils;
import com.tianxiao.demo.utils.RedisTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class OrderController {

	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private RedisTokenUtils redisTokenUtils;

	// 从redis中获取Token
	@RequestMapping("/redisToken")
	public String RedisToken() {
		return redisTokenUtils.getToken();
	}

	// 验证Token
	@RequestMapping(value = "/addOrderExtApiIdempotent", produces = "application/json; charset=utf-8")
	@ExtApiIdempotent(value = ConstantUtils.EXTAPIHEAD) //表示从请求头获取token
	public String addOrderExtApiIdempotent(@RequestBody OrderEntity orderEntity, HttpServletRequest request) {
		int result = orderMapper.addOrder(orderEntity);
		return result > 0 ? "添加成功" : "添加失败" + "";
	}
}