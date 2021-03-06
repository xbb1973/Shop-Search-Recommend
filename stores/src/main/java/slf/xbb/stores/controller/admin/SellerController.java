package slf.xbb.stores.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;
import slf.xbb.stores.aspect.AdminPermission;
import slf.xbb.stores.common.*;
import slf.xbb.stores.entity.Seller;
import slf.xbb.stores.service.ISellerService;
import slf.xbb.stores.vo.PageQuery;
import slf.xbb.stores.vo.SellerCreateReq;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 商户 前端控制器
 * </p>
 *
 * @author xbb
 * @since 2020-04-13
 */
@Controller("/admin/seller")
@RequestMapping("/admin/seller")
public class SellerController {

    @Autowired
    private ISellerService sellerService;

    public static String controllerAttributeName = "CONTROLLER_NAME";
    public static String actionAttributeName = "ACTION_NAME";
    public static String controllerAttributeValue = "seller";

    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index(PageQuery pageQuery)  {
        // 分页查询

        // // 1、使用PageHelper插件
        // PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
        // //sellerService.list
        // List<Seller> sellerList = sellerService.list();
        // PageInfo<Seller> sellerPageInfo = new PageInfo<>(sellerList);

        // 2、使用MyBatisPlus的通用Mapper
        Page<Seller> querySellerPage = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        IPage<Seller> sellerPage = sellerService.page(querySellerPage);

        // 3、使用MyBatisPlus的自定义分页
        // IPage<Seller> sellerPage = sellerService.selectSellerPage(querySellerPage, 1);

        ModelAndView modelAndView = new ModelAndView("/seller/index.html");
        // modelAndView.addObject("data", sellerPageInfo);
        modelAndView.addObject("data", sellerPage);
        modelAndView.addObject(controllerAttributeName, controllerAttributeValue);
        modelAndView.addObject(controllerAttributeValue, "index");
        return modelAndView;
    }

    @RequestMapping("/createPage")
    @AdminPermission
    public ModelAndView createPage() {
        ModelAndView modelAndView = new ModelAndView("/seller/create.html");
        modelAndView.addObject(controllerAttributeName, controllerAttributeValue);
        modelAndView.addObject(controllerAttributeValue, "create");
        return modelAndView;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @AdminPermission
    public String create(@Valid SellerCreateReq sellerCreateReq, BindingResult bindingResult) throws BussinessException {
        if (bindingResult.hasErrors()){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, CommonUtils.processErrorString(bindingResult));
        }
        Seller seller = new Seller();
        seller.setName(sellerCreateReq.getName());
        sellerService.create(seller);
        return "redirect:/admin/seller/index";
    }

    @RequestMapping("/get")
    @ResponseBody
    @AdminPermission(produceType = "application/json")
    public CommonReturnType getSeller(@RequestParam("id") Integer id) throws BussinessException {
        Seller seller = sellerService.getById(id);
        if (seller == null) {
            // return CommonReturnType.create(EmBusinessError.PARAMETER_VALIDATION_ERROR, "fail");
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "id不合法/ 数据库中找不到此id");
        }
        return CommonReturnType.create(seller);
    }

    @RequestMapping(value="down",method = RequestMethod.POST)
    @ResponseBody
    @AdminPermission(produceType = "application/json")
    public CommonReturnType down(@RequestParam(value="id")Integer id) throws BussinessException {
        Seller seller = sellerService.changeStatus(id,1);
        return CommonReturnType.create(seller);
    }

    @RequestMapping(value="up",method = RequestMethod.POST)
    @ResponseBody
    @AdminPermission(produceType = "application/json")
    public CommonReturnType up(@RequestParam(value="id")Integer id) throws BussinessException {
        Seller seller = sellerService.changeStatus(id,0);
        return CommonReturnType.create(seller);
    }

}
