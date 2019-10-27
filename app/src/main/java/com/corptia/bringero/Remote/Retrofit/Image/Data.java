package com.corptia.bringero.Remote.Retrofit.Image;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data{

	@SerializedName("path")
	private String path;

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("collections")
	private List<CollectionsItem> collections;

	@SerializedName("__v")
	private int V;

	@SerializedName("name")
	private String name;

	@SerializedName("_id")
	private String id;

	@SerializedName("thumbs")
	private List<Object> thumbs;

	@SerializedName("updatedAt")
	private String updatedAt;

	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){
		return path;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setCollections(List<CollectionsItem> collections){
		this.collections = collections;
	}

	public List<CollectionsItem> getCollections(){
		return collections;
	}

	public void setV(int V){
		this.V = V;
	}

	public int getV(){
		return V;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setThumbs(List<Object> thumbs){
		this.thumbs = thumbs;
	}

	public List<Object> getThumbs(){
		return thumbs;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"path = '" + path + '\'' + 
			",createdAt = '" + createdAt + '\'' + 
			",collections = '" + collections + '\'' + 
			",__v = '" + V + '\'' + 
			",name = '" + name + '\'' + 
			",_id = '" + id + '\'' + 
			",thumbs = '" + thumbs + '\'' + 
			",updatedAt = '" + updatedAt + '\'' + 
			"}";
		}
}