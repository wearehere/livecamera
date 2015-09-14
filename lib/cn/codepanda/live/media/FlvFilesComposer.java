package cn.codepanda.live.media;

public FlvFilesComposer extends AbstractMediaSessionReaderr{
	
	public byte[] mheader;
	public byte[] mfiledata;
	public Vector<byte[]> mframes;
	
	FlvFileCreator mcreator = new FlvFileCreator();
	
	ReadVideoFrameRunnable mvr = new ReadVideoFrameRunnable();
	
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
		
		mcreator.setVideoStreamParameters(pps, sps);
		mcreator.setVideoConfig();
		mcreator.setAudioConfig();
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
				boolean iskeyframe = false;
				//check if is key frame and size larger than 10 second.
				if(iskeyframe){
					//create new file.
					//copy header and frames to filesdata
					//adjust data's timestamp
					byte filecontent = mcreator.getFileContent();
					mreceiver.sendOneFile(filecontent);
					mcreator.reset();
				}
				
				mcreator.addAvcFrameData(frame);
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
				if(frame != null)
					mcreator.addAacFrameData(frame);
			}			
		}		
	}
	
	protected Runnable readVideoFrameRunnable = new ReadVideoRunnable();	
	protected Runnable readAudioFrameRunable = new ReadAudioRunnable();
}