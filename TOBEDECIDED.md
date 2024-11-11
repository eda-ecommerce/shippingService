# Open Issues

- [X] (Eventing) Should events really be so coarsely defined? E.g order topic instead of OrderConfirmed, OrderRequested etc
  - Should take into acoount things like retry topics + queues per topic, as they would give important metrics
  - Yes topic should be split so that there is one data definition per topic, but can be delayed.
- [X] (Architecture) Should common operations be bundle in an application service instead of the current handler-per operation approach?
  - I think the injection of different operations into one another is bad
  - It would be a nice architecture if we actually had use cases
  - We settled for Services for now.
- [X] (Architecture) Does the current Message/Event/Command abstraction make sense?
  - In the end, I couple the specific event to a payload, which is basically controlled by the outside. This makes the abstraction a bit useless?
  - On the other hand, I can then create the same event via different sources, but that might also be accomplished with the handler pattern, i think
    - --> Partly makes sense. But might only be necessary to store different event types in different tables in a DB, if wanted
    - The operation could be controlled by setting it in a field directly instead of using the class name
- [ ] (Database Architecture) Embedding all the stuff seems bad for performance, maybe revisit
  - Partly irrelevant, as ElementCollection does not embed under the hood
  - Different issue is accessing two tables at the same time when saving a shipment with product
- [ ] (Business Logic) If a shipping address is registered beforehand, we need to check if that has already happened
  - So there cannot be a simple "if exists throw" check to protect from overwriting something.
  - 