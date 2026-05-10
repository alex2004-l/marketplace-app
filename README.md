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

## How to start a cluster with 1 manager and 2 workers (which worked for us)

Create a network:
```bash
docker network create --subnet=10.20.0.0/16 swarm-bridge
```

Initiate manager:
```bash
docker swarm init --advertise-addr 10.20.0.1
JOIN_TOKEN=$(docker swarm join-token worker -q)
```

Create workers:
```bash
for i in 1 2; do docker run -d --privileged --name worker-$i --hostname worker-$i --network swarm-bridge --ip 10.20.0.$((i+1)) docker:dind; done```
```

Workers join manager:
```bash
for i in 1 2; do docker exec worker-$i docker swarm join --advertise-addr 10.20.0.$((i+1)) --token $JOIN_TOKEN 10.20.0.1:2377; done
```

## Features implemented:
- Authenticate a user through Keycloak
- Add new products (by sellers)
- See the products available
- Search for products (by name)
- Sort products ascending/descending
- Edit/delete a product
- Add products to wishlist
- Add products to cart
- Delete products from cart
- Create an order with the current products in the cart
- Get the cart total cost
- Modify address/phone number
- Payment simulation for the order
- See the orders history
- Add ratings and reviews for products