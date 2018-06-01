package com.mmall.dao;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

	int checkUserExist(String username);

	User checkLogin(@Param("username")String username, @Param("password")String password);

	int checkEmail(String email);

	String getQuestionByUsername(String username);

	int checkAnswer(@Param("username")String username, @Param("question")String question, @Param("answer")String answer);

	int updatePasswordByUserName(@Param("username")String username, @Param("passwordNew")String passwordNew);

	int checkPasswordById(@Param("id")Integer id, @Param("password")String password);

	int checkEmailWithId(@Param("id")Integer id, @Param("email")String email);
}