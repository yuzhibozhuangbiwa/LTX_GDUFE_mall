/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.service.NewBeeMallCarouselService;
import ltd.newbee.mall.util.NewBeeMallUtils;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 */
@Controller
@RequestMapping("/admin")
public class NewBeeMallCarouselController {

    @Resource
    NewBeeMallCarouselService neeBeeMallCarouselService;

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_carousel");
        return "admin/newbee_mall_carousel";
    }

    @GetMapping("/carousels/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(neeBeeMallCarouselService.getCarouselPage(pageUtil));
    }


    @RequestMapping("/carousels/save")
    @ResponseBody
    public Result save(@RequestBody Carousel carousel){
            if(Objects.isNull(carousel.getCarouselId())
            ||StringUtils.isEmpty(carousel.getCarouselUrl())
                    ||Objects.isNull(carousel.getCarouselRank())){
                return ResultGenerator.genFailResult("传入参数有问题");
            }

        String result = neeBeeMallCarouselService.saveCarousel(carousel);

            if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
                return ResultGenerator.genSuccessResult();
            }else{
                return ResultGenerator.genFailResult(result);
            }

    }


    @PostMapping("/carousels/update")
    @ResponseBody
    public Result update(@RequestBody Carousel carousel){
        if(Objects.isNull(carousel.getCarouselId())
                ||StringUtils.isEmpty(carousel.getCarouselUrl())
                ||Objects.isNull(carousel.getCarouselRank())){
            return ResultGenerator.genFailResult("传入参数有问题");
        }
        String result = neeBeeMallCarouselService.updateCarousel(carousel);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult(result);
        }
    }


    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        Carousel carousel = neeBeeMallCarouselService.getCarouselById(id);
        if (carousel == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(carousel);
    }


    @PostMapping("/carousels/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if (ids.length<1){
            return ResultGenerator.genFailResult("传入参数有误");
        }
        if(neeBeeMallCarouselService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult("deleteBatch方法错误 删除失败");
        }



    }




}