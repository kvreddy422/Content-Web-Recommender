package com.recommendations.org.springrecommendationlucene.controller;

import com.recommendations.org.springrecommendationlucene.service.getInput;
import com.recommendations.org.springrecommendationlucene.service.recommendResults;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("recommendation")
public class MainController {

    @Autowired
    private getInput getInput;

    @Autowired
    private recommendResults recommendResults;

    @RequestMapping(value = "/inputRecommendations")
    public ResponseEntity<List<List<String>>> getInputRecommendations() {
        List<String> inputPosts = getInput.getPosts();
        List<List<String>> recommendations = new ArrayList<>();
        try {
            recommendations = recommendResults.recommendationList(inputPosts);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<List<String>>>(recommendations, HttpStatus.OK);
    }

    @RequestMapping(value = "/inputs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getInputs() {
        List<String> posts = getInput.getPosts();
        return new ResponseEntity<List<String>>(posts, HttpStatus.OK);
    }
}

