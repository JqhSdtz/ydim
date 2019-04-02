package dao.productor.chat;

import bean.chat.MessageBean;

import java.util.List;

/**抽象产品*/

public interface MessageDao {
    void saveOfflineMessage(MessageBean message);

    List<MessageBean> getOfflineMessageList(String target);

    Object getMaxMessageSeq(String target);
}
