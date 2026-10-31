package com.example.javatest.controller;

import com.example.javatest.model.Employee;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;

@Controller
public class EmployeeController {

    private final RestClient restClient;

    public EmployeeController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/employees")
    public String employees(@RequestParam(defaultValue = "") String name,
                            @RequestParam(defaultValue = "") String reportsTo,
                            HttpSession session,
                            Model model) {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        List<Employee> all;
        try {
            all = restClient.get()
                    .uri("/employees")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Employee>>() {});
            if (all == null) all = List.of();
        } catch (HttpClientErrorException.Unauthorized e) {
            session.invalidate();               // token expired or invalid
            return "redirect:/login";
        } catch (RestClientException e) {
            all = List.of();
            model.addAttribute("error", "Could not load employees from the API.");
        }

        // dropdown values: distinct, trimmed, sorted
        List<String> reportsToList = all.stream()
                .map(Employee::getReportsToName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .toList();

        // filters
        String nameFilter = name.trim().toLowerCase();
        List<Employee> filtered = all.stream()
                .filter(e -> nameFilter.isEmpty()
                        || e.getFullName().toLowerCase().contains(nameFilter))
                .filter(e -> reportsTo.isEmpty()
                        || reportsTo.equals(e.getReportsToName()))
                .toList();

        model.addAttribute("employees", filtered);
        model.addAttribute("reportsToList", reportsToList);
        model.addAttribute("name", name);
        model.addAttribute("reportsTo", reportsTo);
        return "employees/index";
    }

