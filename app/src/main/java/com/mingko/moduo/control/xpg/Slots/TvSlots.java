package com.mingko.moduo.control.xpg.Slots;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public class TvSlots extends SlotsEntity{

    private String volume;
    private String channel;
    private String channelName;
    private String page;
    private String button;

    static {
        byte bParam = byteParam;
        mapParam.put("volume", bParam++);//#2
        mapParam.put("channel", bParam++);
        mapParam.put("channelName", bParam++);
        mapParam.put("page", bParam++);
        mapParam.put("button", bParam++);
        mapParam.put("src", bParam++);
        byte bValue = byteValue;
        mapValue.put("MAX", bValue++);//#15
        mapValue.put("MIN", bValue++);
        mapValue.put("静音", bValue++);
        mapValue.put("中央一台", bValue++);
    }

    @Override
    public void initParamValue() {
        if(volume != null && !volume.isEmpty()){
            param = mapParam.get("volume");
            value = calValue(volume);
        }else if(channel != null && !channel.isEmpty()){
            param = mapParam.get("channel");
            value = calValue(channel);
        }else if(channelName != null && !channelName.isEmpty()){
            param = mapParam.get("channelName");
            value = calValue(channelName);
        }else if(page != null && !page.isEmpty()){
            param = mapParam.get("page");
            value = calValue(page);
        }else if(button != null && !button.isEmpty()){
            param = mapParam.get("button");
            value = calValue(button);
        }
    }

    public String getChannel() {
        return channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getVolume() {
        return volume;
    }

    public String getPage() {
        return page;
    }

    public String getButton() {
        return button;
    }
}
