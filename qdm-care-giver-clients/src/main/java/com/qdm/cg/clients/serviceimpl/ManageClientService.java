package com.qdm.cg.clients.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.qdm.cg.clients.dto.ClientActivityDto;
import com.qdm.cg.clients.dto.ClientActivityResponse;
import com.qdm.cg.clients.dto.ClientActivitySummaryDto;
import com.qdm.cg.clients.dto.ClientInfoDto;
import com.qdm.cg.clients.dto.ClientReportResponse;
import com.qdm.cg.clients.dto.Equipment;
import com.qdm.cg.clients.dto.IssueDetailDto;
import com.qdm.cg.clients.dto.IssueDto;
import com.qdm.cg.clients.dto.IssueListResponse;
import com.qdm.cg.clients.dto.ProductRatingDto;
import com.qdm.cg.clients.dto.ProductRatingResponse;
import com.qdm.cg.clients.dto.Products;
import com.qdm.cg.clients.dto.RecommendationsDto;
import com.qdm.cg.clients.dto.RecommendationsTrackResponse;
import com.qdm.cg.clients.dto.RecommendedProductsDto;
import com.qdm.cg.clients.dto.RecommendedProductsResponse;
import com.qdm.cg.clients.dto.ReportsDto;
import com.qdm.cg.clients.dto.TimeLine;
import com.qdm.cg.clients.enums.ManageClientsConstants;
import com.qdm.cg.clients.enums.StatusEnum;
import com.qdm.cg.clients.response.ResponseInfo;

@Service
public class ManageClientService {

	public ResponseInfo getClientReport() {

		List<ReportsDto> reportsList = new ArrayList<ReportsDto>();
		reportsList.add(new ReportsDto("PreAssessment Reports", 1, 2));
		reportsList.add(new ReportsDto("Health Reports", 2, 2));
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(ClientReportResponse.builder().reports(reportsList).total_reports(reportsList.size()).build())
				.build();
	}

	public ResponseInfo getIssueDetail() {

		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(IssueDetailDto.builder().client_phone_no("9876543210").issue_id("#ICG12089")
						.issued_product("Physio-ultra(Monthly)").issued_client_name("")
						.issued_time("26-08-2020 17:02:09").issue_type("")
						.issue_title("Having trouble with access service").build())
				.build();
	}

	public ResponseInfo getIssueList() {
		int openCount = 0, resolvedCount = 0, pendingCount = 0;
		List<IssueDto> issueList = new ArrayList<IssueDto>();
		issueList
				.add(new IssueDto("#ICG1245", "Having trouble with access service", "Physio -Ultra (Monthly)", "Open"));
		issueList.add(
				new IssueDto("#ICG1246", "Having trouble with access service", "Physio -Ultra (Monthly)", "Resolved"));
		for (IssueDto dto : issueList) {

			if (dto.getIssue_status().equalsIgnoreCase(ManageClientsConstants.open_status)) {
				openCount++;
			} else if (dto.getIssue_status().equalsIgnoreCase(ManageClientsConstants.resolved_status)) {
				resolvedCount++;
			} else if (dto.getIssue_status().equalsIgnoreCase(ManageClientsConstants.resolved_status)) {
				pendingCount++;
			}
		}
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(IssueListResponse.builder().open_count(openCount).pending_count(pendingCount)
						.resolved_count(resolvedCount).issues_enum(StatusEnum.values()).issue_list(issueList).build())
				.build();

	}

	public ResponseInfo getRecommendations() {
		List<Equipment> equipmentList = new ArrayList<Equipment>();
		equipmentList.add(new Equipment("Zerostat spacer", "MYR 432", null));
		equipmentList.add(new Equipment("Test machine", "MYR 432", null));
		List<Products> productList = new ArrayList<Products>();
		productList.add(new Products("Physio -Ultra (Monthly)", "Get Everything just cost of Physio ", "123"));
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(RecommendationsDto.builder().equipments(equipmentList).products(productList).build()).build();

	}

