# SwaggerMock
Creating WireMock endpoints from OpenAPI (swagger) specifications.

Currently, WireMock stubs can be generated from swagger specs by running the application locally. 
Send a request to http://localhost:8080/wiremock/upload-spec with the swagger spec in the 
request body. The generated WireMock stubs can be viewed at http://localhost:9999/__admin/.

Future plans include creating a docker image and turning the project into a wiremock extension. 

## Docker container
Create image through  docker build -t swmock-app .  

Run contain via docker run -d --name swmock -p 8080:8080 -p 9999:9999 swmock-app