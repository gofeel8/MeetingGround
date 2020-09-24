package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Restaurant;
import com.example.demo.dto.Simil;
import com.example.demo.service.RestaurantService;
import com.example.demo.util.CosineSimilarity;

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
    @GetMapping()
    public List<Restaurant> getAll(){
    	System.out.println("오스오스");
    	Point point = new Point(127.341441, 36.353687);
		Distance distance = new Distance(1, Metrics.KILOMETERS);
		List<Restaurant>list=restaurantService.findByLocNear(point, distance);
		int cnt=0;
		ArrayList<Simil>csList=new ArrayList<>();
		for(Restaurant el:list) {
			int[] a= {0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0};
			int[] b=new int[24];
			int idx=0;
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
    	return result;
    }
}
