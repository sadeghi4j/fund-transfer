# Fund Transfer

### Task description: 
Define and implement a RESTful API that performs funds transfer between two accounts with currency exchange.

### Requirements:
* Implementation has to be done in Java
* No security layer (authentication / authorization) needs to be provided
* Implementation has to be able to support being invoked concurrently by multiple users/systems
* The minimal attributes to define an Account are:
  * An owner ID (numeric)
  * A Currency (String)
  * A balance (numeric)
* Exchange rates can be retrieved from external APIs
* Program has to be runnable without any special software/container
* Functionality covered with tests
* Fund transfer should fail if:
  * Either the debit or the credit account does not exist
  * The exchange rate cannot be retrieved
  * The balance of the debit account is not sufficient
  
### Additional information:
* The code is expected to be of good quality and easy to maintain
* As business specification is very light, use common sense in case of doubt

## Solution:
### Explanations
* I split the main process into two parts. The first part (transferService.validateTransfer) is responsible to validation and retrieve exchange rate 
and second part (transferService.transferWithLock) is responsible for the main transfer process. I used `@Transactiona` for the second part just because 
a transaction should be done quickly. (validation and external service calls should be executed out of a transaction).  
* **LockModeType:** There are Two kinds of locking: **Pessimistic** and **Optimistic**. Pessimistic is done by `select ... for update` in database side 
but Optimistic is done using version attribute and throws exception if concurrent modification is in progress. 
Since it was necessary to save all the records in this assignment, I chose pessimistic locking. Two types of Pessimistic locking exists that either one can be used
  * PESSIMISTIC_READ: No one can write until transaction is done
  * PESSIMISTIC_WRITE: No one can read or write until transaction is done
* Some test cases implemented, and I tried to use JUnit 5, MockBean and MockMVC for them to show I'm familiar with these libraries.