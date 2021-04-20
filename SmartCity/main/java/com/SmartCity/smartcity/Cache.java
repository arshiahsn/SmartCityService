package com.SmartCity.smartcity;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;

import org.springframework.data.repository.query.Param;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity
public class Cache {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	

	private String row;
	private String col;
	private double distValue;
	private double durValue;

	
	private Date lastUpdate; 
	
	public Cache(String row, String col, double distValue, double durValue) {
		this.row = row;
		this.col = col;
		this.distValue = distValue;
		this.durValue = durValue;
		this.lastUpdate = new Date();
	}
	public Cache(){
		
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
		return "Cache{" +
				"row='" + row + '\'' +
				", column=" + col +
				", distValue=" + distValue +
				", durValue=" + durValue +
				'}';
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public double getDistValue() {
		return distValue;
	}
	public void setDistValue(double distValue) {
		this.distValue = distValue;
	}
	public double getDurValue() {
		return durValue;
	}
	public void setDurValue(double durValue) {
		this.durValue = durValue;
	}

}

