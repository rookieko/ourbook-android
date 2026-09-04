package com.example.ourbook.DataTool.Response;

import java.util.Map;

public class NormalResponseDTO extends ResponseDTO{
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
    }

    @Override
    public int getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(int status) {
        super.setStatus(status);
    }

    @Override
    public boolean isSuccess() {
        return super.isSuccess();
    }

    @Override
    public void setSuccess(boolean success) {
        super.setSuccess(success);
    }


    @Override
    public Map<String, Object> getData() {
        return super.getData();
    }

    @Override
    public void setData(Map<String, Object> data) {
        super.setData(data);
    }
}
