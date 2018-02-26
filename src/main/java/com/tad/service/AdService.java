package com.tad.service;

import java.util.List;

import com.tad.dto.AdDto;

public interface AdService {
	
	public boolean add(AdDto adDto);
	
	public List<AdDto> searchByPage(AdDto adDto);
	
	public boolean remove(Long id);
	
	public boolean modify(AdDto adDto);
	
	public AdDto getById(Long id);
	
	
}
