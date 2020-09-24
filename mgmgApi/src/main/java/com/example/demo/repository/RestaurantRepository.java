package com.example.demo.repository;

import java.util.List;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.Restaurant;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, Integer>{
	List<Restaurant> findByLocNear(Point loc,Distance distance);
}
