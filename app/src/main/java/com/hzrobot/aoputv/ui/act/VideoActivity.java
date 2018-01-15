package com.hzrobot.aoputv.ui.act;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.VideoView;

import com.hzrobot.aoputv.R;
import com.hzrobot.aoputv.adapter.VideoListAdapter;
import com.hzrobot.aoputv.global.Constants;
import com.hzrobot.aoputv.model.event.RobotCommand;
import com.hzrobot.aoputv.model.video.Video;
import com.hzrobot.aoputv.ui.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shijiwei on 2018/1/11.
 *
 * @VERSION 1.0
 */

public class VideoActivity extends BaseActivity implements MediaPlayer.OnCompletionListener {

    private final int PLAY_PREVIOUS = -1;
    private final int PLAY_NEXT = -2;

    /* Widget*/
    private VideoView mVideoPlayer;
    private View mEmptyView;
    private ListView lvVideo;

    /* Data */
    private List<Video> mVideoSet = new ArrayList<>();
    private Video video;
    private boolean isLoadVideoPathSuccess = false;

    private VideoListAdapter videoListAdapter;

    private Handler mLoadVideoLooper = new Handler();
    private Runnable mLoadVideoRunnable = new Runnable() {
        @Override
        public void run() {
            isLoadVideoPathSuccess = loadVideoPath(mVideoSet, Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DIR_video);
            video = getVideo(mVideoSet);
        }
    };

    @Override
    public int getLayoutResId() {
        return R.layout.activity_video;
    }

    @Override
    public void initialData(Bundle savedInstanceState) {

        mEnter = AnimationUtils.loadAnimation(this, R.anim.video_lv_in);
        mOuter = AnimationUtils.loadAnimation(this, R.anim.video_lv_out);
        mOuter.setAnimationListener(al);

        isLoadVideoPathSuccess = loadVideoPath(mVideoSet, Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DIR_video);
        video = getVideo(mVideoSet);
    }

    @Override
    public void initialView() {
        mEmptyView = findViewById(R.id.empty_view);
        mVideoPlayer = (VideoView) findViewById(R.id.video_player);
        if (video != null) {
            mVideoPlayer.setVideoPath(video.getPath());
        }

        lvVideo = (ListView) findViewById(R.id.lv_video);
        videoListAdapter = new VideoListAdapter(this, mVideoSet);
        lvVideo.setAdapter(videoListAdapter);


    }

    @Override
    public void initialEvn() {
        mEmptyView.setVisibility(isLoadVideoPathSuccess ? View.GONE : View.VISIBLE);
        mVideoPlayer.setOnCompletionListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (video != null) {
            mVideoPlayer.seekTo(video.getPlaySeek());
            mVideoPlayer.start();
            showVideoMenu();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (video != null) {
            mVideoPlayer.pause();
            video.setPlaySeek(mVideoPlayer.getCurrentPosition());
        }
    }

    @Override
    public void onBackKeyPressed() {
//        play(mVideoSet, PLAY_NEXT);
        if (lvVideo.getVisibility() == View.VISIBLE) {
            hideVideoMenu(0);
        } else {
            showVideoMenu();
        }
    }

    @Override
    public void onNetworkStateChanged(int type, boolean isAvailable) {

    }

    @Override
    public void onReceiveRobotCommand(RobotCommand cmd) {

        switch (cmd.getAction()) {
            case RobotCommand.ACTION_PREVIOUS_ONE:
                play(mVideoSet, PLAY_PREVIOUS);
                break;
            case RobotCommand.ACTION_NEXT_ONE:
                play(mVideoSet, PLAY_NEXT);
                break;
            case RobotCommand.ACTION_SPECIFIC_ONE:
                play(mVideoSet, cmd.getStep());
                break;
            case RobotCommand.ACTION_SPECIFIC_COMMAND:
                if (cmd.getCommand().contains("视频列表") || cmd.getCommand().contains("视频菜单")) {
                    showVideoMenu();
                }
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        play(mVideoSet, PLAY_NEXT);
    }

    /**
     * 加载本地视频路径
     */
    private boolean loadVideoPath(List<Video> videoSet, String videoDirPath) {
        File videoDir = new File(videoDirPath);
        if (videoDir.isDirectory()) {
            File[] videoFiles = videoDir.listFiles();
            if (videoFiles != null && videoFiles.length != 0) {
                for (int i = 0; i < videoFiles.length; i++) {
                    String path = videoFiles[i].getAbsolutePath();
                    String name = videoFiles[i].getName();
                    videoSet.add(new Video(path, name));
                }
                videoSet.get(0).setPlaying(true);
                return true;
            }
        }

        return false;
    }

    private Video getVideo(List<Video> videoPath) {
        if (videoPath == null || videoPath.size() == 0) return null;
        for (int i = 0; i < videoPath.size(); i++) {
            if (videoPath.get(i).isPlaying()) {
                return videoPath.get(i);
            }
        }
        videoPath.get(0).setPlaying(true);
        return videoPath.get(0);
    }

    /**
     * -1 ,- 2 , > 0
     *
     * @param videos
     * @param position
     */
    private void selectVideo(List<Video> videos, int position) {

        if (videos == null || videos.size() == 0) return;

        int currentPosition = 0;
        int newPosition = 0;
        int videoTotalSize = videos.size();

        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).isPlaying()) {
                videos.get(i).setPlaying(false);
                currentPosition = i;
                break;
            }
        }

        if (position == PLAY_PREVIOUS) {
            // 上一个视频
            newPosition = currentPosition - 1;
            if (newPosition == -1) {
                newPosition = videoTotalSize - 1;
            }

        } else if (position == PLAY_NEXT) {
            // 下一个视频
            newPosition = currentPosition + 1;
            if (newPosition == videoTotalSize) {
                newPosition = 0;
            }

        } else if (position > 0) {
            // 指定下标的视频

            if (position < 0) {
                newPosition = 0;
            } else if (position > videoTotalSize) {
                newPosition = videoTotalSize - 1;
            } else {
                newPosition = position - 1;
            }

        }
        videos.get(newPosition).setPlaying(true);
    }

    /**
     * 指定下标播放
     *
     * @param videos
     * @param position
     */
    private void play(List<Video> videos, int position) {
        if (video != null)
            video.setPlaySeek(mVideoPlayer.getCurrentPosition());
        selectVideo(videos, position);
        video = getVideo(videos);
        if (video != null) {
            mVideoPlayer.setVideoPath(video.getPath());
//            mVideoPlayer.seekTo(video.getPlaySeek());
            mVideoPlayer.start();
        }
        mEmptyView.setVisibility(video == null ? View.VISIBLE : View.GONE);
        videoListAdapter.notifyDataSetChanged();

    }


    private Animation mEnter;
    private Animation mOuter;

    private Handler videoMenuHandler = new Handler();
    private Runnable videoMenuRunnable = new Runnable() {
        @Override
        public void run() {
            if (lvVideo.getVisibility() != View.GONE) {
                lvVideo.startAnimation(mOuter);
            }
        }
    };

    private void showVideoMenu() {
        if (lvVideo.getVisibility() != View.VISIBLE) {
            lvVideo.startAnimation(mEnter);
            lvVideo.setVisibility(View.VISIBLE);
            hideVideoMenu(7 * 1000);
        }
    }

    private void hideVideoMenu(long delay) {
        videoMenuHandler.removeCallbacks(videoMenuRunnable);
        videoMenuHandler.postDelayed(videoMenuRunnable, delay);
    }

    /* 动画监听者 */
    private Animation.AnimationListener al = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            lvVideo.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


}
