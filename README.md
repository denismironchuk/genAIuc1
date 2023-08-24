Description

Application allows to access https://restcountries.com/v3.1/al and provides several functions to operate with returned data:
1) filter by country name
2) filter by population
3) sort by country name
4) limit result

Local Run

to run application locally following steps should be performed:
1) clone following repository git@github.com:denismironchuk/genAIuc1.git
2) move to root of cloned repository
3) run following command: mvn spring-boot:run

Examples

http://localhost:8080/getCountries?countryName=st
http://localhost:8080/getCountries?population=1
http://localhost:8080/getCountries?sort=ascend
http://localhost:8080/getCountries?limit=10
http://localhost:8080/getCountries?countryName=st&population=1
http://localhost:8080/getCountries?countryName=st&population=1&sort=descend
http://localhost:8080/getCountries?countryName=st&population=1&sort=descend&limit=10
