package com.tianxiao.demo.comtroller;

import com.tianxiao.demo.annotate.ExtApiIdempotent;
import com.tianxiao.demo.annotate.ExtApiToken;
import com.tianxiao.demo.entity.OrderEntity;
import com.tianxiao.demo.mapper.OrderMapper;
import com.tianxiao.demo.utils.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class OrderPageController {
	@Autowired
	private OrderMapper orderMapper;

	@RequestMapping("/indexPage")
	@ExtApiToken
	public String indexPage(HttpServletRequest req) {
		return "indexPage";
	}

	@RequestMapping("/addOrderPage")
	@ExtApiIdempotent(value = ConstantUtils.EXTAPIFROM)
	public String addOrder(OrderEntity orderEntity) {
		int addOrder = orderMapper.addOrder(orderEntity);
		return addOrder > 0 ? "success" : "fail";
	}

}
