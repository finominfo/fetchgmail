package hu.finominfo.invoicemaker;

import java.util.List;

public class InvoiceData {

    private String order_id = "";
    private String user_id = "";

    private String order_subtotal = "";
    private String order_tax = "";
    private String order_total = "";
    private String order_currency = "";
    private String order_status = "";
    private String ship_method_id = "";

    private List<InvoiceItemData> orderItems = null;

    private String company = "";
    private String phone_1 = "";
    private String phone_2 = "";
    private String fax = "";
    private String address_1 = "";
    private String address_2 = "";
    private String city = "";
    private String country = "";
    private String zip = "";
    private String user_email = "";

    private String VAT = "AAM";

    private String taxNo = "";
    
    private String invoiceIssuer = "Finominfo Bt";

    public String getInvoiceIssuer() {
        return invoiceIssuer;
    }

    public void setInvoiceIssuer(String invoiceIssuer) {
        this.invoiceIssuer = invoiceIssuer;
    }
    

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getVAT() {
        return VAT;
    }

    public void setVAT(String VAT) {
        this.VAT = VAT;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public List<InvoiceItemData> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<InvoiceItemData> orderItems) {
        this.orderItems = orderItems;
    }

    public String getOrder_currency() {
        return order_currency;
    }

    public void setOrder_currency(String order_currency) {
        this.order_currency = order_currency;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_subtotal() {
        return order_subtotal;
    }

    public void setOrder_subtotal(String order_subtotal) {
        this.order_subtotal = order_subtotal;
    }

    public String getOrder_tax() {
        return order_tax;
    }

    public void setOrder_tax(String order_tax) {
        this.order_tax = order_tax;
    }

    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public String getPhone_1() {
        return phone_1;
    }

    public void setPhone_1(String phone_1) {
        this.phone_1 = phone_1;
    }

    public String getPhone_2() {
        return phone_2;
    }

    public void setPhone_2(String phone_2) {
        this.phone_2 = phone_2;
    }

    public String getShip_method_id() {
        return ship_method_id;
    }

    public void setShip_method_id(String ship_method_id) {
        this.ship_method_id = ship_method_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}
