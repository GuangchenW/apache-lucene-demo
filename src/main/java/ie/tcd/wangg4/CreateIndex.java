package ie.tcd.wangg4;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.ByteBuffersDirectory;
 
public class CreateIndex {
    public static void main(String[] args) throws IOException {
        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();
        
        // To store an index in memory
        //Directory directory = new ByteBuffersDirectory();

        // Create the IndexWriter
        Directory directory = Common.openIndexDirectory();
        IndexWriter iwriter = Common.createIndexWriter(directory, analyzer);
        
        // Create a new test document
        Document doc = new Document();
        doc.add(new TextField("super_name", "Spider-MAN1", Field.Store.YES));
        doc.add(new TextField("name", "Peter ParkER1", Field.Store.YES));
        doc.add(new TextField("category", "superheRO0", Field.Store.YES));

        // Save the document to the index
        iwriter.addDocument(doc);

        // Commit changes and close everything
        iwriter.close();
        directory.close();
    }
}
