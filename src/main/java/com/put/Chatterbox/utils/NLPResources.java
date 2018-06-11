package com.put.Chatterbox.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ch.qos.logback.core.Context;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Stemmer;

@Service
public class NLPResources {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	
	private String threads = "8";
	
	private String stopwordsPath = "stopwords";
	
	private List<String> tags;
	public NLPResources() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		/*
		log.info("-----------------aaaa-----------");
		threads = env.getProperty("NLP.pipeline.threads");
		log.info("-----------------bbbbb-----------");
		stopwordsPath = env.getProperty("NLP.stopwords.path");
		log.info("-----------------ccccc-----------");*/
		tags = Arrays.asList(("NN,VB,JJ".split(",")));
		
		log.info(threads);
		stemmer = new Stemmer();
		log.info("Stemmer set up.");
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		props.setProperty("threads", threads);
		
		log.info("Initializing CoreNLP to run on "+threads+" threads.");
		pipeline = new StanfordCoreNLP(props);
		log.info("CoreNLP set up");
		
		try {
			final Resource file = new ClassPathResource(stopwordsPath);
			InputStream stream = file.getInputStream();
			byte[] bytearray = new byte[stream.available()];
			stream.read(bytearray);
			JacksonJsonParser jsonparse = new JacksonJsonParser();
			String stopwordsJson = new String(bytearray, StandardCharsets.UTF_8);
			stopwords = (ArrayList) jsonparse.parseList(stopwordsJson);
			log.info("Stopwords loaded.");
			
		} catch (IOException e) {
				
				
				stopwords = new ArrayList();
				log.warn("Stopwords file not initialized - stopwords are empty.");
				
		}
		
		
			
	}
	public List<String> getTags() {
		return tags;
	}
	private Stemmer stemmer;
	public Stemmer getStemmer() {
		return stemmer;
	}
	private StanfordCoreNLP pipeline;
	private ArrayList stopwords;
	public StanfordCoreNLP getPipeline() {
		return pipeline;
	}
	public ArrayList getStopwords() {
		return stopwords;
	}
}
