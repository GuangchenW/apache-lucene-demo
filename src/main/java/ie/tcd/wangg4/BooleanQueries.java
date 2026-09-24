package ie.tcd.wangg4;

import java.io.IOException;

import org.apache.lucene.store.Directory;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;

public class BooleanQueries {
	public static void main(String[] args) throws IOException {
		// Open the folder that contains our search index
		Directory directory = Common.openIndexDirectory();
		
		// Create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		// Builder class for creating our query
		BooleanQuery.Builder query = new BooleanQuery.Builder();

		// Keywords we want to find and the field to search
		Query term1 = new TermQuery(new Term("content", "raven"));
		Query term2 = new TermQuery(new Term("content", "lenore"));
		Query term3 = new TermQuery(new Term("content", "criticism"));

		// Construct our query using basic boolean operations
		query.add(term1, BooleanClause.Occur.MUST);   	// AND
		query.add(term2, BooleanClause.Occur.SHOULD);  	// OR
		query.add(term3, BooleanClause.Occur.MUST_NOT); // NOT

		// Get the set of results from the searcher
		ScoreDoc[] hits = isearcher.search(query.build(), Common.MAX_RESULTS).scoreDocs;
		
		// Print the results
		Common.printResults(isearcher, hits);

		// close everything we used
		ireader.close();
		directory.close();
	}
}
