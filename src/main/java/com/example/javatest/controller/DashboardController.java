package com.example.javatest.controller;

import com.example.javatest.model.DashboardMetrics;
import com.example.javatest.model.MetricBox;
import com.example.javatest.model.MonthlySales;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Controller
public class DashboardController {

    private final RestClient restClient;

    public DashboardController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        DashboardMetrics data;
        try {
            data = restClient.get()
                    .uri("/dashboard/metrics")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(DashboardMetrics.class);
        } catch (RestClientException e) {
            data = new DashboardMetrics(0, 0, 0, 0);
        }

        List<MetricBox> metrics = List.of(
                new MetricBox("Total Orders", data.totalOrders(), "bg-info", "/orders/all"),
                new MetricBox("Active Customers", data.activeCustomers(), "bg-success", "/customers"),
                new MetricBox("Products Catalog", data.productsCatalog(), "bg-warning", "/business/products"),
                new MetricBox("Total Employees", data.totalEmployees(), "bg-danger", "/employees")
        );

        List<MonthlySales> salesData;
        try {
            salesData = restClient.get()
                    .uri("/dashboard/sales-data")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MonthlySales>>() {});
        } catch (RestClientException e) {
            // fallback data, same as the Django version
            salesData = List.of(
                    new MonthlySales("January", 15000),
                    new MonthlySales("February", 28000),
                    new MonthlySales("March", 18000),
                    new MonthlySales("April", 35000),
                    new MonthlySales("May", 42000),
                    new MonthlySales("June", 39000),
                    new MonthlySales("July", 48000));
        }

        model.addAttribute("metrics", metrics);
        model.addAttribute("salesData", salesData);
        return "dashboard";
    }
}