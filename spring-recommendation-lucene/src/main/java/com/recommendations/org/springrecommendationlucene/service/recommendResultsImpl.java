package com.recommendations.org.springrecommendationlucene.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class recommendResultsImpl implements recommendResults {

    private static final Logger LOGGER = LoggerFactory.getLogger(recommendResultsImpl.class);

    @Override
    public void indexDirectory(IndexWriter writer, File dir) throws IOException {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                this.indexDirectory(writer, f); // recurse
            } else if (f.getName().endsWith(".txt")) {
                // call indexFile to add the title of the txt file to your index (you can also
                // index html)
                this.indexFile(writer, f);
            }
        }
    }

    @Override
    public void indexFile(IndexWriter writer, File f) throws IOException {
        LOGGER.debug("Indexing " + f.getName());
        Document doc = new Document();
        doc.add(new TextField("filename", f.getName(), TextField.Store.YES));
        // open each file to index the content
        try {
            FileInputStream is = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
            reader.close();
            doc.add(new TextField("contents", stringBuffer.toString(), TextField.Store.YES));
        } catch (Exception e) {
            LOGGER.debug("Something wrong with indexing content of the file: " + f.getName());
            e.printStackTrace();
        }
        writer.addDocument(doc);
    }

    @Override
    public List<List<String>> recommendationList(List<String> inputcommands) throws IOException, ParseException {
        File dataDir = new File("../content"); // my sample file folder path
        // Check whether the directory to be indexed exists
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            throw new IOException(dataDir + " does not exist or is not a directory");
        }
        Directory indexDir = new RAMDirectory();

        // Specify the analyzer for tokenizing text.
        EnglishAnalyzer analyzer = new EnglishAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDir, config);

        // call indexDirectory to add to your index
        // the names of the txt files in dataDir
        this.indexDirectory(writer, dataDir);
        writer.close();

        // Query string!
        // String querystr = "contents:Create a hash table with an initial size of 64.";
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(reader);
        String queryString = null;
        QueryParser queryParser = new QueryParser("contents", analyzer);
        TopScoreDocCollector collector = null;
        Query query = null;
        ScoreDoc[] hits = {};
        int docId = 0;
        Document d = null;
        List<List<String>> recommendationResults = new ArrayList<>();
        for (int i = 0; i < inputcommands.size(); i++) {
            queryString = inputcommands.get(i);
            queryString = queryString.replaceAll(":", " ");
            queryString = queryString.replaceAll("contents", " ");
            queryString = queryString.replaceAll("[^a-zA-Z0-9 ]", " ");
            queryString = "contents:" + queryString;
            query = queryParser.parse(queryString);
            collector = TopScoreDocCollector.create(hitsPerPage);
            searcher.search(query, collector);
            hits = collector.topDocs().scoreDocs;
            List<String> resultsPerPost = new ArrayList<>();
            for (int j = 0; j < hits.length; j++) {
                docId = hits[j].doc;
                d = searcher.doc(docId);
                resultsPerPost.add(d.get("contents"));
                //System.out.println((j + 1) + ". " + d.get("filename"));
            }
            recommendationResults.add(resultsPerPost);
        }
        reader.close();

        return recommendationResults;
    }
}
