*URL Shortener Service*

Run with: mvn spring-boot:run

URLs:

http://localhost:8080/?shorten=http://www.google.com - generate short code
http://localhost:8080/<shortcode> - redirect to url
http://localhost:8080/<shortcode>+ - view url

Real instance deployed on AWS here: http://url-shortener-345064791.eu-west-1.elb.amazonaws.com

Package: mvn clean package

Execute from Spring Boot Jar: ./url-shortener-1.0.0-SNAPSHOT.jar

 