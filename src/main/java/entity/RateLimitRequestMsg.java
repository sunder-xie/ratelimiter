package entity;

import java.io.Serializable;
import java.util.UUID;

public class RateLimitRequestMsg implements Serializable {

	private static final long serialVersionUID = 8446577012398642855L;
	
	private final UUID id;
	private final String resource;
	
	public RateLimitRequestMsg(UUID id, String resource)
	{
		this.id = id;
		this.resource = resource;
	}

	public UUID getId() {
		return id;
	}

	public String getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return "RateLimitRequestMsg [id=" + id + ", resource=" + resource + "]";
	}
	
}
