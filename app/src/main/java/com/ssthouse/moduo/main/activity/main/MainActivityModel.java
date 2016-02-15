package com.ssthouse.moduo.main.activity.main;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.ssthouse.moduo.main.control.util.CloudUtil;
import com.ssthouse.moduo.main.model.bean.ModuoInfo;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Model
 * Created by ssthouse on 2016/2/15.
 */
public class MainActivityModel {

    public Observable<ModuoInfo> getUserInfo(String did) {
        return Observable.just(did)
                .map(new Func1<String, ModuoInfo>() {
                    @Override
                    public ModuoInfo call(String did) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(CloudUtil.TABLE_MODUO_DEVICE);
                        query.whereEqualTo(CloudUtil.KEY_DID, did);
                        AVObject moduoObject = null;
                        try {
                            moduoObject = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        if (moduoObject == null) {
                            Timber.e("服务器端无该设备信息!");
                            return null;
                        }
                        return new ModuoInfo(moduoObject.getString(CloudUtil.KEY_DID),
                                moduoObject.getString(CloudUtil.KEY_PASSCODE),
                                moduoObject.getString(CloudUtil.KEY_CID),
                                moduoObject.getString(CloudUtil.KEY_VIDEO_USERNAME),
                                moduoObject.getString(CloudUtil.KEY_VIDEO_PASSWORD));
                    }
                });
    }

}
