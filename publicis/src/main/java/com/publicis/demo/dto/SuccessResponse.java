package com.publicis.demo.dto;

import lombok.*;
import org.json.JSONObject;

@Getter
@Setter
@NoArgsConstructor
public class SuccessResponse implements ApiResponse{
    private String status;
    private String data;

    public SuccessResponse(String status,JSONObject data){
        this.status = status;
        this.data = data.toString();
    }
}
