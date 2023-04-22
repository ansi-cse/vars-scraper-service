package com.resdii.vars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class InvestorDTO {
    String investorName;
    String investorAddress;
    String investorPrimaryField;
    String investorSubField;
    String investorEmail;
    String investorWebsite;
    String investorDescription;
    String logoUrl;
    String phone;
    String rawHtml;
    String url;
}
