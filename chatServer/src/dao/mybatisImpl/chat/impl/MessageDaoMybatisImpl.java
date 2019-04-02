package dao.mybatisImpl.chat.impl;

import bean.chat.MessageBean;
import dao.mybatisImpl.chat.mapper.MessageMapper;
import dao.productor.chat.MessageDao;
import org.apache.ibatis.session.SqlSession;
import util.MybatisUtil;

import java.util.List;

public class MessageDaoMybatisImpl implements MessageDao {
    /*由于发送源用户的信息已经存到了message中，所以只需要知道消息要发送给谁就可以*/

    @Override
    public void saveOfflineMessage(MessageBean message){
        SqlSession session = MybatisUtil.getSession();
        MessageMapper messageMapper = session.getMapper(MessageMapper.class);
        try {
            messageMapper.insertOfflineMessage(message);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<MessageBean> getOfflineMessageList(String target){
        List<MessageBean> messageList = null;
        SqlSession session = MybatisUtil.getSession();
        MessageMapper messageMapper = session.getMapper(MessageMapper.class);
        try {
            messageList = messageMapper.selectMessagesByTargetId(target);
            messageMapper.deleteMessagesByTargetId(target);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return messageList;
    }

    @Override
    public Integer getMaxMessageSeq(String target){
        SqlSession session = MybatisUtil.getSession();
        MessageMapper messageMapper = session.getMapper(MessageMapper.class);
        Integer maxSeq = 0;
        try {
            Object obj = messageMapper.selectMaxMessageSeq(target);
            if(obj != null)
                maxSeq = (Integer) obj;
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return maxSeq;
    }

}
