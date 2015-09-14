package cn.codepanda.live.flv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import cn.codepanda.live.util.ByteUtilBE;
import cn.codepanda.live.util.MiscUtil;

//translate from amf0decoder.cpp

public class Amf0Unit{

    public static final int UNIT_NUMBER = 0x00;
    public static final int UNIT_BOOL = 0x01;
    public static final int UNIT_STRING = 0x02;
    public static final int UNIT_OBJECT = 0x03;
    public static final int UNIT_MOVCLIP = 0x04;
    public static final int UNIT_NULL = 0x05;
    public static final int UNIT_UNDEFINE = 0x06;
    public static final int UNIT_REFERENCE = 0x07;
    public static final int UNIT_ECMAARRAY = 0x08;
    public static final int UNIT_OBJEND = 0x09;
    public static final int UNIT_STRICTARRAY = 0x0A;
    public static final int UNIT_DATE = 0x0B;
    public static final int UNIT_LONGSTRING = 0x0C;
    public static final int UNIT_UNSUPPORT = 0x0D;
    public static final int UNIT_RECORDSET = 0x0E;
    public static final int UNIT_XMLDOC = 0x0F;
    public static final int UNIT_TYPEDOBJECT = 0x10;
	
	/*---------------------------------------------------------*/
	protected static byte[] readBytes(ByteArrayInputStream bis, int len){
		byte[] buf = new byte[len];
		bis.read(buf, 0, len);
		return buf;
	}
	
	
	protected static short readU16(ByteArrayInputStream bis){
		byte[] buf = readBytes(bis, 2);
		short vle = ByteUtilBE.getShort(buf);
		return vle;		
	}
	
