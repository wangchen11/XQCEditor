package person.wangchen11.activate.action;

import com.smart.framework.annotation.Bean;
import com.smart.framework.annotation.Inject;
import com.smart.framework.annotation.Request;
import com.smart.framework.base.BaseAction;
import com.smart.framework.bean.Pager;
import com.smart.framework.bean.Result;
import com.smart.framework.util.CastUtil;
import com.smart.framework.util.WebUtil;

import person.wangchen11.activate.bean.ProductBean;
import person.wangchen11.activate.service.ProductService;

import java.util.Map;

@Bean
public class ProductAction extends BaseAction {

    @Inject
    private ProductService productService;

    @Request("get:/product")
    public Result index() {
        Pager<ProductBean> productBeanPager = productService.searchProductPager(1, 10, null);
        return new Result(true).data(productBeanPager);
    }

    @Request("post:/product/search")
    public Result search(Map<String, Object> fieldMap) {
        int pageNumber = CastUtil.castInt(fieldMap.get(PAGE_NUMBER));
        int pageSize = CastUtil.castInt(fieldMap.get(PAGE_SIZE));
        String queryString = CastUtil.castString(fieldMap.get(QUERY_STRING));

        Map<String, String> queryMap = WebUtil.createQueryMap(queryString);

        Pager<ProductBean> productBeanPager = productService.searchProductPager(pageNumber, pageSize, queryMap);
        return new Result(true).data(productBeanPager);
    }

    @Request("get:/product/view/{id}")
    public Result view(long id) {
        if (id == 0) {
            return new Result(false).error(ERROR_PARAM);
        }
        ProductBean productBean = productService.getProductBean(id);
        if (productBean != null) {
            return new Result(true).data(productBean);
        } else {
            return new Result(false).error(ERROR_DATA);
        }
    }

    @Request("post:/product/create")
    public Result create(Map<String, Object> fieldMap) {
        boolean success = productService.createProduct(fieldMap);
        return new Result(success);
    }

    @Request("put:/product/update/{id}")
    public Result update(long id, Map<String, Object> fieldMap) {
        if (id == 0) {
            return new Result(false).error(ERROR_PARAM);
        }
        boolean success = productService.updateProduct(id, fieldMap);
        return new Result(success);
    }

    @Request("delete:/product/delete/{id}")
    public Result delete(long id) {
        if (id == 0) {
            return new Result(false).error(ERROR_PARAM);
        }
        boolean success = productService.deleteProduct(id);
        return new Result(success);
    }
}
