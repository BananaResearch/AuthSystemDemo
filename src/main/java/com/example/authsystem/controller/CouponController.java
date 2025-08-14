package com.example.authsystem.controller;

import com.example.authsystem.dto.CouponDTO;
import com.example.authsystem.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // 创建优惠券
    @PostMapping
    public ResponseEntity<CouponDTO> createCoupon(@RequestBody CouponDTO couponDTO) {
        CouponDTO createdCoupon = couponService.createCoupon(couponDTO);
        return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
    }

    // 更新优惠券
    @PutMapping("/{id}")
    public ResponseEntity<CouponDTO> updateCoupon(@PathVariable Long id, @RequestBody CouponDTO couponDTO) {
        CouponDTO updatedCoupon = couponService.updateCoupon(id, couponDTO);
        return ResponseEntity.ok(updatedCoupon);
    }

    // 删除优惠券
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    // 根据ID查询优惠券
    @GetMapping("/{id}")
    public ResponseEntity<CouponDTO> getCouponById(@PathVariable Long id) {
        Optional<CouponDTO> couponDTO = couponService.getCouponById(id);
        return couponDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 根据类型查询优惠券
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CouponDTO>> getCouponsByType(@PathVariable String type) {
        List<CouponDTO> coupons = couponService.getCouponsByType(type);
        return ResponseEntity.ok(coupons);
    }

    // 获取所有未过期优惠券
    @GetMapping("/valid")
    public ResponseEntity<List<CouponDTO>> getValidCoupons() {
        List<CouponDTO> coupons = couponService.getValidCoupons();
        return ResponseEntity.ok(coupons);
    }

    // 根据多个ID查询优惠券
    @GetMapping("/ids")
    public ResponseEntity<List<CouponDTO>> getCouponsByIds(@RequestParam List<Long> ids) {
        List<CouponDTO> coupons = couponService.getCouponsByIds(ids);
        return ResponseEntity.ok(coupons);
    }

    // 获取所有优惠券
    @GetMapping
    public ResponseEntity<List<CouponDTO>> getAllCoupons() {
        List<CouponDTO> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }
}