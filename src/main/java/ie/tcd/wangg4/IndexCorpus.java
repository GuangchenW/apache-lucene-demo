package ie.tcd.wangg4;

import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
 
public class IndexCorpus {
	public static void main(String[] args) throws IOException {
		// validate arguments
		if (args.length <= 0) {
            System.out.println("Expected corpus as input");
            System.exit(1);            
        }

		// Analyzer that is used to process TextField
		Analyzer analyzer = new StandardAnalyzer();
		
		// List for pooling documents in corpus
		ArrayList<Document> documents = new ArrayList<Document>();

		for (Path file : Common.collectFiles(args)) {
			System.out.printf("Indexing \"%s\"\n", file);
			// Create a new document and add the file's contents
			Document doc = new Document();
			// Parse file content
			String content = Common.readFile(file);
			doc.add(new StringField("filename", file.toString(), Field.Store.YES));
			doc.add(new TextField("content", content, Field.Store.YES));
			// Add the file to list
			documents.add(doc);
		}

		// Create the IndexWriter
		Directory directory = Common.openIndexDirectory();
		IndexWriter iwriter = Common.createIndexWriter(directory, analyzer);
		// Write all the documents in the linked list to the search index
		iwriter.addDocuments(documents);

		// Commit everything and close
		iwriter.close();
		directory.close();
	}
}
