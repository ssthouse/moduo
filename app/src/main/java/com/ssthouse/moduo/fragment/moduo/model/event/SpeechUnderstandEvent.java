package com.ssthouse.moduo.fragment.moduo.model.event;

/**
 * 语义理解的结果事件
 * Created by ssthouse on 2016/3/17.
 */
public class SpeechUnderstandEvent {

    //是否语义理解成功
    private boolean success;

    //语义理解的json解析结果
    private String jsonResult;

    public SpeechUnderstandEvent(boolean success, String jsonResult) {
        this.success = success;
        this.jsonResult = jsonResult;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }
}
