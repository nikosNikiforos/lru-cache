
# LRUcache Assignment
The following code applies to the first part of the assignment for Data Structures by Dimitrios Michael.
This is an implementation of a Cache memory using the LRU(Least Recently Used) strategy
combining HashMap, Node and double Linked List.
# MRU Cache Assignment
The program uses an interface and an abstract class to implement the general Cache class (MyCache) . 
Constructor has changed so each memory is created with capacity and policy of replacement. A new strategy is 
implemented so we can create a memory that evicts the last object used.
# Team33
it2021155 Aimilios Papakonstantinou
it21535   Nikolaos Nikiforos 


## Usage

Compile using 

```
mvn compile
```

Create a jar using 

```
mvn package
```

Run main using 

```
java -cp target/LRUCache-1.0-SNAPSHOT.jar org.hua.cache.Main

```

Run unit tests using 

```
mvn test
```

