##Install 
cd auth-service
helm install otus-auth helm

cd gateway-service
helm install spring-gateway helm

cd user-service
helm install otus-user helm