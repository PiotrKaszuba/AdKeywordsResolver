package com.put.Chatterbox.Controller;

import org.springframework.web.bind.annotation.RestController;
import com.put.Chatterbox.utils.NLPResources;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.rowset.CachedRowSet;

import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.Stemmer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
/** Credits:
 *  STANFORD CORE NLP.
 *  Stopwords: Copyright (c) 2017 Peter Graham, contributors. Released under the Apache-2.0 license.
 */



@RestController
public class BaseRequestController {
	
	@Autowired
	private NLPResources resources;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	
	public Map<String, Long> getTokenCount(String text) {

		StanfordCoreNLP pipeline = resources.getPipeline();
		Stemmer stemmer = resources.getStemmer();
		ArrayList stopwords = resources.getStopwords();
		List<String> tags = resources.getTags();
		
		
		CoreDocument document = new CoreDocument(text);
		pipeline.annotate(document);
		List<CoreLabel> tokens = document.tokens();
		
		
		
		
		
		
		List<String> target = new LinkedList();
		for (CoreLabel token : tokens) {
			for(String tag : tags) {
				String tag1 = token.tag();
				if (tag1.startsWith(tag) || tag1.equals(tag)) {
					List temp = new LinkedList();
					
					String lemma = token.lemma().toLowerCase();
					String stemmed = stemmer.stem(lemma).toLowerCase();
					
					temp.add(token.originalText().toLowerCase());
					temp.add(lemma);
					temp.add(stemmed);
					
					if(Collections.disjoint(stopwords, temp))
					{
						target.add(stemmed);
						
					}
					break;
					
					
				}
			}	
		}
			
		Map<String, Long> counts =
			    target.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		
		
		
		return counts;
	}
	
	public Double getWeight(int N, int X, int T) {
		
		return Math.sqrt(Math.max((N*X - T), 0.0));
		
	}
	
	public String getCategory(Map<String, Map<String, Integer>> total, int NumberOfCat) {
		
		final Map<String, Double> weights = new HashMap<>();
		
		total.entrySet().stream().forEach(word -> {
			
			Integer catTotal = word.getValue().get("tttotalll");
			if(catTotal != null)
			
				word.getValue().entrySet().stream().forEach(category ->{
				
				if(!category.getKey().equals("tttotalll")){
				Double currWeight = weights.get(category.getKey());
				if (currWeight==null)
					currWeight = getWeight(NumberOfCat, category.getValue(), catTotal);
				else currWeight = currWeight+getWeight(NumberOfCat, category.getValue(), catTotal);
				
				weights.put(category.getKey(), currWeight);
			
				
				
				
				
				}
			});
			
		
			
			
			
		});
		
		Double max =0.0;
		String category = "none";
		for(Map.Entry<String, Double> x : weights.entrySet()) {
			if(x.getValue() > max) {
				max = x.getValue();
				category = x.getKey();
			}
		}
		
			
	
		
		return category;
	}
	
	@RequestMapping("/get")
	public String category(@RequestParam(value="message", required = false) String messag){
		
		List<String> messages = new ArrayList<String>();
		messages.add(messag);
		final StringBuilder sb = new StringBuilder("Select * from words where false");
		//while(crs.next()) {
			//sb.append(crs.getString(1) + crs.getString(2) + crs.getInt(3));
		//}
		//return(sb.toString());/*
		final StringBuilder ret = new StringBuilder();
		SqlRowSet NSet =  jdbcTemplate.queryForRowSet("select Count( distinct category) from words");
		NSet.next();
		Integer N = NSet.getInt(1);
		
		messages.forEach(item ->{
			Map<String, Long> counts = getTokenCount(item);
			counts.entrySet().stream().forEach(e -> {
        		
        		sb.append(" or word='" + e.getKey()+"'");
        	
        	});
			SqlRowSet crs = jdbcTemplate.queryForRowSet(sb.toString());
			
			Map<String, Map<String, Integer>> total = new HashMap<>();
			//Map<String, Integer> categoryWord = new HashMap<>();
			while(crs.next()) {
				String word = crs.getString(1);
				String category=crs.getString(2);
				Integer value = crs.getInt(3);
				
				Map<String, Integer> categoryWord = total.get(word);
				
				if(categoryWord==null)
					categoryWord = new HashMap<String, Integer>();
				
				Integer oldVal = categoryWord.get("tttotalll");
				Integer newVal = 0;
				if(oldVal != null )
					newVal = value + oldVal;
				else
					newVal = value;
				
				categoryWord.put("tttotalll", newVal);
				categoryWord.put(category, value);
				
				total.put(word, categoryWord);
				
				
	
			}
			ret.append(getCategory(total, N));
		});
		return ret.toString();
		
	}
	
    @RequestMapping("/")
    public String index(@RequestParam(value="message", required=false) String messag, @RequestParam(value="category", required=false)String categori) {
        
    	
    	final StringBuilder ret = new StringBuilder();
        final List<String> query = new ArrayList<String>();
        List<String> messages = new ArrayList<String>();
        List<String> categories = new ArrayList<String>();

        messages.add(messag);
        categories.add(categori);
        ret.append("Token lists:<br>--------------------<br>");
        final Map CategoryMap = new HashMap();
        if( categories!=null) {
	      
	        for(int i=0; i < messages.size(); i++) {
	        	if(categories.size() >= i+1) {
	        		CategoryMap.put(messages.get(i), categories.get(i));
	        	}
	        	else {
	        		break;
	        	}
	        }
        
        }
        if(messages != null) {
        	
        	messages.forEach(item ->{
        	
        	
        	Object category = (CategoryMap.get(item));
        	if(category != null) { 
        		ret.append(category);
        		ret.append("<br>");
        	}
        
        	
        	
        	Map<String, Long> counts = getTokenCount(item);
        	counts.entrySet().stream().forEach(e -> {
        		ret.append(e.getKey() +", "+ e.getValue() +"<br>");
        		if(category !=null)
        			query.add("Insert into words values('"+e.getKey()+"','"+category+"','"+e.getValue()+"') ON DUPLICATE KEY UPDATE count=count+"+e.getValue()+";");
        	
        	
        	});
        	
        	ret.append("--------------------<br>");
        	
        });;
        }
        String[] qr = new String[query.size()];
        for(int i =0; i<query.size();i++) {
        	qr[i] = query.get(i);
        }
        jdbcTemplate.batchUpdate(qr);
        
        return ret.toString();
       
    }


}