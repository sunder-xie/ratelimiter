package entity;

import java.io.Serializable;

public class RateLimitRegisterMsg implements Serializable{

	private static final long serialVersionUID = -8661082185568569076L;
	
	private final String resource;
	
	private final double qps;
	
	public RateLimitRegisterMsg(String resource, double qps)
	{
		this.resource = resource;
		this.qps = qps;
	}

	public String getResource() {
		return resource;
	}

	public double getQps() {
		return qps;
	}

	@Override
	public String toString() {
		return "RateLimitRegisterMsg [resource=" + resource + ", qps=" + qps + "]";
	}

}
