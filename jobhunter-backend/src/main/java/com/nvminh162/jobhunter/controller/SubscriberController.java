package com.nvminh162.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nvminh162.jobhunter.domain.Subscriber;
import com.nvminh162.jobhunter.service.SubscriberService;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;
import com.nvminh162.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/${api.version}")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a new subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        boolean isExist = this.subscriberService.isExistEmail(subscriber.getEmail());
        if(isExist) {
            throw new IdInvalidException("Email " + subscriber.getEmail() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> update(@Valid @RequestBody Subscriber reqSub) throws IdInvalidException {
        Subscriber subDB = this.subscriberService.findById(reqSub.getId());
        if(subDB == null) {
            throw new IdInvalidException("ID " + reqSub.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriberService.update(subDB, reqSub));
    }


}
