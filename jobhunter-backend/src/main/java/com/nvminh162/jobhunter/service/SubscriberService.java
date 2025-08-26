package com.nvminh162.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nvminh162.jobhunter.domain.Skill;
import com.nvminh162.jobhunter.domain.Subscriber;
import com.nvminh162.jobhunter.repository.SkillRepository;
import com.nvminh162.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    public boolean isExistEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subs) {
        // check skills
        if (subs.getSkills() != null) {
            List<Long> reqSkills = subs.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subs.setSkills(dbSkills);
        }

        return this.subscriberRepository.save(subs);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> optionalSub = this.subscriberRepository.findById(id);
        if (optionalSub.isPresent()) {
            return optionalSub.get();
        }
        return null;
    }

    public Subscriber update(Subscriber subsDB, Subscriber subsRequest) {
        // check skills
        if (subsRequest.getSkills() != null) {
            List<Long> reqSkills = subsRequest.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subsDB);
    }
}
