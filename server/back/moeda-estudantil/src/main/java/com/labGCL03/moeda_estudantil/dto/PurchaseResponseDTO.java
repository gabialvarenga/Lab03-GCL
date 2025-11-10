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
public class PurchaseResponseDTO {
    
    private String code;
    private AdvantageResponseDTO advantage;
    private StudentResponseDTO student;
    private String purchaseDate;

    public PurchaseResponseDTO(Coupon coupon) {
        this.code = coupon.getCode();
        this.advantage = new AdvantageResponseDTO(coupon.getAdvantage());
        this.student = new StudentResponseDTO(coupon.getStudent());
        this.purchaseDate = formatDate(coupon.getGeneratedDate());
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
