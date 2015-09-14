package cn.codepanda.live.receiver;

abstract public class AbstractFilesReceiver{
	protected String meventid = "";
	
	public void setEventId(String eventid){meventid = eventid;}
	public void sendOneFile(byte[] content){}
}