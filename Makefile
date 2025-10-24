.PHONY: docker-build docker-run drop-db kafka-consumer run

docker-build:
	docker build -t adeskticketsvc:latest .

docker-run: docker-build
	docker run --rm -p 8080:8080 --name ticketsvc \
	 -e SPRING_PROFILES_ACTIVE=prod \
	 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/adeskdb?currentSchema=ticket \
	 -e SPRING_DATASOURCE_USERNAME=adesk_admin \
	 -e SPRING_DATASOURCE_PASSWORD=adesk_password \
	 -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
	 -e SPRING_FLYWAY_ENABLED=true \
	 adeskticketsvc

drop-db:
	mvn org.flywaydb:flyway-maven-plugin:11.7.2:clean \
		-Dflyway.cleanDisabled=false \
		-Dflyway.url=jdbc:postgresql://localhost:5432/adeskdb \
		-Dflyway.user=adesk_admin \
		-Dflyway.password=adesk_password \
		-Dflyway.schemas=ticket

kafka-consumer:
	kafka-console-consumer --bootstrap-server localhost:9092 --topic "ticket.events.v1"

run:
	mvn spring-boot:run
