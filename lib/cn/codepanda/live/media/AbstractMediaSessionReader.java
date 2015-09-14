package cn.codepanda.live.media;


abstract public class AbstractMediaSessionReaderr{
	
	protected MediaStream maudiostream;
	protected MediaStream mvideostream;
	protected AbstractFilesReceiver mreceiver;
	protected Session msession;
	
	public void setReceiver(AbstractFilesReceiver recv){mreceiver = recv;}
	//public void setAudioMediaStream(MediaStream stream){maudiostream = stream;}
	//public void setVideoMediaStream(MediaStream stream){mvideostream = stream;}
	
	public void setSession(Session sess_in){
		msession = sess_in;
		maudiostream = msession.getAudioTrack();
		mvideostream = msession.getVideoTrack();
	}
	//public void setH264StreamParameter(byte[] sps, byte[] pps){}
	public abstract void start();
	public abstract void stop();
	
	protected byte[] readAvcFrame(InputStream is){
		// NAL units are preceeded with 0x00000001
		fill(is, header,0,5);
		ts = ((MediaCodecInputStream)is).getLastBufferInfo().presentationTimeUs*1000L;
		//ts += delay;
		naluLength = is.available()+1;
		if (!(header[0]==0 && header[1]==0 && header[2]==0)) {
			// Turns out, the NAL units are not preceeded with 0x00000001
			Log.e(TAG, "NAL units are not preceeded by 0x00000001");
			streamType = 2; //should happen, if happen, try some other code.
			return null;
		}
		// Parses the NAL unit type
		type = header[4]&0x1F;


		// The stream already contains NAL unit type 7 or 8, we don't need 
		// to add them to the stream ourselves
		if (type == 7 || type == 8) {
			Log.v(TAG,"SPS or PPS present in the stream.");
//			count++;
//			if (count>4) {
//				sps = null;
//				pps = null;
//			}
			return null;
		}else{
			byte[] avcdata = new avcdata[naluLength];
			avcdata[0] = header[4];
			len = fill(avcdata, 1,  naluLength-1);
			return avcdata;
		}		
		
	protected byte[] readAacFrame(InputStream is){
		Log.d(TAG,"AAC LATM packetizer started !");

		int length = 0;
		long oldts;
		BufferInfo bufferInfo;

		try {
			while (!Thread.interrupted()) {
				byte[] buffer = new byte[4096]; //??size correct?
				length = is.read(buffer, 0, 4096);
				
				if (length>0) {
					
					bufferInfo = ((MediaCodecInputStream)is).getLastBufferInfo();
					//Log.d(TAG,"length: "+length+" ts: "+bufferInfo.presentationTimeUs);
					oldts = ts;
					ts = bufferInfo.presentationTimeUs*1000;
					
					byte[] aacbuf = new byte[length];
					System.arraycopy(buffer,0, aacbuf, 0, length);
					return aacbuf;

				}
			}
		} catch (IOException e) {
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.e(TAG,"ArrayIndexOutOfBoundsException: "+(e.getMessage()!=null?e.getMessage():"unknown error"));
			e.printStackTrace();
		} catch (InterruptedException ignore) {}

		Log.d(TAG,"AAC LATM packetizer stopped !");
		return null;
	}
	
	private int fill(InputStream is, byte[] buffer, int offset,int length) throws IOException {
		int sum = 0, len;
		while (sum<length) {
			len = is.read(buffer, offset+sum, length-sum);
			if (len<0) {
				throw new IOException("End of stream");
			}
			else sum+=len;
		}
		return sum;
	}			
}