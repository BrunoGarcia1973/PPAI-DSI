# Instrucciones para Configurar la Base de Datos PostgreSQL

## Problema Actual

La aplicación está configurada para usar PostgreSQL pero la base de datos no está disponible. Esto causa que la aplicación no pueda iniciar.

## Solución: Configurar PostgreSQL

### Opción 1: Instalar y Configurar PostgreSQL Localmente

1. **Instalar PostgreSQL** (si no lo tienes):
   - Descarga desde: https://www.postgresql.org/download/
   - O usa un instalador como PostgreSQL Installer para Windows

2. **Crear la Base de Datos**:
   ```sql
   CREATE DATABASE red_sismica;
   ```

3. **Ejecutar el Script DDL**:
   - Abre psql o pgAdmin
   - Conecta a la base de datos `red_sismica`
   - Ejecuta el script: `src/main/resources/data/ddl.sql`

4. **Verificar Credenciales**:
   - Edita `src/main/resources/application.properties`
   - Verifica que las credenciales sean correctas:
     ```properties
     spring.datasource.username=postgres
     spring.datasource.password=tu_contraseña
     ```

5. **Cambiar Configuración de JPA**:
   - En `application.properties`, cambia:
     ```properties
     spring.jpa.hibernate.ddl-auto=validate
     ```
   - Esto validará que el esquema coincida con las entidades JPA

### Opción 2: Usar Base de Datos en Memoria (H2) para Desarrollo

Si quieres desarrollar sin instalar PostgreSQL, puedes usar H2 temporalmente:

1. **Agregar H2 al pom.xml**:
   ```xml
   <dependency>
       <groupId>com.h2database</groupId>
       <artifactId>h2</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

2. **Cambiar application.properties**:
   ```properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.jpa.hibernate.ddl-auto=create-drop
   ```

### Opción 3: Usar Docker (Recomendado)

Si tienes Docker instalado:

```bash
docker run --name postgres-ppai -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=red_sismica -p 5432:5432 -d postgres:14
```

Luego ejecuta el script DDL en el contenedor.

## Estado Actual de la Configuración

- ✅ **Entidades JPA**: Todas convertidas y configuradas
- ✅ **Repositorios JPA**: Todos creados y listos
- ✅ **Servicios**: Configurados para usar repositorios JPA
- ⚠️ **Base de Datos**: Requiere PostgreSQL corriendo

## Notas Importantes

1. El `DatosInicialesService` intentará inicializar datos cuando la BD esté disponible
2. Todos los repositorios están listos y funcionarán una vez que PostgreSQL esté configurado
3. El esquema de la BD está definido en `src/main/resources/data/ddl.sql`

## Comandos Útiles

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación (requiere PostgreSQL)
mvn javafx:run

# Ejecutar con Spring Boot (si no usas JavaFX)
mvn spring-boot:run
```

