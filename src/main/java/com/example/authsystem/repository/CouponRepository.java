package com.example.authsystem.repository;

import com.example.authsystem.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // 根据优惠券类型查询
    List<Coupon> findByCouponType(String couponType);

    // 根据过期时间查询（未过期的优惠券）
    List<Coupon> findByExpireTimeAfter(LocalDateTime now);

    // 根据优惠券类型和未过期状态查询
    List<Coupon> findByCouponTypeAndExpireTimeAfter(String couponType, LocalDateTime now);

    // 根据多个ID查询优惠券
    @Query("SELECT c FROM Coupon c WHERE c.id IN :ids")
    List<Coupon> findByIds(@Param("ids") List<Long> ids);
}