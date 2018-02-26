package com.tad.service;

import java.util.List;

import com.tad.dto.BusinessDto;
import com.tad.dto.BusinessListDto;

public interface BusinessService {

	public boolean add(BusinessDto businessDto);
	
	public BusinessDto getById(Long id);
	
	public List<BusinessDto> searchByPage(BusinessDto businessDto);
	
	public boolean remove(Long id);
	
	public boolean modify(BusinessDto businessDto);

	public BusinessListDto searchByPageForApi(BusinessDto businessDto);
}
