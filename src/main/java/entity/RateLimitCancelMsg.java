package entity;

import java.io.Serializable;

public class RateLimitCancelMsg implements Serializable{

	private static final long serialVersionUID = -1549888811979799735L;
	private final String resource;
	
	public RateLimitCancelMsg(String res)
	{
		this.resource = res;
	}

	public String getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return "RateLimitCancelMsg [resource=" + resource + "]";
	}
	
}
