package com.example.authsystem.service.impl;

import com.example.authsystem.dto.CouponDTO;
import com.example.authsystem.entity.Coupon;
import com.example.authsystem.repository.CouponRepository;
import com.example.authsystem.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public CouponDTO createCoupon(CouponDTO couponDTO) {
        Coupon coupon = convertToEntity(couponDTO);
        Coupon savedCoupon = couponRepository.save(coupon);
        return convertToDTO(savedCoupon);
    }

    @Override
    public CouponDTO updateCoupon(Long id, CouponDTO couponDTO) {
        Optional<Coupon> existingCouponOptional = couponRepository.findById(id);
        if (existingCouponOptional.isPresent()) {
            Coupon existingCoupon = existingCouponOptional.get();
            existingCoupon.setName(couponDTO.getName());
            existingCoupon.setCouponType(couponDTO.getCouponType());
            existingCoupon.setExpireTime(couponDTO.getExpireTime());
            existingCoupon.setDiscountType(couponDTO.getDiscountType());
            existingCoupon.setDiscountValue(couponDTO.getDiscountValue());
            existingCoupon.setMinOrderAmount(couponDTO.getMinOrderAmount());

            Coupon updatedCoupon = couponRepository.save(existingCoupon);
            return convertToDTO(updatedCoupon);
        } else {
            throw new RuntimeException("Coupon not found with id: " + id);
        }
    }

    @Override
    public void deleteCoupon(Long id) {
        if (couponRepository.existsById(id)) {
            couponRepository.deleteById(id);
        } else {
            throw new RuntimeException("Coupon not found with id: " + id);
        }
    }

    @Override
    public Optional<CouponDTO> getCouponById(Long id) {
        return couponRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public List<CouponDTO> getCouponsByType(String type) {
        List<Coupon> coupons = couponRepository.findByCouponType(type);
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponDTO> getValidCoupons() {
        List<Coupon> coupons = couponRepository.findByExpireTimeAfter(LocalDateTime.now());
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponDTO> getCouponsByIds(List<Long> ids) {
        List<Coupon> coupons = couponRepository.findByIds(ids);
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponDTO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CouponDTO convertToDTO(Coupon coupon) {
        CouponDTO couponDTO = new CouponDTO();
        couponDTO.setId(coupon.getId());
        couponDTO.setName(coupon.getName());
        couponDTO.setCouponType(coupon.getCouponType());
        couponDTO.setExpireTime(coupon.getExpireTime());
        couponDTO.setDiscountType(coupon.getDiscountType());
        couponDTO.setDiscountValue(coupon.getDiscountValue());
        couponDTO.setMinOrderAmount(coupon.getMinOrderAmount());
        couponDTO.setCreatedAt(coupon.getCreatedAt());
        couponDTO.setUpdatedAt(coupon.getUpdatedAt());
        return couponDTO;
    }

    @Override
    public Coupon convertToEntity(CouponDTO couponDTO) {
        Coupon coupon = new Coupon();
        coupon.setName(couponDTO.getName());
        coupon.setCouponType(couponDTO.getCouponType());
        coupon.setExpireTime(couponDTO.getExpireTime());
        coupon.setDiscountType(couponDTO.getDiscountType());
        coupon.setDiscountValue(couponDTO.getDiscountValue());
        coupon.setMinOrderAmount(couponDTO.getMinOrderAmount());
        return coupon;
    }
}