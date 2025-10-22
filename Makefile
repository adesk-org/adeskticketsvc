.PHONY: drop-db run

drop-db:
	mvn org.flywaydb:flyway-maven-plugin:11.7.2:clean \
		-Dflyway.cleanDisabled=false \
		-Dflyway.url=jdbc:postgresql://localhost:5432/adeskdb \
		-Dflyway.user=adesk_admin \
		-Dflyway.password=adesk_password \
		-Dflyway.schemas=ticket

run:
	mvn spring-boot:run
