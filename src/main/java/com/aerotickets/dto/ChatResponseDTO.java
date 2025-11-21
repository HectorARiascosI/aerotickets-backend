package com.aerotickets.dto;

import java.util.List;

public class ChatResponseDTO {
    private String response;
    private String action; // "search", "info", "help", null
    private Object data; // Datos adicionales según la acción

    public ChatResponseDTO() {
    }

    public ChatResponseDTO(String response) {
        this.response = response;
    }

    public ChatResponseDTO(String response, String action, Object data) {
        this.response = response;
        this.action = action;
        this.data = data;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
