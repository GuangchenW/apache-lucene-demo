package ie.tcd.wangg4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public class Common {
    // Path to the index (relative to where you run `java`)
    public static final String INDEX_DIR = "index";
    // Maximum number of search results to show
    public static final int MAX_RESULTS = 10;

    // Open the index folder on disk
    public static Directory openIndexDirectory() throws IOException{
        return FSDirectory.open(Paths.get(INDEX_DIR));
    }

    /**
     * Creates a new <code>IndexWriter</code>.
     * @param directory the directory for the new writer
     * @param analyzer the analyzer used by the new writer
     * @param mode the open mode for the new writer
     * @return a new <code>IndexWriter</code>
     * @throws IOException
     */
    public static IndexWriter createIndexWriter(
        Directory directory, Analyzer analyzer, IndexWriterConfig.OpenMode mode
    ) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(mode);
        return new IndexWriter(directory, config);
    }

    /**
     * Creates a new {@code IndexWriter} with
     * {@code mode} as {@code IndexWriterConfig.OpenMode.CREATE}.
     * <p>
     * Replaces the existing index.
     * @throws IOException
     * @see #createIndexWriter(Directory, Analyzer, IndexWriterConfig.OpenMode)
     */
    public static IndexWriter createIndexWriter(
        Directory directory, Analyzer analyzer
    ) throws IOException {
        return createIndexWriter(directory, analyzer, IndexWriterConfig.OpenMode.CREATE);
    }

    /**
     * Turns command-line arguments into a list of files to index.
     * <p>
     * A file is added as-is. A folder adds every regular file inside it,
     * including files in sub-folders, sorted by path.
     * @param args file and/or folder paths
     * @return list of files to index
     * @throws IOException if a folder cannot be read
     */
    public static List<Path> collectFiles(String[] args) throws IOException {
        List<Path> fileList = new ArrayList<Path>();
        for (String arg : args) {
            Path p = Paths.get(arg);
            if (Files.isRegularFile(p)) {
                fileList.add(p);
            } else if (Files.isDirectory(p)) {
                // Files.walk() visits the folder and all its sub-folders.
                try (Stream<Path> files = Files.walk(p)) {
                    files.filter(Files::isRegularFile).sorted().forEach(fileList::add);
                }
            } else {
                System.out.printf("Skipping \"%s\": not a file or folder\n", arg);
            }
        }
        return fileList;
    }

    public static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    /**
     * Prints the rank, file name, and score for each hit.
     * @param isearcher
     * @param hits
     * @throws IOException
     */
    public static void printResults(IndexSearcher isearcher, ScoreDoc[] hits) throws IOException {
        StoredFields storedFields = isearcher.storedFields();
        System.out.println("Documents: " + hits.length);
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = storedFields.document(hits[i].doc);
            System.out.println(i + ") " + hitDoc.get("filename") + " " + hits[i].score);
        }
    }
}
