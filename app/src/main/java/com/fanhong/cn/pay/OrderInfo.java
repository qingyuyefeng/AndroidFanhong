package com.fanhong.cn.pay;

public class OrderInfo {

	public String outtradeno;     //订单号
	public String productname;    // 商品名称
	public String desccontext;    // 商品详情
	public String totalamount;    // 商品金额

	public OrderInfo(String productname, String desccontext, String totalamount) {
		this.productname = productname;
		this.desccontext = desccontext;
		this.totalamount = totalamount;
	}

	public String getOuttradeno() {
		return outtradeno;
	}

	public void setOuttradeno(String outtradeno) {
		this.outtradeno = outtradeno;
	}

	@Override
	public String toString() {
		return "OrderInfo{" +
				", productname='" + productname + '\'' +
				", desccontext='" + desccontext + '\'' +
				", totalamount='" + totalamount + '\'' +
				'}';
	}
}
