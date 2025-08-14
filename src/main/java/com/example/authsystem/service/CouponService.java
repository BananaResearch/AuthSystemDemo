package com.example.authsystem.service;

import com.example.authsystem.dto.CouponDTO;
import com.example.authsystem.entity.Coupon;
import java.util.List;
import java.util.Optional;

public interface CouponService {
    // 创建优惠券
    CouponDTO createCoupon(CouponDTO couponDTO);

    // 更新优惠券
    CouponDTO updateCoupon(Long id, CouponDTO couponDTO);

    // 删除优惠券
    void deleteCoupon(Long id);

    // 根据ID查询优惠券
    Optional<CouponDTO> getCouponById(Long id);

    // 根据类型查询优惠券
    List<CouponDTO> getCouponsByType(String type);

    // 获取所有未过期优惠券
    List<CouponDTO> getValidCoupons();

    // 根据多个ID查询优惠券
    List<CouponDTO> getCouponsByIds(List<Long> ids);

    // 获取所有优惠券
    List<CouponDTO> getAllCoupons();

    // 实体转DTO
    CouponDTO convertToDTO(Coupon coupon);

    // DTO转实体
    Coupon convertToEntity(CouponDTO couponDTO);
}