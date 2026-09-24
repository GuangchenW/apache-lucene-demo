package ie.tcd.wangg4;

import java.io.IOException;

import java.util.Scanner;

import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

public class QueryIndex2
{

    // the location of the search index
    private static String INDEX_DIRECTORY = "../index";
    
    // Limit the number of search results we get
    private static int MAX_RESULTS = 10;
    
    public static void main(String[] args) throws IOException, ParseException {
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
            // trim leading and trailing whitespace from the query
            queryString = queryString.trim();

            // if the user entered a querystring
            if (queryString.length() > 0)
            {
                // parse the query with the parser
                Query query = parser.parse(queryString);

                // Get the set of results
                ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;

                // Print the results
                Common.printResults(isearcher, hits);
                System.out.println();	
            }
            
            // prompt the user for input and quit the loop if they escape
            System.out.print(">>> ");
            queryString = scanner.nextLine();
        } while (!queryString.equals("\\q"));
        
        // close everything and quit
        scanner.close();
        ireader.close();
        directory.close();
    }
}
