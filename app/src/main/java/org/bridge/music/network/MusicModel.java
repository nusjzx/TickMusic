package org.bridge.music.network;

/**
 * Music相关的业务层组件
 */

public class MusicModel {

    private static MusicModel sDefault;

    public static MusicModel getDefault() {
        if (null == sDefault) {
            sDefault = new MusicModel();
        }
        return sDefault;
    }

}
