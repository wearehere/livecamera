package com.botata.live.cameralive;

import java.io.IOException;

import cn.codepanda.live.Session;
import cn.codepanda.live.SessionBuilder;
import cn.codepanda.live.audio.AudioQuality;
import cn.codepanda.live.cameralive2.R;
import cn.codepanda.live.gl.SurfaceView;
import cn.codepanda.live.media.FlvFilesComposer;
import cn.codepanda.live.media.SaveAsFileMediaSessionReader;
import cn.codepanda.live.receiver.AbstractFilesReceiver;
import cn.codepanda.live.receiver.FileWriter;
import cn.codepanda.live.receiver.HttpUploaderService;
import cn.codepanda.live.video.VideoQuality;


import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	
	private Button mbtnstartrecord = null;
	//private SurfaceHolder camerasurfaceholder = null;
	private SurfaceView mviewcamera = null;
	//private boolean mcamerarunning = false;
	
	//private CameraPreviewCallback mpreviewcallback = new CameraPreviewCallback();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.mbtnstartrecord = (Button) this.findViewById(R.id.buttonStartRecord);
        this.mviewcamera = (SurfaceView) this.findViewById(R.id.surfaceViewCamera);
        
        mbtnstartrecord.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
			    try {
			        SessionBuilder builder = SessionBuilder.getInstance();
			        builder.setSurfaceView(mviewcamera)
			        .setContext(MainActivity.this)
			        .setAudioEncoder(SessionBuilder.AUDIO_AAC)
			        .setVideoEncoder(SessionBuilder.VIDEO_H264)
			        //.setAudioQuality(new AudioQuality(44100,16000, 2)) //must be 44.1 k for flv aac.
			        .setAudioQuality(new AudioQuality(8000,16000, 2)) 
			        //.setPreviewOrientation(0) //0,90,180,270
			        .setVideoQuality(new VideoQuality(640,480, 30, 512000));
			        //builder.setCallBack(new SessionCallback());

			        Session sess = builder.build();
			        try {
                        sess.configure();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Phone incompatible", Toast.LENGTH_LONG).show();
                    }
			        sess.start();
			        
			        //AbstractFilesReceiver receiver = new FileWriter();
			        HttpUploaderService receiver = new HttpUploaderService();
			        //receiver.setUploadParam("http://192.168.233.200:3000/file", "file");
			        receiver.setUploadParam("http://192.168.1.100:3000/file", "file");
			        receiver.start();

			        //SaveAsFileMediaSessionReader comp = new SaveAsFileMediaSessionReader();
			        FlvFilesComposer comp = new FlvFilesComposer();
			        comp.setReceiver(receiver);
			        comp.setSession(sess);
			        comp.start();
			        
//			        enc.setSurfaceView(mviewcamera);
//                    MP4Config m4c = enc.testMediaRecorderAPI();
                } catch (RuntimeException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    Toast.makeText(MainActivity.this, "Phone incompatible", Toast.LENGTH_LONG).show();
                }
			    return;
			}				
        });
    }
//    
//    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
//        final double ASPECT_TOLERANCE = 0.1;
//        double targetRatio = (double) w / h;
//        if (sizes == null) return null;
//
//        Size optimalSize = null;
//        double minDiff = Double.MAX_VALUE;
//
//        int targetHeight = h;
//
//        // Try to find an size match aspect ratio and size
//        for (Size size : sizes) {
//            double ratio = (double) size.width / size.height;
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (Math.abs(size.height - targetHeight) < minDiff) {
//                optimalSize = size;
//                minDiff = Math.abs(size.height - targetHeight);
//            }
//        }
//
//        // Cannot find the one match the aspect ratio, ignore the requirement
//        if (optimalSize == null) {
//            minDiff = Double.MAX_VALUE;
//            for (Size size : sizes) {
//                if (Math.abs(size.height - targetHeight) < minDiff) {
//                    optimalSize = size;
//                    minDiff = Math.abs(size.height - targetHeight);
//                }
//            }
//        }
//        return optimalSize;
//    }        

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    class CameraPreviewCallback implements Camera.PreviewCallback{

		@Override
		public void onPreviewFrame(byte[] arg0, Camera arg1) {
			//mencoder.offerEncoder(arg0);
		}
    
    }
}
