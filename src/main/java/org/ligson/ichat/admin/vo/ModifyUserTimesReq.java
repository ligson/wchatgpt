package org.ligson.ichat.admin.vo;

import lombok.Data;
import org.ligson.ichat.enums.UserType;

@Data
public class ModifyUserTimesReq {
    private String id;
    private int times;
}
