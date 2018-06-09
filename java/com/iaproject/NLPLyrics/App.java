package com.iaproject.NLPLyrics;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.opencsv.CSVReader;

/**
 * Hello world!
 *
 */
public class App {

	static final String SAMPLE_CSV_FILE_PATH = "C:/Users/andrade-gamer/Desktop/PROJECT-LYRICS-NLP/DATASETS/lyrics_1.csv";
	static String query;

	public static void main(String[] args) throws IOException, ParseException {

		// Lucene doc analyzer
		StandardAnalyzer analyzer = new StandardAnalyzer();
		
		// create query
		String querystr = JOptionPane.showInputDialog("Please enter a value.");
		Query q = new QueryParser("Lyrics", analyzer).parse(querystr);
		
		// create the index
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index, config);

		// reading the document
		try (Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
				CSVReader csvReader = new CSVReader(reader);) {
			// reading per line
			String[] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) {
				Song song = new Song();
				song.setName(nextRecord[1]); // SONG COLUMN
				song.setArtist(nextRecord[3]); // ARTIST COLUMN
				song.setGenre(nextRecord[4]); // GENRE COLUMN
				song.setLyrics(nextRecord[5]); // LYRICS COLUMN
				addDoc(w, song);
			}
		}
		w.close();

		// search
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector);

		// rank Score
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// display results
		displayResults(hits, searcher);

		// close the reader
		reader.close();
	}

	private static void addDoc(IndexWriter w, Song song) throws IOException {
		// creating a logial document for indexing and searching
		Document doc = new Document();
		
		doc.add(new TextField("Song", song.getName(), Field.Store.YES));
		doc.add(new StringField("Artist", song.getArtist(), Field.Store.YES));
		doc.add(new StringField("Genre", song.getGenre(), Field.Store.YES));
		doc.add(new TextField("Lyrics", song.getLyrics(), Field.Store.YES));
		
		w.addDocument(doc);
	}

	private static void displayResults(ScoreDoc[] hits, IndexSearcher searcher) throws IOException {
		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". Song - " + d.get("Song") + "\t Genre" + d.get("Genre"));
		}
	}

}
