package com.labGCL03.moeda_estudantil.dto;

import com.labGCL03.moeda_estudantil.entities.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {
    
    private Long id;
    private String code;
    private String generatedDate;
    private Boolean used;
    private String advantageName;
    private String companyName;
    private String studentName;

    public CouponResponseDTO(Coupon coupon) {
        this.id = coupon.getId();
        this.code = coupon.getCode();
        this.generatedDate = formatDate(coupon.getGeneratedDate());
        this.used = coupon.isUsed();
        this.advantageName = coupon.getAdvantage() != null ? coupon.getAdvantage().getName() : null;
        this.companyName = coupon.getAdvantage() != null && coupon.getAdvantage().getCompany() != null 
            ? coupon.getAdvantage().getCompany().getName() : null;
        this.studentName = coupon.getStudent() != null ? coupon.getStudent().getName() : null;
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
