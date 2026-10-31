package com.example.javatest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DashboardMetrics(long totalOrders, long activeCustomers,
                               long productsCatalog, long totalEmployees) {}