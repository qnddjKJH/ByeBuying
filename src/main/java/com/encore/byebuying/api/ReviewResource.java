package com.encore.byebuying.api;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.encore.byebuying.domain.Item;
import com.encore.byebuying.domain.Review;
import com.encore.byebuying.domain.User;
import com.encore.byebuying.service.ItemService;
import com.encore.byebuying.service.ReviewService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewResource {
	private final ReviewService reviewService;
	private final ItemService itemService;
	private final int PAGECOUNT = 5;
	@GetMapping("/all")
	public ResponseEntity<Page<Review>> getReviews(
			@RequestBody GetReviewForm getReviewForm,
			@RequestParam(required = false, defaultValue="1",value="page") int page){
		Sort sort;
		if(getReviewForm.asc.equals("ASC") || getReviewForm.asc.equals("asc")) {
			sort = Sort.by(Sort.Direction.ASC, getReviewForm.sortname);
		}else {
			sort = Sort.by(Sort.Direction.DESC, getReviewForm.sortname);
		}
		Pageable pageable = PageRequest.of(page-1, PAGECOUNT,sort);
        
		Page<Review> review = reviewService.getReviews(pageable);
		return ResponseEntity.ok().body(review);
	}

	@GetMapping("/byItemname")
	public ResponseEntity<Page<Review>> getReviewByItemname(
			@RequestBody GetReviewForm getReviewForm,
			@RequestParam(required = false, defaultValue="1",value="page") int page){
		Sort sort;
		if(getReviewForm.asc.equals("ASC") || getReviewForm.asc.equals("asc")) {
			sort = Sort.by(Sort.Direction.ASC, getReviewForm.sortname);
		}else {
			sort = Sort.by(Sort.Direction.DESC, getReviewForm.sortname);
		}
		
        Pageable pageable = PageRequest.of(page-1, PAGECOUNT,sort);
        Page<Review> review = reviewService.getByItemname(pageable,getReviewForm.getItemname());
		return ResponseEntity.ok().body(review);
	}
	
	@GetMapping("/byUsername")
	public ResponseEntity<Page<Review>> getReviewByUsername(
			@RequestBody GetReviewForm getReviewForm,
			@RequestParam(required = false, defaultValue="1",value="page") int page){
		Sort sort;
		if(getReviewForm.asc.equals("ASC") || getReviewForm.asc.equals("asc")) {
			sort = Sort.by(Sort.Direction.ASC, getReviewForm.sortname);
		}else {
			sort = Sort.by(Sort.Direction.DESC, getReviewForm.sortname);
		}
		
        Pageable pageable = PageRequest.of(page-1, PAGECOUNT,sort);
        Page<Review> review = reviewService.getByUsername(pageable,getReviewForm.getUsername());
		return ResponseEntity.ok().body(review);
	}
	
	@Transactional
	@PostMapping("/save")
	public String saveReview(@RequestBody Review review){
		if(review.getDate()==null)review.setDate(new Date());
		reviewService.saveReview(review);
		Item item = itemService.getItemByItemname(review.getItemname());
		item.setReviewmean(Double.parseDouble(reviewService.getAvgScoreByItemname(item.getItemname())));
		item.setReviewcount(reviewService.countScoreByItemname(item.getItemname()));
		itemService.saveItem(item);
		return "SUCCESS";
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteReview(@RequestBody Review review){
		reviewService.deleteReview(review.getId());
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}
	
	@Transactional
	@PutMapping("/update")
	public ResponseEntity<?> updateReview(@RequestBody Review changedReview){
		Review review = reviewService.getReview(changedReview.getId());
		review.setDate(new Date());
		review.setScore(changedReview.getScore());
		review.setContent(changedReview.getContent());
		reviewService.saveReview(review);
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}
	
	// 테스트용 만들어봄
	@GetMapping("/avg")
	public String getAvgScoreByItemname(@RequestParam(defaultValue = "", value = "itemname") String itemname) {
		return reviewService.getAvgScoreByItemname(itemname)+" "+reviewService.countScoreByItemname(itemname);
	}
}

@Data
class GetReviewForm{
	String username;
	String itemname;
	String asc;
	String sortname;
}
