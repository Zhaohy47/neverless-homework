# demo

##Functionality
- [1] allow user to directly transfer money to another user
- [2] allow user to withdraw money from their account
- [3] allow user to check withdraw request status
- [4] support money transfer or withdraw on different currency

## For test purpose API
- /account/list (GET) to list account details
- /transfer/query (GET) to list transfer details

## FYI postman collection
### withdraw
curl --location 'http://localhost:8080/moneyTransfer/withdraw' \
--header 'Content-Type: application/json' \
--data '{
"userId":2,
"address":"testAddress",
"amount":1920,
"targetCurrency": "EUR"
}'

### transfer
curl --location 'http://localhost:8080/moneyTransfer/direc-transfer' \
--header 'Content-Type: application/json' \
--data '{
"fromUserId":1,
"toUserId":2,
"amount":10
}'

### query transfer status
curl --location 'http://localhost:8080/moneyTransfer/status?transferId=c7a16b71-5060-438b-b868-654149d74ff1' \
--data ''

### query user account detail 
curl --location 'http://localhost:8080/account/list?userIds=1%2C2' \
--data ''

### query transfer detail
curl --location 'http://localhost:8080/transfer/query?transferId=ac92d447-69e3-4a3d-bedc-827bbd12b470'

## Data init
Initial data of userAccount and exchangeRate are UserAccount.json and ExchangeRate.json respectively.