package com.aws.codestar.hikermeetup.event.data;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PagingEventRepository extends PagingAndSortingRepository<Event, UUID> {

    @EnableScan
    @EnableScanCount
    Page<Event> findAll(Pageable pageable);
}
