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
            case "postings" -> PostingsDemo.main(stepArgs);
            default -> {
                printUsage();
                System.exit(1);
            }
        }
    }    

    // TODO
    private static void printUsage() {
        System.out.println("Usage: java -jar ...");
    }
}
