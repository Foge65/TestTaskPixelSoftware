Запуск черз docker compose:

- создать в корне проекта .env file, записть в него креды. пример

```
spring_port=8084

postgres_host=localhost
postgres_port=5438
postgres_database=postgres
postgres_username=postgres
postgres_password=dbpass

redis_port=6379

elastic_host=elasticsearch
elastic_port_client=9200
elastic_port_node=9300
elastic_username=elastic
elastic_password=password

jwt_secret=4b36b362e25441463aa5c15a6dbc60fa789b5ddeaa6bdcc25dba8d466fea8935
jwt_lifetime=30m
```

- запускать командой из директории проекта

```
docker compose up --build -d
```
