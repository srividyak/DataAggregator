# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* This is the standalone backend component of data aggregator which pulls data from various sources.
* 1.0

### How do I get set up? ###

* To set up data aggregator on your local box, you'd need java 7, gradle (build tool), elastic search 1.2.1, apache kafka 2.10.
* Once you check out the code, execute: gradle run on the main directory. It would start pulling data and would dump collected data into elastic search. Refer config files for getting details about the ElasticSearch indexes and types where data is getting into.

### Who do I talk to? ###

* Repo owner or admin