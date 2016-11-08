# File Service
A file service with a REST API

## REST API


### PUT File

Put an object to the file store.

This operation returns a token (UUID).

### GET File

Get an object from the file store using the token (UUID)

### DELETE File

Delete an object in the file store using the token (UUID)

### POST tags

Post tags about an object in the file store.

This operation requires a token (UUID).

The tags will be indexed and used for file search/retrieval.

### SEARCH File

Return the tokens (UUIDs) of files that satisfy the search criteria.


## Mandatory Request Headers

| Header Name        | Example       | Description  |
| ------------------ |:-------------:| -----:|
| Content-Length     | 1200          | All requests include a valid Content-Length header |
| Content-Type       | text/html     | See supported Content Types section |



## Supported Content Types

| Content-Types      | File Type |
| ------------------ |-----------| 
| text/html          | html |
| text/css           | css  |
| text/javascript    | js   |
| text/xml           | xml  |
| image/jpeg         | jpg  |
| image/png          | png  | 
