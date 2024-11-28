This is a RAG that responds to questions based on a document provided by a user.

POST /api/upload - upload file (.txt or .pdf), receive document id
params:
- file (multipart/form-data)
  
POST /api/chat - ask a question, about a file
params:
- document id
- question

# Getting started
1. Set OPENAI_API_KEY env variable to your API key.
2. mvn compile to build the code.
3. mvn exec:java to run the code.

Example cURLs:

curl --location 'http://localhost:8080/api/chat' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'documentId=db9299df-b07f-4329-8bc6-0a6d224271b6' \
--data-urlencode 'question=O czym jest dokument? streść'

curl --location 'http://localhost:8080/api/upload' \
--form 'file=@"/C:/Documents/test.txt"'
