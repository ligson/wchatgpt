package org.ligson.ichat.common.request.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestHeader implements Serializable {
    private String name;
    private String value;
}
