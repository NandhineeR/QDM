package com.qdm.cg.clients.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfoDto {

    private String client_name;
    private String gender;
    private String age;
    private String mobile_no;
    private String latitude;
    private String longitude;
 
}
