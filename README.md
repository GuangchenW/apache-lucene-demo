# Apache Lucene Demo

## About
Original scripts by Dr. Gary Munnelly.
Adapted for 2020 by Colin Daly.
Adapted for 2026 (Lucene 10.5) by Guangchen Wang.

These are the example programs from the CS7IS3 (TCD) Lucene tutorial and a good starting point for Assignment 1.

## Requirements
- Java 21 or newer
- Maven

## Build
```
mvn package
```
This produces a runnable jar: `target/ir-demo.jar`.

## Run
Run from the project root. The index is written to `./index`.
```
java -jar target/ir-demo.jar <step> [args...]
```
Run the jar with no arguments to see the usage message.

| Step | Command | Source | What it does |
|---|---|---|---|
| 1 | `create-index` | `CreateIndex.java` | Indexing one hard-coded document |
| 2 | `index-corpus <files\|folders>` | `IndexCorpus.java` | Indexing text files |
| 3 | `boolean-query` | `BooleanQueries.java` | Evaluating a hand-built `BooleanQuery` |
| 4 | `query-index` | `QueryIndex.java` | Interactive search |
| 5 | `postings-demo <files\|folders>` | `PostingsDemo.java` | Term stats (tfidf) |

Example usages:
```
java -jar target/ir-demo.jar index-corpus data/example/corpus
java -jar target/ir-demo.jar boolean-query
java -jar target/ir-demo.jar query-index
java -jar target/ir-demo.jar postings-demo data/example/corpus
```

## Notes
- Steps 3 and 4 require a built index, so step 2 must be run first. (The index from step 1 has no `content` field so it won't work).
- Steps 1, 2 and 5 will replace the existing index.
- You may see a `WARNING: Java vector incubator module is not readable` message. You can ignore it. To hide it, run `java --add-modules jdk.incubator.vector -jar ...`.

## Data
- `data/example/corpus`: Edgar Allan Poe texts.
