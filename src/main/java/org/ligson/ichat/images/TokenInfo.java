package org.ligson.ichat.images;

import lombok.Data;

@Data
public class TokenInfo {

    private String access_token;

    private String expires_in;

    private String refresh_token;

    private String id_token;

    private String user_id;

    private String project_id;
}
