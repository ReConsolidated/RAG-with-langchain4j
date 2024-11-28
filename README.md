This is a RAG that responds to questions based on a document provided by a user.

POST /api/upload - upload file (.txt or .pdf), receive document id
params:
- file (multipart/form-data)
  
POST /api/chat - ask a question, about a file
params:
- document id
- question
