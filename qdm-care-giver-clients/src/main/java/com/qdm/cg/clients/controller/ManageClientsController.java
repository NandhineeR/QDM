package com.qdm.cg.clients.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qdm.cg.clients.dto.IssueStatus;
import com.qdm.cg.clients.response.ResponseInfo;
import com.qdm.cg.clients.serviceimpl.ManageClientService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/clients")
@Slf4j
public class ManageClientsController {
	@Autowired
	ManageClientService manageClientService;

	@GetMapping("/reports/get")
	public ResponseEntity<?> getReports(@RequestParam String clientId) {
		log.info("Reports for Client ID " + clientId);
		ResponseInfo responseinfo = manageClientService.getClientReport();
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/issues/list/get")
	public ResponseEntity<?> getIssuesList(@RequestParam String clientId) {
		ResponseInfo responseinfo = manageClientService.getIssueList();
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/issues/detail/get")
	public ResponseEntity<?> getIssueDetail(@RequestParam String issueId) {
		ResponseInfo responseinfo = manageClientService.getIssueDetail();
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@PutMapping("/issues/status/modify")
	public ResponseEntity<?> getDetailsList(@RequestBody IssueStatus issueStatus) {
		ResponseInfo responseinfo = new ResponseInfo();
		responseinfo.setStatus_code(200);
		responseinfo.setStatus("Success");
		responseinfo.setMessage("");
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/recommendations/details/get")
	public ResponseEntity<?> getRecommendationsDetailsList(@RequestParam String clientId) {
		ResponseInfo responseinfo = manageClientService.getRecommendations();
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/recommendations/products/list/get")
	public ResponseEntity<?> getRecommendationsProductsList(@RequestParam String clientId) {
		ResponseInfo responseinfo = manageClientService.getRecommendedProductList();
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/recommendations/products/track/get")
	public ResponseEntity<?> getRecommendationsProductsTrack(@RequestParam String clientId) {
		ResponseInfo responseinfo = manageClientService.getRecommendedProductTrack();
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/products/ratings/get")
	public ResponseEntity<?> getRatingsList(@RequestParam String productId) {
		ResponseInfo responseinfo = manageClientService.getProductRatings();
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/activities/summary/get")
	public ResponseEntity<?> getActivitySummary(@RequestParam String activityId) {
		ResponseInfo responseinfo = manageClientService.getActivitySummary();
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}

	@GetMapping("/activities/get")
	public ResponseEntity<?> getActivity(@RequestParam String clientId, @RequestParam(required = false) String event) {
		ResponseInfo responseinfo = manageClientService.getClientActivity(event);
		return new ResponseEntity<Object>(responseinfo, HttpStatus.OK);
	}
}
