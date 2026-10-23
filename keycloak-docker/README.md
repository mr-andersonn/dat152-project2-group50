Starting Keycloak Server with pre-configured realm and user data for testing purposes
-------------------------------------------------

Run the following docker command in the root directory to start Keycloak Server in development mode:

```shell
docker run --name keycloak_unoptimized -p 8080:8080 \
        -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
        -v ./keycloak/imports:/opt/keycloak/data/import \
        quay.io/keycloak/keycloak:latest \
        start-dev --import-realm
```
Keycloak can then be accessed at: `http://localhost:8080`

Log in as the `admin` user to access the Keycloak Administration Console. Username is `admin` and password `admin`.

The [realm configuration file](keycloak/imports/DAT152-realm.json) will create a new realm called `DAT152` including clients (dat152oblig2), client scopes and 2 client roles (USER and ADMIN).

The [realm user configuration file](keycloak/imports/DAT152-users-0.json)  will create 3 users (`user1`. `user2` and `user3`). The default password for each user is the name (e.g., user1).


You can then obtain an auth JWT bearer token by using curl as below (Linux/Mac) or any other relevant client (e.g., Postman):

```shell
curl -X POST http://localhost:8080/realms/DAT152/protocol/openid-connect/token -d 'client_id=dat152oblig2' -d 'username=user1&password=user1&grant_type=password'
```