    @GetMapping("/employees/edit/{id}")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            Employee employee = restClient.get()
                    .uri("/employees/{id}", id)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Employee.class);

            List<Employee> managers = restClient.get()
                    .uri("/employees")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Employee>>() {});

            model.addAttribute("employee", employee);
            model.addAttribute("managers", managers == null ? List.of() : managers);
            return "employees/edit";

        } catch (HttpClientErrorException.Unauthorized e) {
            session.invalidate();
            return "redirect:/login";
        } catch (RestClientException e) {
            return "redirect:/employees";
        }
    }

    @PostMapping("/employees/edit/{id}")
    public String editSubmit(@PathVariable int id,
                             @RequestParam Map<String, String> form,
                             @RequestParam(value = "Photo", required = false) MultipartFile photo,
                             HttpSession session) throws IOException {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        String photoBase64 = null;
        if (photo != null && !photo.isEmpty()) {
            photoBase64 = Base64.getEncoder().encodeToString(photo.getBytes());
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("LastName", form.get("LastName"));
        payload.put("FirstName", form.get("FirstName"));
        payload.put("Title", form.get("Title"));
        payload.put("TitleOfCourtesy", form.get("TitleOfCourtesy"));
        payload.put("BirthDate", blankToNull(form.get("BirthDate")));
        payload.put("HireDate", blankToNull(form.get("HireDate")));
        payload.put("Address", form.get("Address"));
        payload.put("City", form.get("City"));
        payload.put("Region", form.get("Region"));
        payload.put("PostalCode", form.get("PostalCode"));
        payload.put("Country", form.get("Country"));
        payload.put("HomePhone", form.get("HomePhone"));
        payload.put("Extension", form.get("Extension"));
        payload.put("Notes", form.get("Notes"));
        payload.put("PhotoPath", form.get("PhotoPath"));
        payload.put("ReportsTo", blankToNull(form.get("ReportsTo")));
        payload.put("Photo", photoBase64);

        try {
            restClient.put()
                    .uri("/employees/{id}", id)
                    .header("Authorization", "Bearer " + token)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Unauthorized e) {
            session.invalidate();
            return "redirect:/login";
        }

        return "redirect:/employees";
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
    @GetMapping("/employees/add")
    public String addForm(HttpSession session, Model model) {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("managers", loadManagers(token));
        } catch (HttpClientErrorException.Unauthorized e) {
            session.invalidate();
            return "redirect:/login";
        } catch (RestClientException e) {
            model.addAttribute("managers", List.of());
            model.addAttribute("error", "Could not load managers from the API.");
        }
        return "employees/add";
    }

    @PostMapping("/employees/add")
    public String addSubmit(@RequestParam Map<String, String> form,
                            @RequestParam(value = "Photo", required = false) MultipartFile photo,
                            HttpSession session,
                            Model model) throws IOException {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        String photoBase64 = null;
        if (photo != null && !photo.isEmpty()) {
            photoBase64 = Base64.getEncoder().encodeToString(photo.getBytes());
        }

        try {
            restClient.post()
                    .uri("/employees")
                    .header("Authorization", "Bearer " + token)
                    .body(buildPayload(form, photoBase64))
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/employees";

        } catch (HttpClientErrorException.Unauthorized e) {
            session.invalidate();
            return "redirect:/login";
        } catch (RestClientException e) {
            // API rejected the request or is unreachable: show the form again with a message
            model.addAttribute("error", "Could not save the employee. Please check the details and try again.");
            try {
                model.addAttribute("managers", loadManagers(token));
            } catch (RestClientException ex) {
                model.addAttribute("managers", List.of());
            }
            return "employees/add";
        }
    }

    private List<Employee> loadManagers(String token) {
        List<Employee> managers = restClient.get()
                .uri("/employees")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Employee>>() {});
        return managers == null ? List.of() : managers;
    }

    private static Map<String, Object> buildPayload(Map<String, String> form, String photoBase64) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("LastName", form.get("LastName"));
        payload.put("FirstName", form.get("FirstName"));
        payload.put("Title", form.get("Title"));
        payload.put("TitleOfCourtesy", form.get("TitleOfCourtesy"));
        payload.put("BirthDate", blankToNull(form.get("BirthDate")));
        payload.put("HireDate", blankToNull(form.get("HireDate")));
        payload.put("Address", form.get("Address"));
        payload.put("City", form.get("City"));
        payload.put("Region", form.get("Region"));
        payload.put("PostalCode", form.get("PostalCode"));
        payload.put("Country", form.get("Country"));
        payload.put("HomePhone", form.get("HomePhone"));
        payload.put("Extension", form.get("Extension"));
        payload.put("Notes", form.get("Notes"));
        payload.put("PhotoPath", form.get("PhotoPath"));
        payload.put("ReportsTo", blankToNull(form.get("ReportsTo")));
        payload.put("Photo", photoBase64);
        return payload;
    }

    @GetMapping("/employees/{id}")
    public String details(@PathVariable int id, HttpSession session, Model model) {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            Employee employee = restClient.get()
                    .uri("/employees/{id}", id)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Employee.class);

            model.addAttribute("employee", employee);
            return "employees/details";

        } catch (HttpClientErrorException.Unauthorized e) {
            session.invalidate();
            return "redirect:/login";
        } catch (RestClientException e) {
            return "redirect:/employees";
        }
    }
    @GetMapping("/employees/delete/{id}")
    public String deleteConfirm(@PathVariable int id, HttpSession session, Model model) {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            Employee employee = restClient.get()
                    .uri("/employees/{id}", id)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Employee.class);

            model.addAttribute("employee", employee);
            return "employees/delete";

        } catch (HttpClientErrorException.Unauthorized e) {
            session.invalidate();
            return "redirect:/login";
        } catch (RestClientException e) {
            return "redirect:/employees";
        }
    }

    @PostMapping("/employees/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            restClient.delete()
                    .uri("/employees/{id}", id)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Unauthorized e) {
            session.invalidate();
            return "redirect:/login";
        } catch (RestClientException e) {
            // e.g. employee has related orders: go back to the confirmation page with a message
            return "redirect:/employees/delete/" + id + "?error";
        }

        return "redirect:/employees";
    }
}