package com.tech.task.repository.config;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CustomPageable implements Pageable {
    private final int page;
    private final int size;

    public CustomPageable(int page, int size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return (long) page * size;
    }

    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }

    @Override
    public Pageable next() {
        return new CustomPageable(page + 1, size);
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? new CustomPageable(page - 1, size) : first();
    }

    @Override
    public Pageable first() {
        return new CustomPageable(0, size);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new CustomPageable(pageNumber, size);
    }

    @Override
    public boolean hasPrevious() {
        return page > 0;
    }
}
