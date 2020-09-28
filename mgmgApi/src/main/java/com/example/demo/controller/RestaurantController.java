package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
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

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Log4j2
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    private CosineSimilarity cs=new CosineSimilarity();
    private Tags tagArr=new Tags();
    
    @PostMapping("/search")
    @ApiOperation(value = "MgApi", tags = "search")
    public List<RestInfo> getAll(@RequestBody Info info){    	
    	Point point = new Point(info.getLat(), info.getLon());
		Distance distance = new Distance(1, Metrics.KILOMETERS);
		List<Restaurant>list=restaurantService.findByLocNear(point, distance);
		for(Restaurant el:list) {
			System.out.println("쿼리 결과:"+el);
		}
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
			csList.add(new Simil(cnt++,cs.calCS(a,b)));
		}
		csList.sort(Comparator.comparing(Simil::getSimilarity).reversed());
		int len=csList.size();
		len=len>10?10:len;
		List<Restaurant>result=new ArrayList<>();
		for(int i=0;i<len;i++) {
			Simil el=csList.get(i);
			result.add(list.get(el.getIdx()));
		}
		ArrayList<RestInfo>res=new ArrayList<>();
		for(Restaurant el:result) {
			System.out.println("분석결과:"+el);
			RestInfo r=new RestInfo();
			r.setId(el.getId().toHexString());
			r.setName(el.getName());
			r.setTel(el.getTel());
			r.setAddress(el.getAddress());
			r.setArea(el.getArea());
			r.setLat(el.getLatitude());
			r.setLon(el.getLongitude());
			r.setBranch(el.getBranch());
			r.setBhour_list(el.getBhour_list());
			r.setCategory_list(el.getCategory_list());
			r.setMenu_list(el.getMenu_list());
			r.setReview_list(el.getReview_list());
			List<Integer> arr=el.getTags();
			List<String>tags=new ArrayList<>();
			for(int i=0;i<arr.size();i++)if(arr.get(i)==1)tags.add(tagArr.getTagsArr()[i]);
			r.setTags(tags);
			r.setImages(el.getImages());
			res.add(r);
		}
    	return res;
    }
}
