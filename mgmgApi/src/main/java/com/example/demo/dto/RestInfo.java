package com.example.demo.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RestInfo {
	private String id;
	private String name;
	private String tel;
	private String address;
	private String area;
	private String lat;
	private String lon;
	private String branch;
	private List<Object> bhour_list;
	private List<Object> category_list;
	private List<Object> menu_list;
	private List<Object> review_list;
	private List<String> tags;
	private List<String> images;
}
