package com.desen.desenmall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class FareVo {

	private MemberAddressVo memberAddressVo;

	private BigDecimal fare;
}
