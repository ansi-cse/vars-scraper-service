package com.resdii.vars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ANSI.
 */

@Data
@NoArgsConstructor
public class BrokerDTO {
    String _id;
    String url;
    String rawHtml;
    String type;
    String name;
    String email;
    String phone;
    String address;
    String description;
    String logoUrl;
}
