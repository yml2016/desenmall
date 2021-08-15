package com.desen.common.exception;


public class NotStockException extends RuntimeException{

	private Long skuId;

	public NotStockException(String msg) {
		super(msg);
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}
}
