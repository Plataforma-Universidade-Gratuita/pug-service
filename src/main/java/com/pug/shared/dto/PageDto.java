package com.pug.shared.dto;

public record PageDto (java.util.List<?> items, long total, int page, int size) {}
