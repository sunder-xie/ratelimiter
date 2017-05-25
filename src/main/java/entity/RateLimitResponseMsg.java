package entity;

import java.io.Serializable;

public class RateLimitResponseMsg  implements Serializable{

	private static final long serialVersionUID = 5635968627898017898L;

	public static enum MsgType
	{
		REGISTER, CANCEL
	}
	
	private final MsgType msgType;
	private final String resource;
	
	public RateLimitResponseMsg(MsgType msgType, String resource)
	{
		this.msgType = msgType;
		this.resource = resource;
	}

	public MsgType getMsgType() {
		return msgType;
	}

	public String getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return "RateLimitRgisteResponseMsg [msgType=" + msgType + ", resource=" + resource + "]";
	}
	
}
