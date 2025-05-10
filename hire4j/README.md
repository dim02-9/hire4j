# hire4j

**Attention!** The current setup exposes environment variables in the `docker-compose.yml` file. This is not recommended for production use. Please consider using a more secure method for managing sensitive information.

## Setup

1. Clone the repository
```bash
git clone
```

2. Install maven packages
```bash
mvn install
```

3. Clean and build the project
```bash
mvn clean package -DskipTests
```

4. Run docker-compose
```bash
docker-compose up -d
```
