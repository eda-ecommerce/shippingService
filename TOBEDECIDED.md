# Open Issues

- [ ] (Eventing) Should events really be so coarsely defined? E.g order topic instead of OrderConfirmed, OrderRequested etc
  - Should take into acoount things like retry topics + queues per topic, as they would give important metrics
- [ ] (Architecture) Should common operations be bundle in an application service instead of the current handler-per operation approach?
  - I think the injection of different operations into one another is bad
- [ ] (Architecture) Does the current Message/Event/Command abstraction make sense?
  - In the end, I couple the specific event to a payload, which is basically controlled by the outside. This makes the abstraction a bit useless?
  - On the other hand, I can then create the same event via different sources, but that might also be accomplished with the handler pattern, i think
- 