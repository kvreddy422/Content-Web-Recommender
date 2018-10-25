package com.recommendations.org.springrecommendationlucene.service;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface recommendResults {

    public void indexDirectory(IndexWriter writer, File dir) throws IOException;

    public void indexFile(IndexWriter writer, File f) throws IOException;

    public List<List<String>> recommendationList(List<String> inputcommands) throws IOException, ParseException;
}
