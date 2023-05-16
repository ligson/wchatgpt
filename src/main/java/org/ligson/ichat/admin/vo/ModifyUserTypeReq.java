package org.ligson.ichat.admin.vo;

import lombok.Data;
import org.ligson.ichat.enums.UserLevel;
import org.ligson.ichat.enums.UserType;

@Data
public class ModifyUserTypeReq {
    private String id;
    private UserType userType;
}
