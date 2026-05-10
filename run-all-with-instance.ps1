Write-Host "Iniciando Consul..."
docker start consul
Start-Sleep -Seconds 5

Write-Host "Iniciando ms-producto instancia 1 en puerto 8080..."
Start-Process cmd -ArgumentList "/c cd ms.producto && mvnw quarkus:dev"
Start-Sleep -Seconds 5

Write-Host "Iniciando ms-producto instancia 2 en puerto 8085..."
Start-Process cmd -ArgumentList "/c cd ms.producto && mvnw quarkus:dev -Dquarkus.http.port=8085"
Start-Sleep -Seconds 5

Write-Host "Iniciando ms-cliente en puerto 8082..."
Start-Process cmd -ArgumentList "/c cd ms.cliente && mvnw quarkus:dev"
Start-Sleep -Seconds 5

Write-Host "Iniciando ms-ventas en puerto 8083..."
Start-Process cmd -ArgumentList "/c cd ms.ventas && mvnw quarkus:dev"
Start-Sleep -Seconds 5

Write-Host "Iniciando API Gateway..."
Start-Process cmd -ArgumentList "/c cd api.gateway && mvnw spring-boot:run"