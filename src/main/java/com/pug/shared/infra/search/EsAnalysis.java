package com.pug.shared.infra.search;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

/**
 * Configures custom analyzers and normalizers for Elasticsearch indexing and searching.
 */
@ApplicationScoped
public class EsAnalysis implements ElasticsearchAnalysisConfigurer {
  /**
   * Configures custom analyzers and normalizers.
   *
   * @param ctx the Elasticsearch analysis configuration context.
   */
  @Override
  public void configure(ElasticsearchAnalysisConfigurationContext ctx) {
    ctx.normalizer("folding_lowercase").custom().tokenFilters("lowercase", "asciifolding");

    ctx.tokenFilter("edge_ngram_custom")
            .type("edge_ngram")
            .param("min_gram", 2)
            .param("max_gram", 20);

    ctx.analyzer("pt_folded")
            .custom()
            .tokenizer("standard")
            .tokenFilters("lowercase", "asciifolding");

    ctx.analyzer("auto_ngram")
            .custom()
            .tokenizer("standard")
            .tokenFilters("lowercase", "asciifolding", "edge_ngram_custom");
  }
}
