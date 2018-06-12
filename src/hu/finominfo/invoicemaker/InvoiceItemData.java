package hu.finominfo.invoicemaker;

public class InvoiceItemData {
    
    private String product_id = "";
    private String order_item_sku = "";
    private int product_quantiy = 0;
    private int product_item_price = 0;
    private int product_final_price = 0;
    private String VAT = "AAM";

    public String getVAT() {
        return VAT;
    }

    public void setVAT(String VAT) {
        this.VAT = VAT;
    }
    
    
    public String getOrder_item_sku() {
        return order_item_sku;
    }

    public void setOrder_item_sku(String order_item_sku) {
        this.order_item_sku = order_item_sku;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getProduct_quantiy() {
        return product_quantiy;
    }

    public void setProduct_quantiy(int product_quantiy) {
        this.product_quantiy = product_quantiy;
    }

    public int getProduct_final_price() {
        return product_final_price;
    }

    public void setProduct_final_price(int product_final_price) {
        this.product_final_price = product_final_price;
    }

    public int getProduct_item_price() {
        return product_item_price;
    }

    public void setProduct_item_price(int product_item_price) {
        this.product_item_price = product_item_price;
    }
    
    
    

}
