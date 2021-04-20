package com.SmartCity.smartcity;


import java.util.Date;

import javax.persistence.*;



@Entity
public class SmartCity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String serviceType;
	private String url;
	private int quality;


	private Date lastUpdate; 

	@OneToOne(cascade = CascadeType.ALL, optional = false, 
			fetch = FetchType.EAGER, orphanRemoval = true)
	@PrimaryKeyJoinColumn
	//@JoinColumn(id="id", nullable=false)
	private Location location;

	public SmartCity(){
	}
	public SmartCity(int quality,
			Location location,
			String serviceType){
		this.quality = quality;
		this.location = location;
		this.serviceType = serviceType;
		this.url = null;
		lastUpdate = new Date();
	}
	public SmartCity(SmartCity smartCity){
		this.quality = smartCity.quality;
		this.location = smartCity.location;
		this.serviceType = smartCity.serviceType;
		this.url = null;
		this.lastUpdate = new Date();
	}
	public Long getId(){
		return this.id;
	}
	
	public void setId(Long id){
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
	public void setUrl(String url){
		this.url = url;
	}
	public String getUrl(){
		return this.url;
	}
	public Date getLastUpdate(){
		return this.lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate){
		this.lastUpdate = lastUpdate;
	}
	
	@PreUpdate
	protected void onUpdate() {
		lastUpdate = new Date();
	}
	@PrePersist
	protected void onCreate() {
		lastUpdate = new Date();
	}

	@Override
	public String toString(){
		return "SmartCity{" +
				"id=" + id +
				", quality=" + quality +
				", location=" + location +
				", serviceType='" + serviceType +'\'' +
				", url='" + url + '\'' +
				'}';
	}


}
