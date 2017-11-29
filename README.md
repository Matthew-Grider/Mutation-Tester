# Mutations Tester #
### by Matthew Grider and Nick Zakharov ###

## Set Up ##
This project will work best on the repository of my first hw in this class
https://github.com/ReactiveX/RxJava

when you have the RXJava project you will need to compile the tests, easyies way to do this is just run them all one time through

Also it is very important that you modify the config file to set the right directory paths to the YOUR LOCAL PATH/out/production/classes and YOUR LOCAL PATH/out/test/classes of the RXJava project on your system.
If you are on a unix based system dont forget to change your \ to / or visa versa in a windows system.

Also the last part of the config.txt file is the list of classes to be modified, the list is shorter right now and you can add more if you want to but there are 6000+ tests in this project so I tried to keep it
shor to improve run time, as it will most likely take better part of an hour to run all the mutations and tests as it is now.

The code is reasonably documented so it should be straight forward enough to see what is done but at a high level the process goes something like this:

1. get paths and load in the classes for the base tests

2. run base tests -runtime: 8 min or so

3. created copies of all classes files for the mutation process so we dont damage actual code -runtime: 3 min

4. define rules for mutation and create som MutationTest objects and a thread pool with 3 threads

5. pass the 6 different MutationTests to the thread pool to be executed

6. inside each run it will open up and mutate the classes listed in the config according to the different mutation rules

7. then run each of those different mutations against the tests again -runtime: 50 min or more, you have to run 6000 test 6 times so it can take a while sorry :(

8. print all results to log files

9. after all threads finish, compare all collected tests data to see if the mutations were detected and print the resuslts to the termnal

10. its supposed to remove all created files and dir but this is still buggy as it terminates before all the deletions are finished sometimes


ps: while the tests are running expect a bunch of scary failure errors, that is the tests not being able to run correctly which is caused possibly by the execution but also there are several hundered tests in
the project that are built to fail anyways.