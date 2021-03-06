package com.example.demo.controller;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Info;
import com.example.demo.dto.RestInfo;
import com.example.demo.dto.Restaurant;
import com.example.demo.dto.Simil;
import com.example.demo.dto.Tags;
import com.example.demo.service.RestaurantService;
import com.example.demo.util.CosineSimilarity;
import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@CrossOrigin(origins = {"*"}, maxAge = 6000)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Log4j2
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    private CosineSimilarity cs=new CosineSimilarity();
    private Tags tagArr=new Tags();
    private Gson gson=new Gson();
    @PostMapping("/search")
    @ApiOperation(value = "MgApi", tags = "search")
    public String getAll(@RequestBody Info info){            	
    	
        Point point = new Point(info.getLat(),info.getLon());    	
		Distance distance = new Distance(20, Metrics.KILOMETERS);
		Circle area=new Circle(point, distance);
		List<Restaurant>list=restaurantService.findByLocWithin(area);	
       
		int cnt=0;
		ArrayList<Simil>csList=new ArrayList<>();
		int[] a=new int[24];
		int idx=0;
		for(boolean f:info.getKeys()) {
			if(f) a[idx++]=1;
			else a[idx++]=0;
		}
		for(Restaurant el:list) {			
			int[] b=new int[24];
			idx=0;
			for(int num:el.getTags()) {
				b[idx++]=num;
			}
			double result=cs.calCS(a,b);		
			csList.add(new Simil(cnt++,result));
		}
		csList.sort(Comparator.comparing(Simil::getSimilarity).reversed());
		int len=csList.size();
		int max=len>30?30:len;		
		List<Restaurant>result=new ArrayList<>();
		List<Integer>NanList=new ArrayList<>();
		int cnt1=0;
		idx=0;
		while(true) {
			if(idx>=len||cnt1>=max)break;
			Simil el=csList.get(idx++);			
			if(Double.isNaN(el.getSimilarity())) {
				//NanList.add(el.getIdx());
			}else {
				if(el.getSimilarity()>0.0) {
					result.add(list.get(el.getIdx()));
					cnt1++;
				}				
			}			
		}
		for(int idx1:NanList)result.add(list.get(idx1));
		ArrayList<RestInfo>res=new ArrayList<>();
		Set<String>set=new HashSet<>();
		int sum=0;
		for(int num:a)sum+=num;
		if(sum==0) {
			result.clear();
			len=list.size();
			len=len>30?30:len;
			for(int i=0;i<len;i++) {
				result.add(list.get(i));
			}
		}
		for(Restaurant el:result) {
			//System.out.println("분석결과:"+el);
			set.clear();
			RestInfo r=new RestInfo();
			r.setId(el.getId().toHexString());
			r.setName(el.getName());
			r.setTel(el.getTel());
			r.setAddress(el.getAddress());
			r.setArea(el.getArea());
			r.setLat(el.getLatitude());
			r.setLon(el.getLongitude());
			r.setBranch(el.getBranch());
			List<String>categorys=new ArrayList<>();
			for(Object o:el.getCategory_list()) {
				Map map = new HashMap();
				map = (Map) gson.fromJson(gson.toJson(o), map.getClass());					
				String rv=map.get("category").toString();
				if(rv.length()>0)categorys.add(rv);	
				set.add(rv);
			}
			r.setCategory_list(categorys);
			List<String>menu=new ArrayList<>();
			for(Object o:el.getMenu_list()) {
				Map map = new HashMap();
				map = (Map) gson.fromJson(gson.toJson(o), map.getClass());					
				String rv=map.get("menu").toString();
				if(rv.length()>0)menu.add(rv);
				set.add(rv);
			}
			r.setMenu_list(menu);
			List<String>reviews=new ArrayList<>();
			for(Object o:el.getReview_list()) {
				Map map = new HashMap();
				map = (Map) gson.fromJson(gson.toJson(o), map.getClass());							
				map = (Map) gson.fromJson(gson.toJson(map.get("review_info")), map.getClass());
				String rv=map.get("content").toString();
				if(rv.length()>0)reviews.add(rv);		
			}
			r.setReview_list(reviews);
			List<Integer> arr=el.getTags();
			List<String>tags=new ArrayList<>();			
			for(int i=0;i<arr.size();i++)if(arr.get(i)==1)tags.add(tagArr.getTagsArr()[i]);			
			Iterator iter=set.iterator();
			while (iter.hasNext()) {
				tags.add(iter.next().toString());				
			}
			//System.out.println(tags.toString());
			r.setTags(tags);
			r.setImages(el.getImages());
			res.add(r);
		}
		Map<String,Object>map=new HashMap<>();
		map.put("result", res);
		//System.out.println(gson.toJson(map));
    	return gson.toJson(map);
    }
}
