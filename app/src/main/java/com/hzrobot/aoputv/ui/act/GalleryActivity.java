package com.hzrobot.aoputv.ui.act;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.hzrobot.aoputv.R;
import com.hzrobot.aoputv.adapter.Gallery3dAdapter;
import com.hzrobot.aoputv.global.Constants;
import com.hzrobot.aoputv.model.event.RobotCommand;
import com.hzrobot.aoputv.service.CommandServer;
import com.hzrobot.aoputv.ui.base.BaseActivity;
import com.hzrobot.aoputv.utils.log.LogUtil;
import com.hzrobot.aoputv.widget.Gallery3d;
import com.hzrobot.aoputv.widget.InvertedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    /* Widget */
    private Gallery3d gallery;
    private InvertedImageView mEmptyView;

    /* Data */
    private Gallery3dAdapter gallery3dAdapter;
    private List<String> _imagePathSet = new ArrayList<>();
    private Point displaySize = new Point();


    @Override
    public int getLayoutResId() {
        return R.layout.activity_galllery;
    }

    @Override
    public void initialData(Bundle savedInstanceState) {

        getWindowManager().getDefaultDisplay().getSize(displaySize);
        loadPicture(_imagePathSet, Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DIR_gallery);
    }

    @Override
    public void initialView() {
        startService(new Intent(this, CommandServer.class));
        gallery = (Gallery3d) findViewById(R.id.gallery_3d_frame);
        gallery3dAdapter = new Gallery3dAdapter(_imagePathSet, this);
        gallery.setAdapter(gallery3dAdapter);

        mEmptyView = (InvertedImageView) findViewById(R.id.empty_view);
//        mEmptyView.setLayoutParams(new FrameLayout.LayoutParams(displaySize.x / 9 * 2, displaySize.x / 4));
        mEmptyView.setVisibility(_imagePathSet.size() == 0 ? View.VISIBLE : View.GONE);

    }


    @Override
    public void initialEvn() {

    }

//    private int index;

    @Override
    public void onBackKeyPressed() {
//        if (index == 0) {
//            gallery.moveToSpecificPosition(2);
//        } else if (index == 1) {
//            gallery.moveToSpecificPosition(1);
//        } else {
//            gallery.moveToSpecificPosition(new Random().nextInt(gallery.getAdapter().getCount() - 2) + 1);
//        }
//
//        index++;
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, CommandServer.class));
        super.onDestroy();
    }

    @Override
    public void onNetworkStateChanged(int type, boolean isAvailable) {

    }

    @Override
    public void onReceiveRobotCommand(RobotCommand cmd) {

        int photoTotalSize = gallery.getAdapter().getCount();
        int currentPosition = gallery.getSelectedItemPosition();

        LogUtil.e(TAG, " onReceiveRobotCommand action: " + cmd.getAction()
                + "  , step :" + cmd.getStep()
                + "  command :" + cmd.getCommand()
                + "   ,photoTotalSize : " + photoTotalSize
                + "   ,currentPosition :" + currentPosition
        );

        LogUtil.e(TAG, " test : " + gallery.getChildCount());

        switch (cmd.getAction()) {
            case RobotCommand.ACTION_NEXT_ONE:
                if (cmd.getStep() == 1) {
                    gallery.moveToNext();
                } else {
                    int position = currentPosition + cmd.getStep() + 1;
                    moveToSpecificPositionWithFormatNumber(gallery, photoTotalSize, position);
                }
                break;
            case RobotCommand.ACTION_PREVIOUS_ONE:
                if (cmd.getStep() == 1) {
                    gallery.moveToPrevious();
                } else {
                    int position = currentPosition - cmd.getStep() + 1;
                    moveToSpecificPositionWithFormatNumber(gallery, photoTotalSize, position);
                }
                break;
            case RobotCommand.ACTION_SPECIFIC_ONE:
                if (cmd.getCommand().contains("最后")) {
                    gallery.moveToSpecificPosition(photoTotalSize);
                } else {
                    int position = cmd.getStep();
                    moveToSpecificPositionWithFormatNumber(gallery, photoTotalSize, position);
                }
                break;
            case RobotCommand.ACTION_SPECIFIC_COMMAND:
                break;
        }
    }

    /**
     * @param gallery
     * @param photoTotalSize
     * @param position
     */
    private void moveToSpecificPositionWithFormatNumber(Gallery3d gallery, int photoTotalSize, int position) {
        if (position <= 0) position = 1;
        if (position > photoTotalSize) position = photoTotalSize;
        gallery.moveToSpecificPosition(position);
    }

    private void loadPicture(List<String> imagePathSet, String galleryDirPath) {
        File videoDir = new File(galleryDirPath);
        if (videoDir.isDirectory()) {
            File[] gallerDir = videoDir.listFiles();
            for (File picture : gallerDir) {
                String path = picture.getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg")) {
                    imagePathSet.add(path);
                }
            }
        }
    }


}
