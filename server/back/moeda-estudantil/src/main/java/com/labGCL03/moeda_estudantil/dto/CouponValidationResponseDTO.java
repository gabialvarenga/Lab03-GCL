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
public class CouponValidationResponseDTO {
    
    private String code;
    private Boolean isValid;
    private Boolean used;
    private String generatedDate;
    private String advantageName;
    private String advantageDescription;
    private String companyName;
    private String studentName;
    private String studentEmail;
    private String message;

    public CouponValidationResponseDTO(Coupon coupon) {
        this.code = coupon.getCode();
        this.isValid = !coupon.isUsed();
        this.used = coupon.isUsed();
        this.generatedDate = formatDate(coupon.getGeneratedDate());
        
        if (coupon.getAdvantage() != null) {
            this.advantageName = coupon.getAdvantage().getName();
            this.advantageDescription = coupon.getAdvantage().getDescription();
            
            if (coupon.getAdvantage().getCompany() != null) {
                this.companyName = coupon.getAdvantage().getCompany().getName();
            }
        }
        
        if (coupon.getStudent() != null) {
            this.studentName = coupon.getStudent().getName();
            this.studentEmail = coupon.getStudent().getEmail();
        }
        
        this.message = coupon.isUsed() 
            ? "⚠️ Este cupom já foi utilizado anteriormente" 
            : "✅ Cupom válido e disponível para uso";
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
