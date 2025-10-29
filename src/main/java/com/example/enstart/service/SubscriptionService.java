package com.example.enstart.service;

import com.example.enstart.model.*;
import com.example.enstart.repository.HomepageRepository;
import com.example.enstart.repository.SubscriberRepository;
import com.example.enstart.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

    @Autowired
    private HomepageRepository homepageRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Transactional
    public void createSubscription(String email, String domain) {
        // 1. Find the service by domain
        Homepage homepage = homepageRepository.findByDomain(domain)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + domain));
        com.example.enstart.model.Service service = homepage.getService();

        // 2. Find or create the subscriber
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Subscriber newSubscriber = new Subscriber();
                    newSubscriber.setEmail(email);
                    return subscriberRepository.save(newSubscriber);
                });

        // 3. Create the subscription
        SubscriptionId subscriptionId = new SubscriptionId();
        subscriptionId.setSubscriberId(subscriber.getId());
        subscriptionId.setServiceId(service.getId());

        if (subscriptionRepository.existsById(subscriptionId)) {
            throw new RuntimeException("Already subscribed");
        }

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setSubscriber(subscriber);
        subscription.setService(service);
        subscription.setStatus("active");

        subscriptionRepository.save(subscription);
    }
}
