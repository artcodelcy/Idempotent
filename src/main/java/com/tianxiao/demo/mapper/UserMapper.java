package com.tianxiao.demo.mapper;

import com.tianxiao.demo.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

	@Select(" SELECT  * FROM user_info where userName=#{userName} and password=#{password}")
	public UserEntity login(UserEntity userEntity);

	@Insert("insert user_info values (null,#{userName},#{password})")
	public int insertUser(UserEntity userEntity);
}
