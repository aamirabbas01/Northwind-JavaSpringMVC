package com.example.javatest.controller;

import com.example.javatest.model.Book;
import com.example.javatest.model.Employee;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        return "index";

    }
//        List<Book> books = List.of(
//                new Book("Clean Code", "Robert C. Martin", 2008),
//                new Book("Effective Java", "Joshua Bloch", 2018),
//                new Book("Spring in Action", "Craig Walls", 2022)
//        );
//
//        model.addAttribute("books", books);   // "books" is the name used in the HTML
//         return "index";    }

    private final RestClient restClient = RestClient.create("http://localhost:3005");


}
