package ie.tcd.wangg4;

import java.util.Arrays;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        String step = args[0];
        String[] stepArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (step) {
            case "create-index" -> CreateIndex.main(stepArgs);
            case "index-corpus" -> IndexCorpus.main(stepArgs);
            case "boolean-query" -> BooleanQueries.main(stepArgs);
            case "query-index" -> QueryIndex.main(stepArgs);
            case "postings-demo" -> PostingsDemo.main(stepArgs);
            default -> {
                printUsage();
                System.exit(1);
            }
        }
    }    

    // Print how to run each step.
    private static void printUsage() {
        System.out.println("""
            Usage: java -jar target/ir-demo.jar <step> [args...]

            Steps:
              create-index                      Step 1: index a hard-coded document
              index-corpus <files|folders>      Step 2: index a corpus
              boolean-query                     Step 3: query the index with a hard-coded BooleanQuery
              query-index                       Step 4: interactive QueryParser
              postings-demo <files|folders>     Step 5: index the corpus with term vectors and print term stats

            Example:
              java -jar target/ir-demo.jar index-corpus data/example/corpus
            """);
    }
}
