/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
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
public class NewBeeMallGoodsController {

    @Autowired
    NewBeeMallCategoryService newBeeMallCategoryService;
    @Autowired
    NewBeeMallGoodsService newBeeMallGoodsService;


    @GetMapping("/goods")
    public String showgood(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_goods");
        return "admin/newbee_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String showeditgoods(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        List<GoodsCategory> firstgoodsCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstgoodsCategories)) {
            List<GoodsCategory> secondgoodsCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstgoodsCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());

            if (!CollectionUtils.isEmpty(firstgoodsCategories)) {
                List<GoodsCategory> thirdgoodsCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondgoodsCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());

                request.setAttribute("firstLevelCategories", firstgoodsCategories);
                request.setAttribute("secondLevelCategories", secondgoodsCategories);
                request.setAttribute("thirdLevelCategories", thirdgoodsCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/newbee_mall_goods_edit";

            }

        }

        NewBeeMallException.fail("分类数据不完善");
        return null;
    }

    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(newBeeMallGoodsService.getNewBeeMallGoodsPage(pageUtil));
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String eidt(HttpServletRequest request,@PathVariable("goodsId") Long goodsId){

        request.setAttribute("path","edit");
        NewBeeMallGoods newBeeMallGoodsById = newBeeMallGoodsService.getNewBeeMallGoodsById(goodsId);
        if(newBeeMallGoodsById!=null||newBeeMallGoodsById.getGoodsCategoryId()>0){
            GoodsCategory currentGoodsCategory = newBeeMallCategoryService.getGoodsCategoryById(newBeeMallGoodsById.getGoodsCategoryId());

            if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()){
                List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());

                List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());


                GoodsCategory secondegoodsCategoryById = newBeeMallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                       if (secondegoodsCategoryById!= null && secondegoodsCategoryById.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
                    List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondegoodsCategoryById.getParentId()), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());

                    GoodsCategory firstgoodsCategoryById = newBeeMallCategoryService.getGoodsCategoryById(secondegoodsCategoryById.getParentId());

                    if (firstgoodsCategoryById != null && firstgoodsCategoryById.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
                        request.setAttribute("firstLevelCategories", firstLevelCategories);
                        request.setAttribute("secondLevelCategories", secondLevelCategories);
                        request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                        request.setAttribute("firstLevelCategoryId", firstgoodsCategoryById.getCategoryId());
                        request.setAttribute("secondLevelCategoryId", secondegoodsCategoryById.getCategoryId());
                        request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());

                    }


                }
            }
        }
                    if (newBeeMallGoodsById.getGoodsCategoryId() == 0) {
                        //查询所有的一级分类
                        List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                            //查询一级分类列表中第一个实体的所有二级分类
                            List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                                //查询二级分类列表中第一个实体的所有三级分类
                                List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                                request.setAttribute("firstLevelCategories", firstLevelCategories);
                                request.setAttribute("secondLevelCategories", secondLevelCategories);
                                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            }
                        }
                    }




        request.setAttribute("goods",newBeeMallGoodsById);
        request.setAttribute("path","goods-edit");
        return "admin/newbee_mall_goods_edit";



    }


    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody NewBeeMallGoods newBeeMallGoods) {
        if (StringUtils.isEmpty(newBeeMallGoods.getGoodsName())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(newBeeMallGoods.getTag())
                || Objects.isNull(newBeeMallGoods.getOriginalPrice())
                || Objects.isNull(newBeeMallGoods.getGoodsCategoryId())
                || Objects.isNull(newBeeMallGoods.getSellingPrice())
                || Objects.isNull(newBeeMallGoods.getStockNum())
                || Objects.isNull(newBeeMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result=newBeeMallGoodsService.saveNewBeeMallGoods(newBeeMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody NewBeeMallGoods newBeeMallGoods) {
        if (Objects.isNull(newBeeMallGoods.getGoodsId())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsName())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(newBeeMallGoods.getTag())
                || Objects.isNull(newBeeMallGoods.getOriginalPrice())
                || Objects.isNull(newBeeMallGoods.getSellingPrice())
                || Objects.isNull(newBeeMallGoods.getGoodsCategoryId())
                || Objects.isNull(newBeeMallGoods.getStockNum())
                || Objects.isNull(newBeeMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = newBeeMallGoodsService.updateNewBeeMallGoods(newBeeMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


}