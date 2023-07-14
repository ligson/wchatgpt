package org.ligson.ichat.chatlog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.ligson.ichat.fw.simplecrud.domain.BaseEntity;

@Table(name = "chat_log")
@Comment("用户表")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatLog extends BaseEntity {
    @Column(nullable = false)
    @Comment("用户id")
    private String userId;
    @Column(nullable = false, length = 40000)
    @Comment("提示")
    private String prompt;
    @Column(nullable = false, length = 40000)
    @Comment("回复内容")
    private String ask;

    @Column(nullable = false)
    @Comment("耗时")
    private Long totalTime;
}
