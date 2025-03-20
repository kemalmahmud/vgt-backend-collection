package com.vgt.collections.Utils;

import com.vgt.collections.Model.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {
    public static ResponseEntity<BaseResponse> success(Object data, String message) {
        BaseResponse res = new BaseResponse();
        res.setStatus(HttpStatus.OK.value());
        res.setMessage(message);
        res.setData(data);
        return ResponseEntity.ok(res);
    }
}
