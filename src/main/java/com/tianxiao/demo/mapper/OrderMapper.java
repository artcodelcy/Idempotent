package com.tianxiao.demo.mapper;

import com.tianxiao.demo.entity.OrderEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

public interface OrderMapper {
	@Insert("insert order_info values (null,#{orderName},#{orderDes})")
	public int addOrder(OrderEntity OrderEntity);
}
