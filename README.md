# Marketplace App
---

## App description

A microservices-driven Marketplace application built with Spring Boot and Docker.

#### Team members:
- Daria Tănasie 342C1 - @daria-tanasie
- Lache Alexandra 342C1 - @alex2004-l


## Build

To build the Docker images: 
```bash
docker compose build
```

To start the stack:
```bash
docker stack deploy -c docker-compose.yaml marketplaceapp
```

To remove the stack: 
```bash
docker stack rm marketplaceapp
```
