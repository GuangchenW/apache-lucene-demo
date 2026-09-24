package ie.tcd.wangg4;

import java.io.IOException;

import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


import org.apache.lucene.store.Directory;

import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

public class QueryIndex {
    public static void main(String[] args) throws IOException {
        // Open the folder that contains our search index
        Directory directory = Common.openIndexDirectory();
        
        // Analyzer used by the query parser.
        // Must be the same as the one used when creating the index
        Analyzer analyzer = new StandardAnalyzer();
        
        // create objects to read and search across the index
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        
        // Create the query parser. The default search field is "content", 
        // but we can use this to search across any field
        QueryParser parser = new QueryParser("content", analyzer);

        String queryString = "";
        Scanner scanner = new Scanner(System.in);
        do {
            // Trim leading and trailing whitespace from the query
            queryString = queryString.trim();
            // If the user entered a querystring
            if (queryString.length() > 0) {
                // Parse the query with the parser
                try {
                    Query query = parser.parse(queryString);
                    // Get the set of results
                    ScoreDoc[] hits = isearcher.search(query, Common.MAX_RESULTS).scoreDocs;
                    // Print the results
                    Common.printResults(isearcher, hits);
                    System.out.println();	
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            
            // prompt the user for input and quit the loop if they escape
            System.out.println("[Enter query below | Type \\q to quit]");
            System.out.print(">>> ");
            if (scanner.hasNextLine()) { queryString = scanner.nextLine(); }
        } while (!queryString.equals("\\q"));
        
        // close everything and quit
        scanner.close();
        ireader.close();
        directory.close();
    }
}
