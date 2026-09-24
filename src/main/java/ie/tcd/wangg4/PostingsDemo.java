package ie.tcd.wangg4;

import java.io.IOException;

import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.util.BytesRef;

import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.DocIdSetIterator;

public class PostingsDemo {

    private Analyzer analyzer;
    private Directory directory;

    public PostingsDemo() throws IOException {
        analyzer = new StandardAnalyzer();
        directory = Common.openIndexDirectory();
    }

    /**
     * Build a new index with term vectors. Overrides the existing index.
     * @param args list of files or directories
     * @throws IOException
     */
    public void buildIndex(String[] args) throws IOException {
        // Create a new field type which will store term vector information
        FieldType ft = new FieldType(TextField.TYPE_STORED);
        ft.setTokenized(true); // True by default
        ft.setStoreTermVectors(true);
        // The following fields are unused in the demo
        // You are free to play around with them
        ft.setStoreTermVectorPositions(true);
        ft.setStoreTermVectorOffsets(true);
        ft.setStoreTermVectorPayloads(true);

        // Create and configure an index writer
        IndexWriter iwriter = Common.createIndexWriter(directory, analyzer);

        // Add all input documents to the index
        for (Path file : Common.collectFiles(args)) {
            System.out.printf("Indexing \"%s\"\n", file);
            Document doc = new Document();
            String content = Common.readFile(file);
            doc.add(new StringField("filename", file.toString(), Field.Store.YES));
            // Note this uses our custom file type, different from IndexCorpus.java
            doc.add(new Field("content", content, ft));
            iwriter.addDocument(doc);
        }
        
        // close the writer
        iwriter.close();
    }

    public void postingsDemo() throws IOException {
        DirectoryReader ireader = DirectoryReader.open(directory);
    
        // Use IndexSearcher to retrieve some arbitrary document from the index        
        IndexSearcher isearcher = new IndexSearcher(ireader);
        Query queryTerm = new TermQuery(new Term("content","raven"));
        ScoreDoc[] hits = isearcher.search(queryTerm, 1).scoreDocs;
        
        // Print to stdout and return if no hits
        if (hits.length <= 0) {
            System.out.println("Failed to retrieve a document");
            ireader.close();
            return;
        }

        // Get the fields of the first hit document
        int docID = hits[0].doc;
        Fields fields = ireader.termVectors().get(docID);
        if (fields == null) {
            System.out.println("Document has no term vectors. Are you using the correct index?");
            ireader.close();
            return;
        }

        for (String field : fields) {
            // For each field, get the terms it contains i.e. unique words
            Terms terms = fields.terms(field);

            // Iterate over each term in the field
            BytesRef termByte = null;
            TermsEnum termsEnum = terms.iterator();
            while ((termByte = termsEnum.next()) != null) {
                // Retrieve the posting for this term
                // Supplied with FREQS flag for term frequency
                PostingsEnum posting = termsEnum.postings(null, PostingsEnum.FREQS);
                
                // This only processes the single document we retrieved earlier
                while (posting.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                    // convert the term from byte array to a string
                    String termString = termByte.utf8ToString();

                    // extract some stats from the index
                    Term term = new Term(field, termString);
                    long freq = posting.freq();
                    long docFreq = ireader.docFreq(term);
                    long totFreq = ireader.totalTermFreq(term);

                    // print the results
                    System.out.printf(
                        "%-16s : freq = %4d : totfreq = %4d : docfreq = %4d\n",
                        termString, freq, totFreq, docFreq
                    );
                }
            }
        }

        // Close everything when we're done
        ireader.close();
    }

    public void shutdown() throws IOException {
        directory.close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            System.out.println("Expected corpus as input");
            System.exit(1);            
        }

        PostingsDemo qi = new PostingsDemo();
        qi.buildIndex(args);
        qi.postingsDemo();
        qi.shutdown();
    }
}
