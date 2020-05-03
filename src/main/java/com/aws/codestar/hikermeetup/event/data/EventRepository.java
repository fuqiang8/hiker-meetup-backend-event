package com.aws.codestar.hikermeetup.event.data;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, UUID> {
}
