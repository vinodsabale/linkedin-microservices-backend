package com.linkdin.searchservice.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.linkdin.searchservice.model.PostDocument;

public interface PostSearchRepository  extends ElasticsearchRepository<PostDocument, String>{
	@Query("{\"match\":{\"content\":{\"query\":\"?0\",\"fuzziness\":\"AUTO\"}}}")
	public List<PostDocument> searchPosts(String post);
	
}
