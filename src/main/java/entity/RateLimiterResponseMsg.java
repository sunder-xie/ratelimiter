package entity;

import java.io.Serializable;
import java.util.UUID;

public class RateLimiterResponseMsg  implements Serializable{

	private static final long serialVersionUID = -6077259661512064099L;
	
	private final UUID id;
	private final boolean canAccess;
	
	public RateLimiterResponseMsg(UUID id, boolean canAccess)
	{
		this.id = id;
		this.canAccess = canAccess;
	}

	public UUID getId() {
		return id;
	}

	public boolean isCanAccess() {
		return canAccess;
	}

	@Override
	public String toString() {
		return "RateLimiterResponseMsg [id=" + id + ", canAccess=" + canAccess + "]";
	}
	
}