	protected static void writeU16(ByteArrayOutputStream bos, short vle){
		byte[] buf = ByteUtilBE.getBytes(vle);
		try{
			bos.write(buf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected static int readU32(ByteArrayInputStream bis){
		byte[] buf = readBytes(bis, 4);
		int vle = ByteUtilBE.getInt(buf);
		return vle;
	}
	
	protected static void writeU32(ByteArrayOutputStream bos, int vle){
		byte[] buf = ByteUtilBE.getBytes(vle);
		try{
			bos.write(buf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected static double readDouble(ByteArrayInputStream bis){
		byte[] buf = readBytes(bis, 8);
		double vle = ByteUtilBE.getDouble(buf);
		return vle;
	}

	protected static void writeDouble(ByteArrayOutputStream bos, double vle){
		byte[] buf = ByteUtilBE.getBytes(vle);
		try{
			bos.write(buf);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected static String readString(ByteArrayInputStream bis, int len, String encoding){
		byte[] buf = new byte[len];
		bis.read(buf, 0, len);
		String vle = ByteUtilBE.getString(buf, encoding);
		return vle;
	}
	
	protected static void writeString(ByteArrayOutputStream bos, String vle){
		try{
			bos.write(vle.getBytes());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/*
	//require check...
	protected int getUtf8CharBytesNum(byte firstbyte){
	    if((vle&0x80) == 0x00)
	        return 1;
	    if((vle&0xE0) == 0xC0)
	        return 2;
	    if((vle&0xF0) == 0xE0)
	        return 3;
	    if((vle&0xF8) == 0xF0)
	        return 4;
	    if((vle&0xFC) == 0xF8)
	        return 5;
	    if((vle&0xFE) == 0xFC)
	        return 6;
	    return 0;		
	}
	
	protected String readUtf8String(ByteArrayInputStream bis){
		short charlen = readU16(bis)
		int bytelen = 0;
		int i = 0;	
		char buf = new byte[6*len];
		while(i < len>)
			bis.read(buf, bytelen, 1);
			int num = getUtf8CharBytesNum(bis[bytelen]);
			if(num > 1)
				bis.read(buf, bytelen + 1, num - 1);
			bytelen += num;
			i ++;
		}	
		return new String(buf, bytelen);
	}
	*/
	/*----------------------------------------------------------*/
	
	

	public static abstract class Amf0Base{
		public int type;
		public int length;
		public abstract int decode(ByteArrayInputStream bis);
		public abstract int encode(ByteArrayOutputStream bos);
	}

	public static class Amf0ObjectProperty{
		public String name;
		public Amf0Base object;
		
		public Amf0ObjectProperty(){}
		public Amf0ObjectProperty(String name_in, Amf0Base obj_in){name = name_in; obj_in = object;}
		
		public int decode(ByteArrayInputStream bis){
			Amf0String strobj = new Amf0String();
			strobj.decode(bis);
			Amf0Base obj = readAmf0Unit(bis);
			if(obj == null)
				return -1;
			name = strobj.svle;
			object = obj;
			return 0;			
		}
		
		public int encode(ByteArrayOutputStream bos){
			int len = 0;
			Amf0String strobj = new Amf0String(name);
			strobj.encode(bos);
			writeAmf0Unit(bos, object);
			return len;			
		}
	}

	
	public static class Amf0Number extends Amf0Base{
		double value;
		
		public Amf0Number(){type = UNIT_NUMBER;}
		public Amf0Number(double in){type = UNIT_NUMBER;value = in;}
		
		public int decode(ByteArrayInputStream bis){
 			value = readDouble(bis);
			System.out.println("[Amf0Number]:"+value);
			return 8;
		}
		public int encode(ByteArrayOutputStream bos){
			writeDouble(bos, value);
			return 8;
		}
	}		
	
	public static class Amf0Bool extends Amf0Base{
		byte bvalue;
		public Amf0Bool(){type = UNIT_BOOL;}
		public Amf0Bool(byte in){type = UNIT_BOOL;bvalue = in;}
		
		public int decode(ByteArrayInputStream bis){
			bvalue = (byte)(bis.read());
			return 1;
		}
		public int encode(ByteArrayOutputStream bos){
			try{
				bos.write(bvalue);
			}catch(Exception e){
				e.printStackTrace();
				return -1;
			}
			return 1;
		}
	}
	
	
	public static class Amf0String extends Amf0Base{
		public String svle;
		public Amf0String(){type = UNIT_STRING;}
		public Amf0String(String in){type = UNIT_STRING;svle = in;}
		
		public int decode(ByteArrayInputStream bis){
			int len = readU16(bis);
			System.out.println("[Amf0String:]L:"+len);
			svle = readString(bis, len, "utf-8");
			System.out.println("[Amf0String:]V:"+svle);
			return len;
		}
		
		public int encode(ByteArrayOutputStream bos){
			short len = (short)(svle.length());
			writeU16(bos, len);			
			writeString(bos, svle);
			return len;
		}
	}
	
	public static class Amf0Object extends Amf0Base{
		public Vector<Amf0ObjectProperty> vles = new Vector<Amf0ObjectProperty>();
		
		public Amf0Object(){type = UNIT_OBJECT;}
		public Amf0Object(Vector<Amf0ObjectProperty> in){type = UNIT_OBJECT;vles = in;}
		public int decode(ByteArrayInputStream bis){
			int len = 0;
			while(true){
				bis.mark(10);
				byte[] endbuf = new byte[3];
				bis.read(endbuf, 0, 3);
				if(endbuf[0] == 0x00 && endbuf[1] == 0x00 && endbuf[2] == 0x09){
					//finish
					len += 3;
					break;
				}			
				bis.reset();
				Amf0ObjectProperty prop = new Amf0ObjectProperty();
				if(prop.decode(bis) == -1)
					break;
				vles.add(prop);
			}
			return len;
		}
		
		public int encode(ByteArrayOutputStream bos){
			for(Amf0ObjectProperty prop:vles){
				prop.encode(bos);
			}
			byte[] end = new byte[]{(byte)0x00, (byte)0x00, (byte)0x09};
			try{
				bos.write(end);
			}catch(Exception e){
				e.printStackTrace();
				return -1;
			}
			return 0;
		}
	}
	
	
	public static class Amf0EcmaArray extends Amf0Base{
		public int assonum;
		public Vector<Amf0ObjectProperty> vles = new Vector<Amf0ObjectProperty>();
		
		public Amf0EcmaArray(){type = UNIT_ECMAARRAY;}
		public Amf0EcmaArray(Vector<Amf0ObjectProperty> in){type = UNIT_ECMAARRAY; vles = in;}
		
		public int decode(ByteArrayInputStream bis){
			int len = 0;
			System.out.println("[ECMAArray]");
			assonum = readU32(bis);
			while(true){
				bis.mark(10);
				byte[] endbuf = new byte[3];
				bis.read(endbuf, 0, 3);
				if(endbuf[0] == 0x00 && endbuf[1] == 0x00 && endbuf[2] == 0x09){
					//finish
					len += 3;
					break;
				}			
				bis.reset();
				Amf0ObjectProperty prop = new Amf0ObjectProperty();
				if(prop.decode(bis) == -1)
					break;
				vles.add(prop);
			}
			return len;
		}
		
		public int encode(ByteArrayOutputStream bos){
			writeU32(bos, (int)(vles.size()));
			for(Amf0ObjectProperty prop:vles){
				prop.encode(bos);
			}
			byte[] end = new byte[]{(byte)0x00, (byte)0x00, (byte)0x09};
			try{
				bos.write(end);
			}catch(Exception e){
				e.printStackTrace();
				return -1;
			}
			return 0;
		}
	}
	
	public static class Amf0StrictArray extends Amf0Base{
		public int num;
		public Vector<Amf0Base> vles = new Vector<Amf0Base>();
		
		public Amf0StrictArray(){type = UNIT_STRICTARRAY;}
		public Amf0StrictArray(Vector<Amf0Base> in){type = UNIT_STRICTARRAY;vles = in;}
		
		public int decode(ByteArrayInputStream bis){
			int len = 0;
			num = readU32(bis);
			for(int i=0; i< num; i++){
				Amf0Base obj = readAmf0Unit(bis);
				vles.add(obj);
			}
			return len;
		}
		
		public int encode(ByteArrayOutputStream bos){
			writeU32(bos, (int)vles.size());
			for(Amf0Base obj:vles){
				obj.encode(bos);
			}
			return 0;
		}
		
	}
	
	public static class Amf0Date extends Amf0Base{
		double timevle;
		short zonevle;
		
		public Amf0Date(){type = UNIT_DATE;}
		public Amf0Date(double time_in, short zone_in){type = UNIT_DATE;timevle = time_in; zonevle = zone_in;}		
		
		public int decode(ByteArrayInputStream bis){
			timevle = readDouble(bis);
			zonevle = readU16(bis);
			return 10;
		}
		
		public int encode(ByteArrayOutputStream bos){
			writeDouble(bos, timevle);
			writeU16(bos, zonevle);
			return 10;
		}
	}
	
	public static class Amf0LongString extends Amf0Base{
		String svle;
		
		public Amf0LongString(){type = UNIT_LONGSTRING;}
		public Amf0LongString(String in){type = UNIT_LONGSTRING;svle = in;}
		
		public int decode(ByteArrayInputStream bis){
			int len = readU32(bis);
			svle = readString(bis, len, "utf-8");
			return 10;
		}
		
		//buggy? utf8 use character number instead of byte number?
		public int encode(ByteArrayOutputStream bos){
			writeU32(bos, svle.length());
			writeString(bos, svle);
			return 0;
		}		
	}
	
	public static class Amf0XmlDoc extends Amf0Base{
		
		public Amf0XmlDoc(){type = UNIT_XMLDOC;}
		
		public int decode(ByteArrayInputStream bis){
			return 0;
		}
		
		public int encode(ByteArrayOutputStream bos){
			return 0;
		}
	}
	
	
	public static class Amf0TypedObjecty extends Amf0Base{
		public String name;
		public Vector<Amf0ObjectProperty> vles;
		
		public Amf0TypedObjecty(){type = UNIT_TYPEDOBJECT;}
		public Amf0TypedObjecty(String name_in, Vector<Amf0ObjectProperty> prop_in)
			{type = UNIT_TYPEDOBJECT;name = name_in; vles = prop_in;}
		
		public int decode(ByteArrayInputStream bis){
			int len = 0;
			Amf0String clsobj = new Amf0String();
			clsobj.decode(bis);
			name = clsobj.svle;
			while(true){
				bis.mark(10);
				byte[] endbuf = new byte[3];
				bis.read(endbuf, 0, 3);
				if(endbuf[0] == 0x00 && endbuf[1] == 0x00 && endbuf[2] == 0x09){
					//finish
					len += 3;
					break;
				}			
				bis.reset();
				Amf0ObjectProperty prop = new Amf0ObjectProperty();
				if(prop.decode(bis) == -1)
					break;
				vles.add(prop);
			}
			return len;
		}
		
		public int encode(ByteArrayOutputStream bos){
			Amf0String clsobj = new Amf0String(name);
			clsobj.encode(bos);
			for(Amf0ObjectProperty prop:vles){
				prop.encode(bos);
			}
			byte[] end = new byte[]{(byte)0x00, (byte)0x00, (byte)0x09};
			try{
				bos.write(end);
			}catch(Exception e){
				e.printStackTrace();
				return -1;
			}
			return 0;
		}
		
	}

	//--------------------------------------------------------
	public static Amf0Base readAmf0Unit(ByteArrayInputStream bis){
		byte tagvle = (byte)(bis.read());
		Amf0Base obj = null;
		System.out.println("[readAmf0Unit]tag:"+ tagvle);
		switch ((int)(tagvle)){
			case UNIT_NUMBER:
				obj = new Amf0Number();
				break;
			case UNIT_BOOL:
				obj = new Amf0Bool();
				break;
			case UNIT_STRING:
				obj = new Amf0String();
				break;
			case UNIT_OBJECT:
				obj = new Amf0Object();
				break;
			case UNIT_MOVCLIP:
				break;
			case UNIT_NULL:
				break;
			case UNIT_UNDEFINE:
				break;
			case UNIT_REFERENCE:
				break;
			case UNIT_ECMAARRAY:
				obj = new Amf0EcmaArray();
				break;
			case UNIT_OBJEND:
				break;
			case UNIT_STRICTARRAY:
				obj = new Amf0StrictArray();
				break;
			case UNIT_DATE:
				obj = new Amf0Date();
				break;
			case UNIT_LONGSTRING:
				obj = new Amf0LongString();
				break;
			case UNIT_UNSUPPORT:
				break;
			case UNIT_RECORDSET:
				break;
			case UNIT_XMLDOC:
				break;
			case UNIT_TYPEDOBJECT:
				obj = new Amf0TypedObjecty();
				break;
		}
		if( obj != null)
			obj.decode(bis);
		else
			System.out.println("[readAmf0Unit]obj null");
		return obj;
	}
	
	public static void writeAmf0Unit(ByteArrayOutputStream bos, Amf0Base obj){
		byte tag = (byte)obj.type;
		bos.write(tag);
		obj.encode(bos);
	}
	
	public static Amf0ObjectProperty readAmf0Property(ByteArrayInputStream bis){
		Amf0String strobj = new Amf0String();
		strobj.decode(bis);
		Amf0Base obj = readAmf0Unit(bis);
		if(obj == null)
			return null;
		Amf0ObjectProperty prop = new Amf0ObjectProperty();
		prop.name = strobj.svle;
		prop.object = obj;
		return prop;
	}
	
	public static int writeAmf0Property(ByteArrayOutputStream bos, Amf0ObjectProperty prop){
		int len = 0;
		Amf0String strobj = new Amf0String(prop.name);
		len = strobj.encode(bos);
		len += prop.object.encode(bos);
		return len;
	}
	
	public static void main(String[] argv){
		if(argv.length != 1){
			System.out.println("require parameter:"+argv.length);
			return;
		}
		Vector<Amf0Base> readobjs = new Vector<Amf0Base>();
		try{
			byte[] content = MiscUtil.getBytesFromFile(argv[0]);
			ByteArrayInputStream bis = new ByteArrayInputStream(content);
			while(true){
				Amf0Base readobj = readAmf0Unit(bis);
				if(readobj == null){
					System.out.println("can not decode amf0object, type="+readobj.type);
					break;
				}
				readobjs.add(readobj);
				if(bis.available() == 0){
					bis.close();
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for(Amf0Base obj:readobjs){
				writeAmf0Unit(bos, obj);
			}
			byte[] outputbyte = bos.toByteArray();
			MiscUtil.writeBytesToFile("aout.meta", outputbyte);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}