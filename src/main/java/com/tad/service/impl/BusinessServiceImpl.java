package com.tad.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tad.bean.Business;
import com.tad.bean.Page;
import com.tad.constant.CategoryConst;
import com.tad.dao.BusinessDao;
import com.tad.dto.BusinessDto;
import com.tad.dto.BusinessListDto;
import com.tad.service.BusinessService;
import com.tad.util.CommonUtil;
import com.tad.util.FileUtil;

@Service
public class BusinessServiceImpl implements BusinessService {

	@Autowired
	private BusinessDao businessDao;

	@Value("${businessImage.savePath}")
	private String savePath;

	@Value("${businessImage.url}")
	private String url;

	@Override
	public boolean add(BusinessDto businessDto) {
		Business business = new Business();
		BeanUtils.copyProperties(businessDto, business);
		if (businessDto.getImgFile() != null && businessDto.getImgFile().getSize() > 0) {
			try {
				String fileName = FileUtil.save(businessDto.getImgFile(), savePath);
				business.setImgFileName(fileName);
				business.setNumber(0);
				business.setCommentTotalNum(0L);
				business.setStarTotalNum(0L);
				businessDao.insert(business);
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
	public BusinessDto getById(Long id) {
		BusinessDto dto = new BusinessDto();
		Business business = businessDao.selectById(id);
		BeanUtils.copyProperties(business, dto);
		dto.setImg(url + business.getImgFileName());
		dto.setStar(this.getStar(business));
		return dto;
	}

	@Override
	public List<BusinessDto> searchByPage(BusinessDto businessDto) {
		List<BusinessDto> result = new ArrayList<>();
		Business businessForSelect = new Business();
		BeanUtils.copyProperties(businessDto, businessForSelect);
		List<Business> list = businessDao.selectByPage(businessForSelect);
		for (Business business : list) {
			BusinessDto businessDtoTemp = new BusinessDto();
			result.add(businessDtoTemp);
			BeanUtils.copyProperties(business, businessDtoTemp);
			businessDtoTemp.setImg(url + business.getImgFileName());
			businessDtoTemp.setStar(this.getStar(business));
		}
		return result;
	}

	private int getStar(Business business) {
		if (business.getStarTotalNum() != null && business.getCommentTotalNum() != null
				&& business.getCommentTotalNum() != 0) {
			return (int) (business.getStarTotalNum() / business.getCommentTotalNum());
		} else {
			return 0;
		}
	}

	@Override
	public boolean remove(Long id) {
		Business business = businessDao.selectById(id);
		int deleteRows = businessDao.delete(id);
		FileUtil.delete(savePath + business.getImgFileName());
		return deleteRows == 1;
	}

	@Override
	public boolean modify(BusinessDto businessDto) {
		Business business = new Business();
		BeanUtils.copyProperties(businessDto, business);// 将businessDto的值赋给business

		String fileName = null;
		if (businessDto.getImgFile() != null && businessDto.getImgFile().getSize() > 0) {
			try {
				fileName = FileUtil.save(businessDto.getImgFile(), savePath);
				business.setImgFileName(fileName);
			} catch (IllegalStateException | IOException e) {
				// TODO 添加日志
				return false;
			}
		}
		int updateCount = businessDao.update(business);
		if (updateCount != 1) {
			return false;
		}
		if (fileName != null) {
			return FileUtil.delete(savePath + businessDto.getImgFileName());
		}
		return true;
	}

	@Override
	public BusinessListDto searchByPageForApi(BusinessDto businessDto) {
		BusinessListDto result = new BusinessListDto();

		// 组织查询条件
		Business businessForSelect = new Business();
		BeanUtils.copyProperties(businessDto, businessForSelect);

		// 当关键字不为空时，把关键字的值分别设置到标题、副标题、描述中
		// TODO 改进做法：全文检索
		if (!CommonUtil.isEmpty(businessDto.getKeyword())) {
			businessForSelect.setTitle(businessDto.getKeyword());
			businessForSelect.setSubtitle(businessDto.getKeyword());
			businessForSelect.setDesc(businessDto.getKeyword());
		}
		// 当类别为全部(all)时，需要将类别清空，不作为过滤条件
		if (businessDto.getCategory() != null && CategoryConst.ALL.equals(businessDto.getCategory())) {
			businessForSelect.setCategory(null);
		}

		// 前端app页码从0开始计算，这里需要+1
		int currentPage = businessForSelect.getPage().getCurrentPage();
		businessForSelect.getPage().setCurrentPage(currentPage + 1);

		List<Business> list = businessDao.selectLikeByPage(businessForSelect);

		// 经过查询后根据page对象设置hasMore
		Page page = businessForSelect.getPage();
		result.setHasMore(page.getCurrentPage() < page.getTotalPage());

		for (Business business : list) {
			BusinessDto businessDtoTemp = new BusinessDto();
			result.getData().add(businessDtoTemp);
			BeanUtils.copyProperties(business, businessDtoTemp);
			businessDtoTemp.setImg(url + business.getImgFileName());
			// 为兼容前端mumber这个属性
			businessDtoTemp.setMumber(business.getNumber());
			businessDtoTemp.setStar(this.getStar(business));
		}

		return result;
	}

}
