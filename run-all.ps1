Write-Host "Iniciando Consul..."
docker start consul
Start-Sleep -Seconds 5

Write-Host "Iniciando ms-producto..."
Start-Process cmd -ArgumentList "/c cd ms.producto && mvnw quarkus:dev"
Start-Sleep -Seconds 5

Write-Host "Iniciando ms-cliente..."
Start-Process cmd -ArgumentList "/c cd ms.cliente && mvnw quarkus:dev"
Start-Sleep -Seconds 5

Write-Host "Iniciando ms-ventas..."
Start-Process cmd -ArgumentList "/c cd ms.ventas && mvnw quarkus:dev"
Start-Sleep -Seconds 5

Write-Host "Iniciando API Gateway..."
Start-Process cmd -ArgumentList "/c cd api.gateway && mvnw spring-boot:run"