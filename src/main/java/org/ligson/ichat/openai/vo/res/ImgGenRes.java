package org.ligson.ichat.openai.vo.res;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImgGenRes {
    private long created;
    private List<ImgGenData> data = new ArrayList<>();
}
