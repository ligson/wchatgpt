package org.ligson.images;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class DrawResult {
    private String id;
    private String user_id;
    private String state;
    private PromptInfo input_spec;
    private boolean premium;
    private String created_at;
    private String updated_at;
    private boolean is_nsfw;
    private List<String> photo_url_list;
    private List<String> generated_photo_keys;
    private ResultInfo result;
}

@Data
class ResultInfo {
    @JsonAlias("final")
    private String finalValue;
}
