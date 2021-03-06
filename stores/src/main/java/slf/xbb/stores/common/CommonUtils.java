package slf.xbb.stores.common;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.github.pagehelper.Page;

import java.math.BigDecimal;

/**
 * @author ：xbb
 * @date ：Created in 2020/4/29 4:12 下午
 * @description：公共方法
 * @modifiedBy：
 * @version:
 */
public class CommonUtils {

    /**
     * 地球平均半径
     */
    private static final double EARTH_RADIUS = (6378137);

    /**
     * 把经纬度转为度（°）
     * @param longitude / latitude
     * @return
     */
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }
    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return
     */
    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2){
        double radLat1 = rad(latitude1);
        double radLat2 = rad(latitude2);
        double a = radLat1 - radLat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a/2),2)
                                + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)
                )
        );
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public static String processErrorString(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            // 使用,逗号拼接
            stringBuilder.append(fieldError.getDefaultMessage() + ",");
        }
        // 将最后一个,逗号去掉
        stringBuilder.subSequence(0, stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * 在我们使用github的pageHelper的时候，我们查出来的是PageInfo的PO对象，
     * 我们往往在上层需要PageInfo的DTO或VO对象，这里我写了一个工具类来优雅的转化PageInfo类型
     * PageHelper.startPage();
     * List<Product> list = productMapper.select(product);
     * PageInfo pageInfo = new PageInfo(list);
     * PageInfo<ProductDTO> productDTOPageInfo = PageInfoUtil.pageInfo2PageInfoDTO(pageInfo, ProductDTO.class);
     *
     * @param pageInfoPO
     * @param dClass
     * @param <P>
     * @param <D>
     * @return
     */
    public static <P, D> PageInfo<D> pageInfo2PageInfoDTO(PageInfo<P> pageInfoPO, Class<D> dClass) {
        Page<D> page = new Page<>(pageInfoPO.getPageNum(), pageInfoPO.getPageSize());
        page.setTotal(pageInfoPO.getTotal());
        pageInfoPO.getList().forEach(p -> {
            D d = null;
            try {
                d = dClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(p, d);
            page.add(d);
        });
        // for (P p : pageInfoPO.getList()) {
        //     D d = null;
        //     try {
        //         d = dClass.newInstance();
        //     } catch (InstantiationException e) {
        //         e.printStackTrace();
        //     } catch (IllegalAccessException e) {
        //         e.printStackTrace();
        //     }
        //     BeanUtils.copyProperties(p, d);
        //     page.add(d);
        // }
        return new PageInfo<>(page);
    }
}
