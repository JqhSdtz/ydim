package dao.mybatisImpl.chat.mapper;

import bean.chat.MessageBean;

import java.util.List;

public interface MessageMapper {
    void insertOfflineMessage(MessageBean message);

    List<MessageBean> selectMessagesByTargetId(String target);

    void deleteMessagesByTargetId(String target);

    Object selectMaxMessageSeq(String target);
}
