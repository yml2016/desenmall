package com.desen.desenmall.order.feign;

import com.desen.desenmall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient("desenmall-member")
public interface MemberFeignService {

	@GetMapping("/member/memberreceiveaddress/{memberId}/addresses")
	List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);

}
