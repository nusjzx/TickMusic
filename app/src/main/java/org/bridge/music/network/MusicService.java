package org.bridge.music.network;

/**
 * 定义所有的服务端接口
 *
 * @author Bridge
 */

public interface MusicService {

    //获取启动图，bing每日一图
    void getSplash();
    //下载歌曲
    void downloadFile();

    //获取歌曲列表
    void getSongListInfo();

    //获取歌曲下载信息
    void getMusicDownloadInfo();

    //根据歌曲Id 获取歌词
    void getLrc();

    //搜索
    void searchMusic();

    //获取歌手信息
    void getArtistInfo();
}
