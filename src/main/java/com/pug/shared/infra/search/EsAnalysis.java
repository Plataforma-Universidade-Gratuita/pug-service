package com.pug.shared.infra.search;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

/**
 * Application-scoped configuration component for Hibernate Search + Elasticsearch.
 * <p>
 * This class defines custom analyzers, normalizers, and token filters used during
 * index creation. These configurations enable advanced text search capabilities
 * such as accent-insensitive searching (ASCII folding), case-insensitivity,
 * fuzzy matching, and fast auto-completion (n-grams) for Portuguese text.
 */
@ApplicationScoped
public class EsAnalysis implements ElasticsearchAnalysisConfigurer {

  /**
   * Configures the custom analysis components applied to the Elasticsearch indexes.
   *
   * @param ctx the context provided by Hibernate Search to register custom analyzers and filters
   */
  @Override
  public void configure(ElasticsearchAnalysisConfigurationContext ctx) {
    /*
     * Normalizer: Used for keyword fields and sorting.
     * Converts text to lowercase and strips accents (e.g., "São Paulo" -> "sao paulo").
     */
    ctx.normalizer("folding_lowercase").custom().tokenFilters("lowercase", "asciifolding");
    /*
     * Token Filter: Generates prefix tokens of length 2 to 20.
     * E.g., "Joinville" -> "Jo", "Joi", "Join", etc. Essential for "type-as-you-go" autocomplete.
     */
    ctx.tokenFilter("edge_ngram_custom")
            .type("edge_ngram")
            .param("min_gram", 2)
            .param("max_gram", 20);
    /*
     * Analyzer: Standard text analyzer for Portuguese text.
     * Tokenizes by standard word boundaries, lowercases, and strips accents.
     * Used for standard full-text matching and fuzzy searches.
     */
    ctx.analyzer("pt_folded")
            .custom()
            .tokenizer("standard")
            .tokenFilters("lowercase", "asciifolding");
    /*
     * Analyzer: Specialized text analyzer for autocomplete text fields.
     * Combines the Portuguese text rules with the edge n-gram filter to match partial words efficiently.
     */
    ctx.analyzer("auto_ngram")
            .custom()
            .tokenizer("standard")
            .tokenFilters("lowercase", "asciifolding", "edge_ngram_custom");
  }
}