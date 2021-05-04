package com.desen.desenmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-03-21 17:56:46
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotNull(message = "品牌logo不能为空")
	@URL(message = "品牌logo地址格式不对")
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	//@TableLogic//(value="1", delval = "0")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotNull(message = "检索首字母不能为空")
	@Pattern(regexp="^[a-zA-Z]$", message = "必须为一个首字母")
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序字段不能为空")
	@Min(value = 0, message = "排序字段必须大于等于0的争整数")
	private Integer sort;

}
