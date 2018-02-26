package com.tad.dao;

import java.util.List;

import com.tad.bean.Business;

public interface BusinessDao {

	public int insert(Business business);
	
	public Business selectById(Long id);
	
	public List<Business> selectByPage(Business business);
	
	public int delete(Long id);
	
	public int update(Business business);

	public List<Business> selectLikeByPage(Business businessForSelect);
	
}
