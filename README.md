# Fund Transfer

### Task description: 
Define and implement a RESTful API that performs finds transfer between two accounts with currency exchange.

### Requirements:
* Implementation has to be done in Java
* No security layer (authentication / authorization) needs to be provided
* Implementation has to be able to support being invoked concurrently by multiple users/systems
* The minimal attributes to define an Account are:
  * An owner ID (numeric)
  * A Currency (String)
  * A balance (numeric)
* Exchange rates can be retrieved from external APIs
* Program has to be runnable without anu special software/container
* Functionality covered with tests
* Fund transfer should fail if:
  * Either the debit or the credit account does not exist
  * The exchange rate cannot be retrieved
  * The balance of the debit account is not sufficient
  
### Additional information:
* The code is expected to be of good quality and easy to maintain
* As business specification is very light, use common sense in case of doubt
