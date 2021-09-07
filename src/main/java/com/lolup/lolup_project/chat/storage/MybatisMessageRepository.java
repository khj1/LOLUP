package com.lolup.lolup_project.chat.storage;

import com.lolup.lolup_project.chat.model.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MybatisMessageRepository extends MessageRepository{

    @Override
    Long save(ChatMessage message);

    @Override
    ChatMessage findById(@Param("_messageId") Long messageId);
}
