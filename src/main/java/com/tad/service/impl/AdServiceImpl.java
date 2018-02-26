package com.tad.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tad.bean.Ad;
import com.tad.dao.AdDao;
import com.tad.dto.AdDto;
import com.tad.service.AdService;
import com.tad.util.FileUtil;

@Service
public class AdServiceImpl implements AdService {

	@Autowired
	private AdDao adDao;

	@Value("${adImage.savePath}")
	private String adImageSavePath;

	@Value("${adImage.url}")
	private String adImageUrl;

	@Override
	// TODO 可以改成添加失败详细原因
	public boolean add(AdDto adDto) {
		Ad ad = new Ad();
		ad.setTitle(adDto.getTitle());
		ad.setLink(adDto.getLink());
		ad.setWeight(adDto.getWeight());
		if (adDto.getImgFile() != null && adDto.getImgFile().getSize() > 0) {
			String imgFileName = System.currentTimeMillis() + "_" + adDto.getImgFile().getOriginalFilename();
			File file = new File(adImageSavePath + imgFileName);
			File fileFolder = new File(adImageSavePath);
			if (!fileFolder.exists()) {
				fileFolder.mkdirs();
			}
			try {
				adDto.getImgFile().transferTo(file);//把内存中的图片写入磁盘
				ad.setImgFileName(imgFileName);
				adDao.insert(ad);
				return true;
			} catch (IllegalStateException | IOException e) {
				// TODO log
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public List<AdDto> searchByPage(AdDto adDto) {
		List<AdDto> result = new ArrayList<AdDto>();
		Ad condition = new Ad();
		BeanUtils.copyProperties(adDto, condition);
		List<Ad> adList = adDao.selectByPage(condition);
		for (Ad ad : adList) {
			AdDto adDtoTemp = new AdDto();
			result.add(adDtoTemp);
			adDtoTemp.setImg(adImageUrl + ad.getImgFileName());
			BeanUtils.copyProperties(ad, adDtoTemp);
		}
		return result;
	}

	@Override
	public boolean remove(Long id) {
		Ad ad = adDao.selectById(id);
		int deleteRows = adDao.delete(id);
		FileUtil.delete(adImageSavePath + ad.getImgFileName());
		return deleteRows == 1;
	}

	@Override
	public boolean modify(AdDto adDto) {
		Ad ad = new Ad();
		BeanUtils.copyProperties(adDto, ad);
		String fileName = null;
		if(adDto.getImgFile() != null && adDto.getImgFile().getSize() > 0) {
			try {
				fileName = FileUtil.save(adDto.getImgFile(), adImageSavePath);
				ad.setImgFileName(fileName);
			} catch (IllegalStateException | IOException e) {
				return false;
			}
		}
		int updateCount = adDao.update(ad);
		if(updateCount != 1) {
			return false;
		}
		if(fileName != null) {
			return FileUtil.delete(adImageSavePath + adDto.getImgFileName());
		}
		return true;
	}

	@Override
	public AdDto getById(Long id) {
		AdDto result = new AdDto();
		Ad ad = adDao.selectById(id);
		BeanUtils.copyProperties(ad, result);
		result.setImg(adImageUrl + ad.getImgFileName());
		return result;
	}

}