	public ResponseInfo getProductRatings() {
		List<ProductRatingDto> productRatings = new ArrayList<ProductRatingDto>();
		productRatings.add(new ProductRatingDto("Product review", "", "5", "", "2", "08-24-2020"));
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(ProductRatingResponse.builder().ratings_list(productRatings).build()).build();

	}

	public ResponseInfo getActivitySummary() {
		ClientInfoDto clientInfo = new ClientInfoDto("ZeroSpacer", "male", 21, "9876543210", "87.09", "78.90");
		ClientActivitySummaryDto clientSummary = new ClientActivitySummaryDto(1, "test", "test", "08-24-2020", "",
				"This assessment is to help", clientInfo);
		return ResponseInfo.builder().status("Success").status_code(200).message("").data(clientSummary).build();

	}

	public ResponseInfo getClientActivity(String event) {

		ClientActivityDto clientInfo = new ClientActivityDto(1, "Pre-Assessment tips to Mia", "Mia Queen",
				"26-08-2020 17:02:09");
		ClientActivityDto clientInfo1 = new ClientActivityDto(2, "Pre-Assessment tips to Mia2", "Mia Queen",
				"26-08-2020 15:02:09");
		ClientActivityDto clientInfo2 = new ClientActivityDto(3, "Pre-Assessment tips to Mia3", "Mia Queen",
				"25-08-2020 17:02:09");
		List<ClientActivityDto> activityList = new ArrayList<ClientActivityDto>();
		activityList.add(clientInfo);
		activityList.add(clientInfo1);
		activityList.add(clientInfo2);
		if (event == null) {
			return ResponseInfo.builder().status("Success").status_code(200).message("")
					.data(ClientActivityResponse.builder().activities(activityList).build()).build();
		}
		List<ClientActivityDto> pastActivityList = new ArrayList<ClientActivityDto>();
		List<ClientActivityDto> upcomingActivityList = new ArrayList<ClientActivityDto>();
		String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
		SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");

		for (ClientActivityDto activity : activityList) {
			try {
				Date d1 = sdfo.parse(date);
				Date d2 = sdfo.parse(activity.getDate_time());
				if (d1.compareTo(d2) > 0) {
					pastActivityList.add(activity);
				} else {
					upcomingActivityList.add(activity);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if ("past".equals(event)) {
			return ResponseInfo.builder().status("Success").status_code(200).message("")
					.data(ClientActivityResponse.builder().activities(pastActivityList).build()).build();

		} else if ("upcoming".equals(event)) {
			return ResponseInfo.builder().status("Success").status_code(200).message("")
					.data(ClientActivityResponse.builder().activities(upcomingActivityList).build()).build();

		}
		return null;
	
	}

	public ResponseInfo getRecommendedProductList() {
		RecommendedProductsDto recommendedProduct = new RecommendedProductsDto("Zerostat spacer", 1, "MYR 432",
				"Recommended on July 04");
		// RecommendedProductsResponse
		List<RecommendedProductsDto> recommendedProductList = new ArrayList<RecommendedProductsDto>();
		recommendedProductList.add(recommendedProduct);
		return ResponseInfo.builder().status("Success").status_code(200).message("")
				.data(RecommendedProductsResponse.builder().recommended_products_list(recommendedProductList).build())
				.build();

	}

	public ResponseInfo getRecommendedProductTrack() {
		RecommendationsTrackResponse response = new RecommendationsTrackResponse();
		List<TimeLine> timeLine = new ArrayList<TimeLine>();
		timeLine.add(new TimeLine("Recommended", "24-08-2020", true));
		timeLine.add(new TimeLine("Consent Received", "24-08-2020", true));
		timeLine.add(new TimeLine("Purchased", "24-08-2020", true));
		timeLine.add(new TimeLine("Delivered", "24-08-2020", false));
		timeLine.add(new TimeLine("Demoed", "24-08-2020", false));
		response.setProduct_id(1);
		response.setProduct_name("Zerostat spacer");
		response.setProduct_price("MYR 432");
		response.setCurrent_status("Recommended on July 04");
		response.setTimeline(timeLine);
		return ResponseInfo.builder().status("Success").status_code(200).message("").data(response).build();

	}
}