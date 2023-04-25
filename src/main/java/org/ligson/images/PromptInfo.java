package org.ligson.images;

import lombok.Data;

@Data
public class PromptInfo {
    private String prompt;
    private int style;
    private int display_freq;
//    private String gen_type;
//    private String aspect_ratio_width;
//    private String aspect_ratio_height;
//    private String aspect_ratio;
}