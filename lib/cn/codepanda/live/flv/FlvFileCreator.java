public FlvFileCreator{

	public byte[] getFileContent();
	
	
	public byte[] mheader;
	public byte[] mfiledata;
	
	public byte[] mavcpps;
	public byte[] mavcsps;
	
	public Vector<byte[]> mframes = Vector<byte[]>();
	
	
	public FlvFileCreator(){
		
	}
	
	public void init(){
		mheader = createFlvHeader();
	}
	
//	public void setAvcSps(byte[] sps){
////		String b64pps = mvideostream.getStringParameter("pps");
////		String b64sps = mvideostream.getStringParameter("sps");
////		byte[] pps = Base64.decode(mConfig.getB64PPS(), Base64.NO_WRAP);
////		byte[] sps = Base64.decode(mConfig.getB64SPS(), Base64.NO_WRAP);
//		mavcsps = sps;		
//	}
	
	public void setVideoStreamParameters(byte[] pps, byte[] sps){
		mavcpps = pps;
		mavcsps = sps;	
	}
	
	public void setVideoConfig(int width, int height, int codecid){
		
	}
	
	public void setAudioConfig(int samplerate, int sampeindex, int codecid){
		
	}
	
	protected byte[] createFlvHeader(){		
		try{
			//flv header
			ByteOutputStream bos = new ByteOutputSteam();
			bos.write('F');bos.write('L');bos.write('V');
			bos.write((byte)0x01);bos.write((byte)0x05);
			byte[] buf = ByteUtilBE.getBytes((int)0x09);
			bos.write(buf);
			//first on meta frame
			int previouslen = 0x00;
			buf = ByteUtilBE.getBytes(previouslen);
			byte[] metadata = createOnMetaData();
			byte[] tagheader = createFlvTagHeader(0x12, metadata.length, 0x00, 0x00);
			bos.write(buf);
			bos.write(tagheader);
			bos.write(metadata);
			previouslen = 11 + metadata.length;
			//aac sequence
			byte[] asc = createAacSequence();
			tagheader = createFlvTagHeader(0x08, af.length, 0x00, 0x00);
			buf = ByteUtilBE.getBytes(previouslen);
			bos.write(buf);
			bos.write(tagheader);
			bos.write(af);
			previouslen = 11 + af.length;
			//avc sequence
			byte[] vsc = createAvcSequence();
			tagheader = createFlvTagHeader(0x08, vsc.length, 0x00, 0x00);
			buf = ByteUtilBE.getBytes(previouslen);
			bos.write(buf);
			bos.write(tagheader);
			bos.write(vsc);
			previouslen = 11 + vf.length;
			
			return bos.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}		
		return null;
	}

	//------ omit something.
	protected byte[] createOnMetaData(double width, double height,
		double videocodecid, double audiocodecid, double duration,
		double lasttimestamp, double lastkeytimestamp, double audiodelay,
		byte canseektoend, double videodatarate, double audiodatarate,
		double framerate, double filesize){
			
		ByteOutputStream bos = new ByteOutputStream();
		//write flv tag header
		Amf0String amfstring = new Amf0String("onMetaData");
		Vector<Amf0ObjectProperty> vap = new Vector<Amf0ObjectProperty>();
		vap.add(new Amf0ObjectProperty("width", new Amf0Number(width));
		vap.add(new Amf0ObjectProperty("height", new Amf0Number(height));
		vap.add(new Amf0ObjectProperty("videocodecid", new Amf0Number(videocodecid));
		vap.add(new Amf0ObjectProperty("audiocodecid", new Amf0Number(audiocodecid));
		vap.add(new Amf0ObjectProperty("duration", new Amf0Number(duration));
		vap.add(new Amf0ObjectProperty("lasttimestamp", new Amf0Number(lasttimestamp));
		vap.add(new Amf0ObjectProperty("lastkeytimestamp", new Amf0Number(lastkeytimestamp));
		vap.add(new Amf0ObjectProperty("audiodelay", new Amf0Number(audiodelay));
		vap.add(new Amf0ObjectProperty("framerate", new Amf0Number(framerate));
		vap.add(new Amf0ObjectProperty("filesize", new Amf0Number(filesize));
		Amf0EcmaArray amfarray = new Amf0EcmaArray(vap);
		amfstring.encode(bos);
		amfarray.encode(bos);
		return bos.toByteArray();
	}
	
	private byte[] createFlvTagHeader(int tagtype, int datasize, int timestamp, 
		int streamid){
		ByteOutputStream bos = new ByteOutputStream();
		try{
			bos.write((byte)tagtype);
			byte[] buf = ByteUtilBE.getBytes(datasize);
			bos.write(buf[1]);bos.write(buf[2]);bos.write(buf[3]);
			buf = ByteUtilBE.getBytes(timestamp);
			bos.write(buf[1]);bos.write(buf[2]);bos.write(buf[3]);bos.write(buf[0]);
			buf = ByteUtilBE.getBytes(streamid);
			bos.write(buf[1]);bos.write(buf[2]);bos.write(buf[3]);
			return bos.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
				
	}
	//----------------------------------------------------------
	// flv frame -> videoframe -> h264 videopacket
		
	protected byte[] createAvcVideoFrame(byte[] data, boolean iskeyframe){
		ByteOutputStream bos = new ByteOutputStream();
		byte frametype = 0;
		if(iskeyframe)
			byte frametype = 1; //4bit
		byte codecid = 0x07; //4bit
		byte buf = ((frametype&0xff)<<4)|(codecid&0xff);
		bos.write(buf);
		//....videodata.
		bos.write(data);
		return bos.toByteArray();
		
	}
	
	
	protected byte[] createAvcVideoPacket(int type, int comptime, byte[] data){
		ByteOutputStream bos  = new ByteOutputStream();
		byte[] avchdr = new byte[4];
		avchdr[0] = (byte)type;
		byte[] timebuf = ByteUtilBE.getBytes(comptime);
		avchdr[1] = timebuf[1];
		avchdr[2] = timebuf[2];
		avchdr[3] = timebuf[3];
		
		bos.write(avchdr);
		bos.write(data);
		return bos.toByteArray();
	}

	protected byte[] createAVCDecoderConfigurationRecord(){
		ByteOutputStream bos = new ByteOutputStream();
		//or get avcC from test mp4 file.
//		byte confverion = 0x01;
//		byte avcprofile = mavcsps[1];
//		byte profilecomp = mavcsps[2];
//		byte avclevelind = mavcsps[3];
//		byte lengthsize = 0xF4 | (nalunit length -1);
//		byte numberofsps = 0xE0 | spsnumber;
		int numberofsps = 1;
		int numberofpps = 1;

		bos.write((byte)0x01);
		bos.write(mavcsps[1]);
		bos.write(mavcsps[2]);
		bos.write(mavcsps[3]);
		byte lengthsize = 0xF4 | (nalunit length -1);
		bos.write(lengthsize);
		byte numberofsps = 0xE0 | spsnumber;
		bos.write(numberofsps);		
		for(int i = 0; i< numberofsps; i++){
			byte[] buf = ByteUtilBE.getBytes((short)(mavcsps.length));
			bos.write(buf);
			bos.write(mavcsps);
		}
		bos.write(numberofpps);		
		for(int i = 0; i< numberofpps; i++){
			byte[] buf = ByteUtilBE.getBytes((short)(mavcpps.length));
			bos.write(buf);
			bos.write(mavcpps);
		}
		return bos.toByteArray();
	}


	protected byte[] createAvcSequence(){
		byte[] avcof = createAVCDecoderConfigurationRecord();
		byte[] avseq = createAvcVideoPacket(0, 0, avcof);
		byte[] flvseq = createAvcVideoFrame(avseq, true);
		return flvseq;
	}	
	
	//----------------audio relate	-------------
	//flv frame-> audio frame -> aac frame
	protected byte[] createAacAudioFrame(byte[] data){
		ByteOutputStream bos = new ByteOutputStream();		
		byte sndformat = 10; //4bit
		byte soundrate = 3 //44.1  2bit
		byte sndsize = 1; //1bit
		byte soundtype = 1; //stereo 1 bit
		byte soundhdr = (byte)(((sndformat&0x0F)<<4)|(soundreate&0x03)<<2|
			((sndsize&0x01)<<1) | (soundtype & 0x01));
		bos.write(soundhdr);
		bos.write(data);
		return bos.toByteArray();
	}
	
	protected byte[] createAacAudioPacket(int type, byte[] data){
		ByteOutputStream bos = new ByteOutputStream();
		bos.write((byte)type);
		bos.write(data);
		return bos.toByteArray();
	}
	
	
	protected byte[] createAudioSpecificConfig(int samplerateidx){
		byte audioobjtype = 0x02; //AAC-LC 5bit
		// 48kHz, samplefrequceindex = 0x03
		//byte samplefrequceindex = 0x04; //44.1khz 4bit
		byte samplefrequceindex = samplerateidx;
		byte channelconfig = 0x02; //2 channels 4bit
		byte framelengthflag = 0x00; //IMDCT 1bit
		byte dependcorecoder = 0x00; // 1bit
		byte extendflag = 0x00; //1bit
		byte[] aconf = new byte[2];
		byte[0] = ((audioobjtype&0x1F) << 3)|((samplefrequceindex&0x0E) >> 1);
		byte[1] = ((samplefrequceindex & 0x01)<<7)|(channelconfig & 0x0f) <<3);
		return aconf;
	}	
		
	
	protected byte[] createAacSequence(){
		//createAudioSpecificConfig
		byte[] aacseq = new byte[3];
		aacseq[0] = 0x00; //type
		aacseq[1,2] = createAudioSpecificConfig(freq)
		//aacseq[1] = 0x11;
		//aacseq[2] = 0x90;
		return createAacAudioFrame(aacseq);
	}
	
	//type = 0 sequence header, type = 1 raw header
	protected byte[] createAacAudioData(byte[] data){
		
	}
	//------------------------------------------------------
	public void addAvcFrameData(byte[] rawavcdata){
		ByteOutputStream bos = new ByteOutputStream();
		byte[] buf = ByteUtilBE.getBytes(previoustagsize);
		bos.write(buf);
		byte[] tagheader = createFlvTagHeader(0x09, data.length, currentmillsec, 0x00);
		bos.write(tagheader);
		byte[] videopac = createVideoPacket(0x01, currentmillsec, rawavcdata);
		buf = createAvcVideoFrame(videopac, iskeyframe);
		bos.write(buf);
		//lock mframes
		byte[] framedata = bos.toByteArray();
		previoustagsize = framedata.length;
		mframes.add(framedata);		
	}
	
	public void addAacFrameData(byte[] data){
		ByteOutputStream bos = new ByteOutputStream();
		byte[] buf = ByteUtilBE.getBytes(previoustagsize);
		bos.write(buf);
		byte[] tagheader = createFlvTagHeader(0x08, data.length, currentmillsec, 0x00);
		bos.write(tagheader);
		buf = createFlvAudioFrame(data);
		bos.write(buf);
		//lock mframes
		byte[] framedata = bos.toByteArray();
		previoustagsize = framedata.length;
		mframes.add(framedata);		
	}
		
	public static void main(String[] argv){
		FlvFileCreator flvc = new FlvFileCreator();
		flv.setAttribute("name", "value");
		flv.setAvcPps();
		flv.setAacPps();
		flv.init();
		while(true){
			flv.addAacFrameData();
			flv.addAvcFrameData();
			sleep(0.5)
		}
		//save flv.byte data to file.
	}

}