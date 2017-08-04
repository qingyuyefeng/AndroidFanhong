package com.fanhong.cn.models;

public class RegionInfo {
	
	private String id; //小区id
	private String property; //所属物业id
	private String name; //小区名字
	public RegionInfo() {
		super();
	}
	
	public RegionInfo(String id, String property, String name) {
		super();
		this.id = id;
		this.property = property;
		this.name = name;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public int getType() {
//		return type;
//	}
//	public void setType(int type) {
//		this.type = type;
//	}
	
	

}
