package com.example.demo.dto;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection="data")
@Data
public class Restaurant {
	@Id
	private ObjectId id;	
	private String name;
	private String branch;
	private String area;
	private String tel;
	private String address;
	private String latitude;
	private String longitude;
	private List<Object> category_list;
	private List<Object> menu_list;
	private List<Object> bhour_list;
	private int creview_cnt;
	private List<Object> review_list;
	private List<Float> loc;
	private List<Integer> tags;
}
