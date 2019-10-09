package person.wangchen11.activate.entity;

import com.smart.framework.base.BaseEntity;

public class ProductType extends BaseEntity {

    private String productTypeName;

    private String productTypeCode;

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }
}
