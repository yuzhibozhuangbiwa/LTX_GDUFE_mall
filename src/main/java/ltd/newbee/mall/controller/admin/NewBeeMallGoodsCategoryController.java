/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 */
@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsCategoryController {

    @Resource
    private NewBeeMallCategoryService newBeeMallCategoryService;

    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest request,@RequestParam("categoryLevel")Byte categoryLevel,@RequestParam("parentId") Long parentId,@RequestParam("backParentId") Long backParentId){
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) {
            NewBeeMallException.fail("参数异常");
        }
        request.setAttribute("path", "newbee_mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("categoryLevel", categoryLevel);
        return "admin/newbee_mall_category";
    }

    @GetMapping("/categories/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(newBeeMallCategoryService.getCategorisPage(pageUtil));
    }

    @GetMapping("/categories/listForSelect")
    @ResponseBody
    public Result listForSelect(@RequestParam("categoryId") Long categoryId){
        if(categoryId<1||categoryId==null){
            return ResultGenerator.genFailResult("接受的categoryId参数错误");
        }

        Map categoryResult = new HashMap(4);
        GoodsCategory goodsCategoryById = newBeeMallCategoryService.getGoodsCategoryById(categoryId);
        if (goodsCategoryById == null || goodsCategoryById.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        if (goodsCategoryById.getCategoryLevel()==NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()||goodsCategoryById!=null){
            GoodsCategory firstCategoryById1 = newBeeMallCategoryService.getGoodsCategoryById(goodsCategoryById.getParentId());
            List<GoodsCategory> thirdgoodsCategories02=newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId),NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdgoodsCategories02);

        }

        if (goodsCategoryById.getCategoryLevel()==NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel()||goodsCategoryById!=null) {
            Long sencondparentId = goodsCategoryById.getParentId();
            List<GoodsCategory> secondgoodsCategories01 = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondgoodsCategories01)) {
                List<GoodsCategory> thirdgoodsCategories01 = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondgoodsCategories01.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondgoodsCategories01);
                categoryResult.put("thirdLevelCategories", thirdgoodsCategories01);
            }

        }
            return ResultGenerator.genSuccessResult(categoryResult);


    }

    @RequestMapping("/categories/save")
    @ResponseBody
    public Result save(@RequestBody GoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = newBeeMallCategoryService.saveCategory(goodsCategory);
        if(ServiceResultEnum.SUCCESS.getResult().equals(goodsCategory)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @RequestMapping("/categories/update")
    @ResponseBody
    public Result update(@RequestBody GoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryLevel())
              ||  Objects.isNull(goodsCategory.getCategoryId())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = newBeeMallCategoryService.saveCategory(goodsCategory);
        if(ServiceResultEnum.SUCCESS.getResult().equals(goodsCategory)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult(result);
        }
    }


    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        GoodsCategory goodsCategory = newBeeMallCategoryService.getGoodsCategoryById(id);
        if (goodsCategory == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(goodsCategory);
    }


    @RequestMapping(value = "/categories/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (newBeeMallCategoryService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }


}