package cn.codepanda.live.media;

public SaveAsFileMediaSessionReader extends AbstractMediaSessionReaderr{
	
	public void start(){
		
		initConfig();
		new Thread(readVideoFrameRunnable).start();		
		new Thread(readAudioFrameRunable()).start();
	}
	
	public void stop(){
		readVideoFrameRunnable.stopflag = true;
		readAudioFrameRunable.stopflag = true;
	}
	
	public void initConfig(){
		String spps = mvideostream.getStringParameter("pps");
		String ssps = mvideostream.getStringParameter("sps");
		int width = mvideostream.getStringParameter("width");
		int width = mvideostream.getStringParameter("height");
		int videocodecid = mvideostream.getStringParameter("videocodecid");
		
		byte[] pps = Base64.decode(spps, Base64.NO_WRAP);
		byte[] sps = Base64.decode(ssps, Base64.NO_WRAP);
		
		//save value to file.
	}	
	
	class ReadVideoRunnable implements Runnable(){
		public boolean stopflag = false;
		
		public void run(){ 
		
			while(true){
				if(stopflag)
					break;
				byte[] frame = readAvcFrame(mvideostream.getInputStream());
				if(frame == null)
					continue;
				//get time as file name
				//save frame to file.
			}
		}			
	}	
		
	class ReadAudioRunnable implements Runnable{
		public boolean stopflag = false;
		
		public void run(){
			while(true){
				if(stopflag)
					break;
				byte[] frame = readAacFrame(maudiostream.getInputStream);
				if(frame == null)
					continue;
				//get time as file name
				//save frame to file.
			}			
		}		
	}		
	
}	