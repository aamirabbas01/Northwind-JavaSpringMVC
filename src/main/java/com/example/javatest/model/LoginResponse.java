package com.example.javatest.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginResponse(String token,
                            String username,
                            @JsonProperty("profilePicture")
                            @JsonAlias({"ProfilePicture", "profilepicture", "profile_picture"})
                            String profilePicture) {}