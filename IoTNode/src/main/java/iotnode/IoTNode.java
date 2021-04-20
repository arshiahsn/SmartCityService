package iotnode;


public class IoTNode {
	private long id;
	private int quality;
    private Location location;
	private String serviceType;
	public IoTNode(){
		
	}
	public IoTNode(int quality,
				   Location location,
				   String serviceType){
		this.id = 0;
		this.quality = quality;
		this.location = location;
		this.serviceType = serviceType; 
	}
	public long getId(){
		return this.id;
	}
	public void setId(long id){
		this.id = id;
	}
	public int getQuality(){
		return this.quality;
	}
	public Location getLocation(){
		return this.location;
	}

	public String getServiceType(){
		return this.serviceType;
	}
	public void setQuality(int quality){
		this.quality = quality;
	}
	
	@Override
	public String toString(){
		return "IoTNode{" +
				"id=" + id +
				", quality=" + quality +
				", location=" + location +
				", serviceType='" + serviceType + '\'' +
				'}';
	}
	
	
}
