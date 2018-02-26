package com.tad.service;

import java.util.List;

import com.tad.bean.Dic;

public interface DicService {

	/**
	 * 根据类型获取字典表列表
	 * @param type
	 * @return
	 */
	public List<Dic> getListByType(String type);
	
}
