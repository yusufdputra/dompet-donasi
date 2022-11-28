package com.mydonate.data;

public class TransaksiPembayaranData {

    String order_id, payment_type, status_message, transaction_id, total_bayar, transaction_time, status_code, id_donatur, product_name, url_pdf;

    public TransaksiPembayaranData() {
    }

    public TransaksiPembayaranData(String order_id, String payment_type, String status_message, String transaction_id, String total_bayar, String transaction_time, String status_code, String id_donatur, String product_name, String url_pdf) {
        this.order_id = order_id;
        this.payment_type = payment_type;
        this.status_message = status_message;
        this.transaction_id = transaction_id;
        this.total_bayar = total_bayar;
        this.transaction_time = transaction_time;
        this.status_code = status_code;
        this.id_donatur = id_donatur;
        this.product_name = product_name;
        this.url_pdf = url_pdf;
    }

    public String getUrl_pdf() {
        return url_pdf;
    }

    public void setUrl_pdf(String url_pdf) {
        this.url_pdf = url_pdf;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getId_donatur() {
        return id_donatur;
    }

    public void setId_donatur(String id_donatur) {
        this.id_donatur = id_donatur;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTotal_bayar() {
        return total_bayar;
    }

    public void setTotal_bayar(String total_bayar) {
        this.total_bayar = total_bayar;
    }

    public String getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(String transaction_time) {
        this.transaction_time = transaction_time;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }
}
