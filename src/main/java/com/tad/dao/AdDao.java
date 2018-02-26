package com.tad.dao;

import java.util.List;

import com.tad.bean.Ad;

public interface AdDao {

	public int insert(Ad ad);
	
	public List<Ad> selectByPage(Ad ad); 
	
	public Ad selectById(Long id);
	
	public int delete(Long id);
	
	public int update(Ad ad);
}
