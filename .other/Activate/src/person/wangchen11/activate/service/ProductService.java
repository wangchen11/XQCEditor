package person.wangchen11.activate.service;

import com.smart.framework.bean.Pager;

import person.wangchen11.activate.bean.ProductBean;
import person.wangchen11.activate.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {

    List<Product> getProductList();

    Product getProduct(long productId);

    ProductBean getProductBean(long productId);

    boolean createProduct(Map<String, Object> productFieldMap);

    boolean updateProduct(long productId, Map<String, Object> productFieldMap);

    boolean deleteProduct(long productId);

    Pager<ProductBean> searchProductPager(int pageNumber, int pageSize, Map<String, String> formFieldMap);
}
