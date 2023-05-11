package org.ligson.ichat.common.request.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RequestHeaders implements Serializable {
    private List<RequestHeader> headers = new ArrayList<>();
}
