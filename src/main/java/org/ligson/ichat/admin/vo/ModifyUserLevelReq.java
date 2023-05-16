package org.ligson.ichat.admin.vo;

import lombok.Data;
import org.ligson.ichat.enums.UserLevel;

@Data
public class ModifyUserLevelReq {
    private String id;
    private UserLevel level;
}
