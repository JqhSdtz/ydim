package bean.chat;

import java.io.Serializable;

public class MessageBean implements Serializable {
    private String target;
    private int messageSeq;
    private String message;

    public MessageBean() {}

    public MessageBean(String target, int messageSeq, String message){
        this.target = target;
        this.messageSeq = messageSeq;
        this.message = message;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getMessageSeq() {
        return messageSeq;
    }

    public void setMessageSeq(int messageSeq) {
        this.messageSeq = messageSeq;
    }

    /**message为一个json消息的字符串*/
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
