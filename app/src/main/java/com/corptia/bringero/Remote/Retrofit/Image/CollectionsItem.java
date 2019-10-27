package com.corptia.bringero.Remote.Retrofit.Image;

import com.google.gson.annotations.SerializedName;

public class CollectionsItem{

	@SerializedName("count")
	private int count;

	@SerializedName("_id")
	private String id;

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"CollectionsItem{" + 
			"count = '" + count + '\'' + 
			",_id = '" + id + '\'' + 
			"}";
		}
}