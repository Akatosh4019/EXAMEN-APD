# Postman - Proyecto Quarkus Gateway, CRUD y Saga

Importa:

1. `saga-quarkus.postman_environment.json`
2. `saga-quarkus.postman_collection.json`

Selecciona el environment **Saga Quarkus Docker Local**.

Las URLs ya estan escritas completas en la coleccion. Solo debes cambiar variables como `cliente_id`, `producto_id`, `cantidad` o `token` si lo necesitas.

## Orden simple

1. `00 - LOGIN / Login admin por gateway - guarda token`
2. `01 - HEALTH`
3. `02 - CRUD CLIENTES por Gateway`
4. `03 - CRUD PRODUCTOS por Gateway`
5. `04 - CRUD VENTAS por Gateway`
6. `05 - SAGA EXITOSA por Gateway`
7. `06 - ERRORES FUNCIONALES por Gateway`
8. `07 - COMPENSACION SAGA`

## Compensacion

Para probar compensacion:

```powershell
docker stop postgres-local
```

Ejecuta `07 - COMPENSACION SAGA / Simular falla despues del descuento`.

Luego:

```powershell
docker start postgres-local
```

Logs utiles:

```powershell
docker logs ms-ventas --tail 120
docker logs ms-producto --tail 80
```
