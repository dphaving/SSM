package com.tad.controller.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tad.constant.DicTypeConst;
import com.tad.constant.PageCodeEnum;
import com.tad.dto.BusinessDto;
import com.tad.service.BusinessService;
import com.tad.service.DicService;

@Controller
@RequestMapping("/businesses")
public class BusinessesController {

	@Autowired
	private BusinessService businessService;

	@Autowired
	private DicService dicService;

	/**
	 * 商户列表
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String search(Model model, BusinessDto dto) {
		model.addAttribute("list", businessService.searchByPage(dto));
		model.addAttribute("searchParam", dto);
		return "/content/businessList";
	}

	/**
	 * 商户新增页初始化
	 */
	@RequestMapping(value = "/addPage", method = RequestMethod.GET)
	public String addInit(Model model) {
		model.addAttribute("cityList", dicService.getListByType(DicTypeConst.CITY));
		model.addAttribute("categoryList", dicService.getListByType(DicTypeConst.CATEGORY));
		return "/content/businessAdd";
	}

	/**
	 * 商户新增
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String add(BusinessDto dto, RedirectAttributes attr) {
		if (businessService.add(dto)) {
			attr.addAttribute(PageCodeEnum.KEY, PageCodeEnum.ADD_SUCCESS);
			return "redirect:/businesses";
		} else {
			attr.addAttribute(PageCodeEnum.KEY, PageCodeEnum.ADD_FAIL);
			return "redirect:/businesses/addPage";
		}
	}

	/**
	 * 商户修改页初始化
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String modifyInit(Model model, @PathVariable("id") Long id) {
		model.addAttribute("cityList", dicService.getListByType(DicTypeConst.CITY));
		model.addAttribute("categoryList", dicService.getListByType(DicTypeConst.CATEGORY));
		model.addAttribute("modifyObj", businessService.getById(id));
		return "/content/businessModify";
	}

	/**
	 * 商户修改
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String modify(Model model, BusinessDto dto) {
		model.addAttribute("modelObj", dto);
		if(businessService.modify(dto)) {
			model.addAttribute(PageCodeEnum.KEY, PageCodeEnum.MODIFY_SUCCESS);
		}else {
			model.addAttribute(PageCodeEnum.KEY, PageCodeEnum.MODIFY_FAIL);
		}
		return "/content/businessModify";
	}
	
	/**
	 * 删除商户
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String remove(@PathVariable("id") Long id, Model model) {
		System.out.println(id);
		if(businessService.remove(id)) {
			model.addAttribute(PageCodeEnum.KEY, PageCodeEnum.REMOVE_SUCCESS);
		}else {
			model.addAttribute(PageCodeEnum.KEY, PageCodeEnum.REMOVE_FAIL);
		}
		return "redirect:/businesses";
	}

}
