package dao.mybatisImpl;

import bean.user.UserBean;
import bean.user.UserInfoBean;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContactsMapResultHandler implements ResultHandler {
    @SuppressWarnings("rawtypes")
    private final Map mappedResults = new HashMap();

    @SuppressWarnings("unchecked")
    @Override
    public void handleResult(ResultContext context) {
        @SuppressWarnings("rawtypes")
        Map map = (Map) context.getResultObject();
        String userId = (String)map.get("userId");
        String username = (String)map.get("username");
        String profileIconUrl = (String)map.get("profileIconUrl");
        String intro = (String)map.get("intro");
        Timestamp latestContactTime = new Timestamp((Long)map.get("latestContactTime")) ;
        UserBean contact = new UserBean(userId, username, null, latestContactTime);
        contact.setUserInfo(new UserInfoBean(profileIconUrl, intro));
        mappedResults.put(userId, contact);  // xml 配置里面的property的值，对应的列
    }
    public Map getMappedResults() {
        return mappedResults;
    }
}
